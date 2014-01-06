package com.serdtsev;

import java.util.ArrayList;
import java.util.List;

public class Handler extends Thread {
  Dispatcher dispatcher;
  List<Item> processedItems = new ArrayList<>();
  int groupId;

  public Handler(Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  @Override
  public void run() {
    System.out.println("Поток " + getId() + " запущен.");
    List<Item> items;
    do {
      items = dispatcher.getNextItems(getId(), groupId);
      for (Item item : items) {
        groupId = item.getGroupId();
        processItem(item);
      }
      dispatcher.unlockGroup(this.getId());
    } while (!items.isEmpty());
    System.out.println("Поток " + getId() + " завершил работу.");
  }

  private void processItem(Item item) {
    item.setProcessingInfo(this);
    System.out.println("Поток " + getId() + " обработал " + item.toString() + ".");
    processedItems.add(item);
  }

  public List<Item> getProcessedItems() {
    return processedItems;
  }
}
