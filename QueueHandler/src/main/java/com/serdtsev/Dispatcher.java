package com.serdtsev;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher {
  // Размер пакета элементов, который передается на обработку методом getNextItems.
  private int packetSize;
  // Группы с очередями необработанных элементов.
  private SortedMap<Integer, BlockingQueue<Item>> groups;
  // Пары [Идентификатор потока]-[Номер группы]. Если пара присутствует в карте, то поток обрабатывает группу.
  private Map<Long, Integer> groupLocks;

  public Dispatcher(int groupsNum, int packetSize) {
    this.packetSize = packetSize;
    groups = new TreeMap<>();
    groupLocks = new HashMap<>();
    Comparator<Item> itemComparator = (o1, o2) -> {
      if (o1.getId() < o2.getId()) {
        return -1;
      } else if (o1.getId() > o2.getId()) {
        return 1;
      }
      return 0;
    };

    final int initialCapacity = 100;
    for (int groupId = 0; groupId < groupsNum; groupId++) {
      BlockingQueue<Item> groupItems = new PriorityBlockingQueue<>(initialCapacity, itemComparator);
      groups.put(groupId, groupItems);
    }
  }

  /**
   * Подготавливает элементы к обработке, распределяя по группам.
   */
  public void addItems(Set<Item> items) {
    for (Item item : items) {
      groups.get(item.getGroupId()).add(item);
    }
  }

  /**
   * Возвращает список элементов одной группы для обработки потоком. Предыдущую группу потока разблокирует,
   * выбранную группу блокирует, удаляя и добавляя в groupLocks соответственно.
   */
  public synchronized List<Item> getNextItems(long threadId) {
    Integer lastGroupId = groupLocks.get(threadId);
    if (lastGroupId != null) {
      groupLocks.remove(threadId);
      System.out.println("Диспетчер разблокировал группу " + lastGroupId + " (поток " + threadId + ")");
    }

    List<Item> items = new ArrayList<>();
    Iterator<Map.Entry<Integer, BlockingQueue<Item>>> iterator = groups.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Integer, BlockingQueue<Item>> entry = iterator.next();
      Integer groupId = entry.getKey();
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
