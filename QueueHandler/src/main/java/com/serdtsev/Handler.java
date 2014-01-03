package com.serdtsev;

import java.util.ArrayList;
import java.util.List;

public class Handler extends Thread {
  Dispatcher dispatcher;
  List<Item> processedItems = new ArrayList<>();

  public Handler(Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  @Override
  public void run() {
    System.out.println("Поток " + getId() + " запущен.");
    List<Item> items;
    do {
      items = dispatcher.getNextItems(getId());
      for (Item item : items) {
        processItem(item);
      }
    } while (!items.isEmpty());
    System.out.println("Поток " + getId() + " завершил работу.");
  }

  private void processItem(Item item) {
    item.setProcessingInfo(this);
    System.out.println("Поток " + getId() + " обработал " + item.toString() + ".");
    processedItems.add(item);
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public List<Item> getProcessedItems() {
    return processedItems;
  }
}
