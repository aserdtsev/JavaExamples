package com.serdtsev;

import java.util.*;

public class App {
  public static void main(String[] args) {
    System.out.println("Главный поток запущен.");

    Map<String, String> argMap = new HashMap<>();
    Arrays.asList(args).forEach(value -> putArgToMap(argMap, value));

    int groupCount = Integer.decode(argMap.get("groups"));
    int handlerCount = Integer.decode(argMap.get("handlers"));
    long itemCount = Integer.decode(argMap.get("items"));

    // Заполним таблицу.
    long idCount = 0;
    Set<Item> items = new HashSet<>();
    Random random = new Random();
    for (int i = 0; i < itemCount; i++) {
      int groupId = random.nextInt(groupCount);
      items.add(new Item(idCount, groupId));
      idCount++;
    }

    Dispatcher dispatcher = new Dispatcher();
    dispatcher.addItems(items);

    List<Handler> handlers = new ArrayList<>();

    // Запустим обработчики.
    for (int i = 0; i < handlerCount; i++) {
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

  private static void putArgToMap(Map<String, String> map, String value) {
    String[] pair = value.split(":");
    map.put(pair[0], pair[1]);
  }
}
