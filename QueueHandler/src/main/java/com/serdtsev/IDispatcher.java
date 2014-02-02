package com.serdtsev;

import java.util.List;

/**
 * User: Andrey Serdtsev
 */
public interface IDispatcher {
  void addItem(Item item);

  void unlockGroup();

  /**
   * Возвращает список элементов одной группы для обработки потоком. Выбранную группу блокирует.
   */
  List<Item> getNextItems();

  boolean hasItems();

  void finish();
}
