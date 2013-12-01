package com.serdtsev;

public class Item {
  long id;
  long groupId;

  public Item(long id, long groupId) {
    this.id = id;
    this.groupId = groupId;
  }

  public long getId() {
    return id;
  }

  public long getGroupId() {
    return groupId;
  }

  @Override
  public String toString() {
    return "Item{" +
      "id=" + id +
      ", groupId=" + groupId +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item)) return false;

    Item item = (Item) o;

    return groupId == item.groupId && id == item.id;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (int) (groupId ^ (groupId >>> 32));
    return result;
  }
}
