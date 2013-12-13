package com.serdtsev;

import java.util.*;

public class App {
  private static Set<Item> items = new HashSet<>();
  public static void main(String[] args) {
    System.out.println("Главный поток запущен.");

    Map<String, String> argMap = new HashMap<>();
    Arrays.asList(args).forEach(value -> argMap.put(value.split(":")[0], value.split(":")[1]));

    int groupsNum = Integer.decode(argMap.get("groups"));
    int handlersNum = Integer.decode(argMap.get("handlers"));
    long itemsNum = Integer.decode(argMap.get("items"));

    fillItems(groupsNum, itemsNum, items);

    Dispatcher dispatcher = new Dispatcher();
    dispatcher.addItems(items);

    List<Handler> handlers = new ArrayList<>();

    // Запустим обработчики.
    int i = 0;
    while (i++ < handlersNum) {
      handlers.add(new Handler(dispatcher));
    }
    handlers.parallelStream().forEach(h -> h.start());

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

  private static void fillItems(int groupsNum, long itemsNum, Set<Item> items) {
    Random random = new Random();
    long id = 0;
    while (id < itemsNum) {
      items.add(new Item(id++, random.nextInt(groupsNum)));
    }
  }
}
