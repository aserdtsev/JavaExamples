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

  public String getEmail() {
    return email;
  }

  String email;

  public Person(String name, Sex gender, LocalDate birthday, String email) {
    this.name = name;
    this.gender = gender;
    this.birthday = birthday;
    this.email = email;
  }

  public int getAge() {
    return Period.between(birthday, LocalDate.now()).getYears();
  }

  public void print() {
    System.out.println(name + ", " + gender + ", " + getAge());
  }
}
