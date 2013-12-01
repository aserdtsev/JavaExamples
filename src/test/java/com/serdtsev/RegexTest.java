package com.serdtsev;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.*;

/**
 * User: Andrey Serdtsev
 */
public class RegexTest {

  /**
   * Пример выделения подстроки, заданной регулярным выражением.
   *
   * Есть строка в определенном формате. Например, "Безналичная оплата. Поставщик: МТС, номер 9139146667, сумма 10.0".
   * Нужно выделить из этой строки название поставщика. В данном случае это будет "МТС". В моей практике задача
   * встречается довольно часто. До того времени, пока еще не было регулярных выражений, задача решалась с помощью
   * метода indexOf класса String. Однако, теперь есть более элегантное решение, которое иллюстрируется данным примером.
   *
   * Подробнее: Г. Шилдт. Java. Методики программирования Шилдта. Стр. 50.
   */
  @Test
  public void selectSubstribg() {
    // Можно было бы вместо класса [a-zа-я0-9] использовать предопределенный символьный класс \w, но он не
    // воспринимает кириллицу.
    Pattern pattern = Pattern.compile("Поставщик:\\s*([a-zа-я0-9_\\-]+)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    String detailsOfPayment = "Безналичная оплата. Поставщик: МТС, номер 9139146667, сумма 10.0";
    Matcher matcher = pattern.matcher(detailsOfPayment);
    assertTrue(matcher.find());
    assertEquals("МТС", matcher.group(1));
  }


  /**
   * Пример экранирования специальных символов.
   *
   * Часто строка, введенная пользователем, используется как часть регулярного выражения. Например, пользователь
   * ввел квартплата, а мы ищем по регулярному выражению (?i).*квартплата.*. А что, если пользователь ввел регулярное
   * выражение? В этом случае нужно экранировать спецсимволы, которые используются в регулярных выражениях. Для этого
   * пользовательскую строку нужно экранировать вначале последовательностью \Q, а в конце - \E. То же самое делает
   * метод java.util.regex.Pattern.quote.
   */
  @Test
  public void testPatternQuote() {
    List<String> services = new ArrayList<>();
    services.add("Сотовая связь - МТС");
    services.add("Сотовая связь - Билайн");
    services.add("Интернет - Новотелеком");

    String userSearchStr = "с.*ь";
    String searchStr= Pattern.quote("(?i).*" + userSearchStr + ".*");

    boolean found = false;
    for (String service : services) {
      found = found || service.matches(searchStr);
    }

    assertFalse(found);
  }
}
