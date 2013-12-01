package com.serdtsev;

import java.util.*;

public class ProcessingDemo {
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

    List<Processor> processors = new ArrayList<Processor>();

    // Запустим обработчики.
    for (int i = 0; i < processorCount; i++) {
      Processor processor = new Processor(dispatcher);
      processor.start();
      processors.add(processor);
    }

    boolean isAlive;
    do {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      isAlive = false;
      for (Processor processor : processors) {
        isAlive = isAlive || processor.isAlive();
      }
    } while (isAlive);

    System.out.println("Главный поток завершен");
  }
}
