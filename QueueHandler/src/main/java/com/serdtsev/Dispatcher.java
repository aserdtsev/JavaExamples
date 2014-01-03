package com.serdtsev;

import java.util.*;

public class Dispatcher {
  private SortedMap<Long, Queue<Item>> groups;
  private Map<Long, Long> groupLocks;
  private Comparator<Item> itemComparator;

  public Dispatcher() {
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

  public synchronized void addItems(Set<Item> items) {
    final int initialCapacity = 100;

    for (Item item : items) {
      long groupId = item.getGroupId();
      Queue<Item> groupItems = groups.get(groupId);
      if (groupItems == null) {
        groupItems = new PriorityQueue<>(initialCapacity, itemComparator);
        groups.put(groupId, groupItems);
      }
      groupItems.add(item);
    }
  }

  public synchronized List<Item> getNextItems(long threadId) {
    Long lastGroupId = groupLocks.get(threadId);
    if (lastGroupId != null) {
      groupLocks.remove(threadId);
      System.out.println("Диспетчер разблокировал группу " + lastGroupId + " (поток " + threadId + ")");
    }

    List<Item> itemList = new ArrayList<>();
    Iterator<Map.Entry<Long, Queue<Item>>> iterator = groups.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Long, Queue<Item>> entry = iterator.next();
      Long groupId = entry.getKey();
      if (!groupLocks.containsValue(groupId) && !groupId.equals(lastGroupId)) {
        Queue<Item> items = entry.getValue();
        Item item = items.poll();
        if (item != null) {
          itemList.add(item);
          groupLocks.put(threadId, groupId);
          System.out.println("Диспетчер заблокировал группу " + groupId + " (поток " + threadId + ")");
          break;
        } else {
          iterator.remove();
        }
      }
    }

    return itemList;
  }
}
