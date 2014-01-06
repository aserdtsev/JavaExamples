package com.serdtsev;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher {
  // Размер пакета элементов, который передается на обработку методом getNextItems.
  private int packetSize;
  // Группы с очередями необработанных элементов.
  private SortedMap<Long, BlockingQueue<Item>> groups;
  // Пары [Идентификатор потока]-[Номер группы]. Если пара присутствует в карте, то поток обрабатывает группу.
  private Map<Long, Long> groupLocks;
  private Comparator<Item> itemComparator;

  public Dispatcher(int packetSize) {
    this.packetSize = packetSize;
    groups = new TreeMap<>();
    groupLocks = new HashMap<>();
    itemComparator = (o1, o2) -> {
      if (o1.getId() < o2.getId()) {
        return -1;
      } else if (o1.getId() > o2.getId()) {
        return 1;
      }
      return 0;
    };
  }

  /**
   * Подготавливает элементы к обработке, распределяя по группам.
   */
  public void addItems(Set<Item> items) {
    final int initialCapacity = 100;

    for (Item item : items) {
      long groupId = item.getGroupId();
      BlockingQueue<Item> groupItems = groups.get(groupId);
      if (groupItems == null) {
        groupItems = new PriorityBlockingQueue<>(initialCapacity, itemComparator);
        groups.put(groupId, groupItems);
      }
      groupItems.add(item);
    }
  }

  /**
   * Возвращает список элементов одной группы для обработки потоком. Предыдущую группу потока разблокирует,
   * выбранную группу блокирует, удаляя и добавляя в groupLocks соответственно.
   */
  public synchronized List<Item> getNextItems(long threadId) {
    Long lastGroupId = groupLocks.get(threadId);
    if (lastGroupId != null) {
      groupLocks.remove(threadId);
      System.out.println("Диспетчер разблокировал группу " + lastGroupId + " (поток " + threadId + ")");
    }

    List<Item> items = new ArrayList<>();
    Iterator<Map.Entry<Long, BlockingQueue<Item>>> iterator = groups.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Long, BlockingQueue<Item>> entry = iterator.next();
      Long groupId = entry.getKey();
      if (!groupLocks.containsValue(groupId) && !groupId.equals(lastGroupId)) {
        groupLocks.put(threadId, groupId);
        System.out.println("Диспетчер заблокировал группу " + groupId + " (поток " + threadId + ")");
        Queue<Item> queue = entry.getValue();
        int count = 0;
        while (!queue.isEmpty() && count < packetSize) {
          Item item = queue.poll();
          if (item != null) {
            items.add(item);
            count++;
          } else {
            // Удаляем группу с пустой очередью из groups.
            iterator.remove();
          }
        }
        break;
      }
    }

    return items;
  }
}
