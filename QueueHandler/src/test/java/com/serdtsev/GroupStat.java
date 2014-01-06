package com.serdtsev;

/**
 * User: Andrey Serdtsev
 */
public class GroupStat {
  int id;
  long itemCount;
  final Item firstItem;
  Item lastItem;

  public GroupStat(int id, Item firstItem) {
    this.id = id;
    this.itemCount = 1;
    this.firstItem = firstItem;
    this.lastItem = firstItem;
  }

  @Override
  public String toString() {
    return "GroupStat{" +
        "id=" + id +
        ", itemCount=" + itemCount +
        ", firstTime=" + firstItem.getWhenWasProcessed() +
        ", lastTime=" + lastItem.getWhenWasProcessed() +
        '}';
  }

  public void  putItem(Item item) {
    if (lastItem.getWhenWasProcessed().isAfter(item.getWhenWasProcessed())) {
      throw new IllegalArgumentException("Элемент обработан раньше предыдущего");
    }
    if (item.getGroupId() != id) {
      throw new IllegalArgumentException("Некорректная группа элемента");
    }
    if (item.getId() < lastItem.getId()) {
      throw new IllegalArgumentException("Некорректная последовательность обработки элементов:\n" + item +
          " обработан позже " + lastItem);
    }
    lastItem = item;
    itemCount++;
  }
}
