package com.serdtsev;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher {
  private int groupsNum;
  // Размер пакета элементов, который передается на обработку методом getNextItems.
  private int packetSize;
  // Группы с очередями необработанных элементов.
  private SortedMap<Integer, BlockingQueue<Item>> groups;
  // Пары [Идентификатор потока]-[Номер группы]. Если пара присутствует в карте, то поток обрабатывает группу.
  private Map<Long, Integer> groupLocks;

  public Dispatcher(int groupsNum, int packetSize) {
    this.groupsNum = groupsNum;
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
    groups.forEach((groupId, groupItems) -> {
      System.out.println("Группа " + groupId + ":");
      groupItems.forEach(System.out::println);
    });
  }

  public void lockGroup(long threadId, int groupId) {
    groupLocks.put(threadId, groupId);
    System.out.println("Lock group " + groupId + " (thread " + threadId + ")");
  }

  public void unlockGroup(long threadId) {
    Integer groupId = groupLocks.get(threadId);
    groupLocks.remove(threadId);
    System.out.println("Unlock group " + groupId + " (thread " + threadId + ")");
  }

  /**
   * Возвращает список элементов одной группы для обработки потоком. Предыдущую группу потока разблокирует,
   * выбранную группу блокирует, удаляя и добавляя в groupLocks соответственно.
   */
  public synchronized List<Item> getNextItems(long threadId, int lastGroupId) {
    List<Item> result = new ArrayList<>();
    Integer groupId = lastGroupId;
    do {
      groupId = Math.floorMod((groupId != null) ? groupId+1 : 0, groupsNum);
      BlockingQueue<Item> queue = groups.get(groupId);
      if (!groupLocks.containsValue(groupId) && !queue.isEmpty()) {
        lockGroup(threadId, groupId);
        int count = 0;
        while (!queue.isEmpty() && count < packetSize) {
          Item item = queue.poll();
          if (item != null) {
            result.add(item);
            count++;
          }
        }
      }
    } while (result.isEmpty() && !groupId.equals(lastGroupId));

    return result;
  }
}
