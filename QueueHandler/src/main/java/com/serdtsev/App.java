package com.serdtsev;

import java.util.*;

public class App {
  public static void main(String[] args) {
    System.out.println("Главный поток запущен.");

    int groupCount = 10;
    int processorCount = 7;
    long itemCount = 50;

    // Заполним таблицу.
    long idCount = 0;
    Set<Item> items = new HashSet<Item>();
    Random random = new Random();
    for (int i = 0; i < itemCount; i++) {
      int groupId = random.nextInt(groupCount);
      items.add(new Item(idCount, groupId));
      idCount++;
    }

    Dispatcher dispatcher = new Dispatcher();
    dispatcher.addItems(items);

    List<Handler> handlers = new ArrayList<Handler>();

    // Запустим обработчики.
    for (int i = 0; i < processorCount; i++) {
      Handler handler = new Handler(dispatcher);
      handler.start();
      handlers.add(handler);
    }

    boolean isAlive;
    do {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      isAlive = false;
      for (Handler handler : handlers) {
        isAlive = isAlive || handler.isAlive();
      }
    } while (isAlive);

    System.out.println("Главный поток завершен");
  }
}
