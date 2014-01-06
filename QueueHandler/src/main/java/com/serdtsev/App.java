package com.serdtsev;

import java.util.*;

/**
 * Задание
 *
 * Имеется очередь элементов на обработку. Каждый элемент имеет собственный идентификатор (itemId) и принадлежит к
 * некоторой группе (groupId). Внутри группы элементы должны обрабатываться строго последовательно, в порядке
 * увеличения идентификаторов элементов. Элементы разных групп могут обрабатываться параллельно. Обработка элемента
 * производится путем вызова некоторого метода с параметрами itemId и groupId, который печатает полученные
 * идентификаторы элементов. Элементы в очередь добавляются асинхронно внешним процессом. После обработки элемент
 * должен быть удален из очереди.

 * Написать обработчик очереди, работающий в несколько потоков. Максимальное количество потоков ограничено, задается
 * при старте обработчика и в общем случае меньше числа групп. Обеспечить равномерную обработку групп элементов:
 * наличие в очереди групп с большим количеством элементов не должно приводить к длительным задержкам в обработке
 * других групп.
 */

public class App {
  private static Set<Item> items = new HashSet<>();
  private static List<Handler> handlers = new ArrayList<>();

  public static void main(String[] args) {
    System.out.println("Главный поток запущен.");

    Map<String, String> argMap = new HashMap<>();
    Arrays.asList(args).forEach(value -> argMap.put(value.split(":")[0], value.split(":")[1]));

    int groupsNum = Integer.decode(argMap.get("groups"));
    int handlersNum = Integer.decode(argMap.get("handlers"));
    long itemsNum = Integer.decode(argMap.get("items"));
    int packetSize = Integer.decode(argMap.get("packetSize"));

    fillItems(groupsNum, itemsNum, items);

    Dispatcher dispatcher = new Dispatcher(groupsNum, packetSize);
    dispatcher.addItems(items);

    // Запустим обработчики.
    int i = 0;
    while (i++ < handlersNum) {
      handlers.add(new Handler(dispatcher));
    }
    handlers.parallelStream().forEach(Handler::start);

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
    while (items.size() < itemsNum) {
      items.add(new Item(random.nextInt((int)itemsNum*2), random.nextInt(groupsNum)));
    }
  }

  public static SortedSet<Item> getProcessedItems() {
    Comparator<Item> comparator = (o1, o2) -> {
      if (o1.getWhenWasProcessed().isBefore(o2.getWhenWasProcessed())) {
        return -1;
      } else {
        return 1;
      }
    };
    SortedSet<Item> result = new TreeSet<>(comparator);

    for (Handler handler : handlers) {
      result.addAll(handler.getProcessedItems());
    }

    return result;
  }

}
