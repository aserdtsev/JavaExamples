package com.serdtsev;

import java.time.LocalDate;
import java.time.Period;

/**
 * User: Andrey Serdtsev
 */
public class Person {
  public enum Sex {
    MALE, FEMALE
  }

  String name;
  LocalDate birthday;
  Sex gender;

  public Person(String name, Sex gender, LocalDate birthday) {
    this.name = name;
    this.gender = gender;
    this.birthday = birthday;
  }

  public int getAge() {
    return Period.between(birthday, LocalDate.now()).getYears();
  }

  public void print() {
    System.out.println(name + ", " + gender + ", " + getAge());
  }
}
