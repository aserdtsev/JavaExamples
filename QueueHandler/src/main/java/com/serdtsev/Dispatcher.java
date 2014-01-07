package com.serdtsev;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dispatcher {
  private int groupsNum;
  // Размер пакета элементов, который передается на обработку методом getNextItems.
  private int packetSize;
  // Группы с очередями необработанных элементов.
  private SortedMap<Integer, BlockingQueue<Item>> groups;
  // Пары [Идентификатор группы]-[Блокировка]. Смысла делать потоко-безопасным нет - коллекцию инициализируем
  // в конструкторе, далее ее состав не меняется.
  private Map<Integer, Lock> groupLocks;
  private ConcurrentMap<Thread, Integer> threads;
  private AtomicBoolean hasItems = new AtomicBoolean(false);

  public Dispatcher(int groupsNum, int packetSize) {
    this.groupsNum = groupsNum;
    this.packetSize = packetSize;
    groups = new TreeMap<>();
    Comparator<Item> itemComparator = (o1, o2) -> {
      if (o1.getId() < o2.getId()) {
        return -1;
      } else if (o1.getId() > o2.getId()) {
        return 1;
      }
      return 0;
    };
    final int initialCapacity = 100;
    groupLocks = new HashMap<>();
    for (int groupId = 0; groupId < groupsNum; groupId++) {
      BlockingQueue<Item> groupItems = new PriorityBlockingQueue<>(initialCapacity, itemComparator);
      groups.put(groupId, groupItems);

      groupLocks.put(groupId, new ReentrantLock());
    }
    threads = new ConcurrentHashMap<>();
  }

  /**
   * Подготавливает элементы к обработке, распределяя по группам.
   */
  public void addItems(Set<Item> items) {
    hasItems.set(!items.isEmpty());
    for (Item item : items) {
      groups.get(item.getGroupId()).add(item);
    }
    groups.forEach((groupId, groupItems) -> {
      System.out.println("Группа " + groupId + ":");
      groupItems.forEach(System.out::println);
    });
  }

  public void unlockGroup() {
    Integer groupId = threads.get(Thread.currentThread());
    Lock lock = groupLocks.get(groupId);
    System.out.println(LocalTime.now() + " [" + Thread.currentThread().getName() + "] unlock group " + groupId);
    lock.unlock();
  }

  /**
   * Возвращает список элементов одной группы для обработки потоком. Выбранную группу блокирует.
   */
  public List<Item> getNextItems() {
    List<Item> result = new ArrayList<>();
    Integer lastGroupId = threads.get(Thread.currentThread());
    if (lastGroupId == null) {
      lastGroupId = groupsNum-1;
    }
    Integer groupId = lastGroupId;
    boolean localHasItems = false;
    do {
      groupId = Math.floorMod(groupId+1, groupsNum);

      BlockingQueue<Item> queue = groups.get(groupId);
      Lock lock = groupLocks.get(groupId);
      System.out.println(LocalTime.now() + " [" + Thread.currentThread().getName() + "] try group " + groupId +
          ": queue.isEmpty()=" + queue.isEmpty());
      localHasItems = localHasItems || !queue.isEmpty();
      if (!queue.isEmpty() && lock.tryLock()) {
        System.out.println(LocalTime.now() + " [" + Thread.currentThread().getName() + "] lock group " + groupId);
        int count = 0;
        while (!queue.isEmpty() && count < packetSize) {
          Item item = queue.poll();
          if (item != null) {
            result.add(item);
            count++;
          }
        }
        threads.put(Thread.currentThread(), groupId);
      }
    } while (result.isEmpty() && !groupId.equals(lastGroupId));

    if (!localHasItems) {
      hasItems.set(groups.entrySet().parallelStream().anyMatch((e) -> !e.getValue().isEmpty()));
    }

    return result;
  }

  public boolean hasItems() {
    return hasItems.get();
  }
}
