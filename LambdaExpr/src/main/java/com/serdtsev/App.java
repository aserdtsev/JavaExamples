package com.serdtsev;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    printPersonsWithPredicate();
  }

  /**
   * Печатает список людей, которые соответствуют критерию: женщины от 18 до 25 лет. Критерий задан реализацией
   * интерфейса Predicate с единственным методом test(). В первом подходе используется анонимный класс. Во втором -
   * лямбда-выражение. Следует учитывать, что использование лямбда-выражения в данном контексте возможно только в том
   * случае, если интерфейс содержит единственный метод.
   *
   * Пример взят из http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html.
   */
  private static void printPersonsWithPredicate() {
    List<Person> roster = new ArrayList<>();
    roster.add(new Person("Serdtsev Andrey", Person.Sex.MALE, LocalDate.of(1970, 4, 5), "andrey.serdtsev@gmail.com"));
    roster.add(new Person("Serdtseva Oksana", Person.Sex.FEMALE, LocalDate.of(1972, 5, 2), "oserdtseva@xxx.ru"));
    roster.add(new Person("Kharenko Anna", Person.Sex.FEMALE, LocalDate.of(1991, 4, 4), "akharenko@xxx.ru"));
    roster.add(new Person("Serdtseva Nadezda", Person.Sex.FEMALE, LocalDate.of(1995, 5, 20), "nserdtseva@xxx.ru"));

    System.out.println("Печатаем с помощью ананимного класса:");
    processPersons(
        roster,
        new Predicate<Person>() {
          @Override
          public boolean test(Person person) {
            return person.gender == Person.Sex.FEMALE && person.getAge() >= 18 && person.getAge() <= 25;
          }
        },
        new Consumer<Person>() {
          @Override
          public void accept(Person person) {
            person.print();
          }
        }
    );

    System.out.println("А теперь печатаем с использованием лямбда-выражения:");
    processPersons(
        roster,
        p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25,
        p -> p.print());

    System.out.println("Теперь печатаем email:");
    processPersonsWithFunction(
        roster,
        p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25,
        p -> p.getEmail(),
        email -> System.out.println(email)
    );
  }

  static void processPersons(List<Person> roster, Predicate<Person> tester, Consumer<Person> block) {
    for (Person person: roster) {
      if (tester.test(person)) {
        block.accept(person);
      }
    }
  }

  static void processPersonsWithFunction(
      List<Person> roster, Predicate<Person> tester, Function<Person, String> mapper, Consumer<String> block) {
    for (Person person: roster) {
      if (tester.test(person)) {
        String data = mapper.apply(person);
        block.accept(data);
      }
    }
  }
}
