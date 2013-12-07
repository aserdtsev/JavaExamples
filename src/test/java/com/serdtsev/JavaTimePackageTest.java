package com.serdtsev;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.Assert.assertEquals;

/**
 * User: Andrey Serdtsev
 */
public class JavaTimePackageTest {
  /**
   * Показывает, как вычислить возраст, зная дату рождения.
   */
  @Test
  public void calcAge() {
    LocalDate birthday = LocalDate.of(1970, 04, 05);
    LocalDate now = LocalDate.of(2013, 12, 07);
    Period age = Period.between(birthday, now);
    assertEquals(43, age.getYears());
  }
}
