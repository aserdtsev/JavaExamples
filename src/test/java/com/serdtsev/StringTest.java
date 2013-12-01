package com.serdtsev;

import org.junit.*;
import static junit.framework.Assert.*;

/**
 * User: Andrey Serdtsev
 */
public class StringTest {
  /**
   * Показывает, как удалить из строки html-теги. Применяется для фильтрации пользовательского ввода, чтобы
   * предотвратить атаку с целью выполнить в браузере код JavaScript.
   * @throws Exception
   */
  @Test
  public void testReplaceAllForRemoveTags() throws Exception {
    String userInput = "<javascript>Зловредный код</javascript>";
    String filteredInput = userInput.replaceAll("<[^>]*>", "");
    assertTrue(filteredInput.indexOf("<") == 0);
    assertTrue(filteredInput.indexOf(">") == 0);
  }
}
