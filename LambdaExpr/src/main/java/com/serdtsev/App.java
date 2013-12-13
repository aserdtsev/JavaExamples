package com.serdtsev;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Лямбда-выражения позволяют подавать в параметры методов выражения, которые можно выполнять в теле метода. Раньше
 * подобная функциональность реализовалась с помощью анонимных классов. Выглядит это чудовищно. Лямбда-выражения
 * делают то же самое на порядок выразительнее и компактнее.
 *
 * Использованы примеры из http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html.
 */
public class App {
  public static void main(String[] args) {
    printPersonsWithPredicate();
  }

  /**
   * Печатает список людей, которые соответствуют критерию: женщины от 18 до 25 лет. Критерий задан реализацией
   * интерфейса Predicate с единственным методом test(). В первом подходе используется анонимный класс. В последующих -
   * лямбда-выражения.
   */
  private static void printPersonsWithPredicate() {
    List<Person> roster = new ArrayList<>();
    roster.add(new Person("Serdtsev Andrey", Person.Sex.MALE, LocalDate.of(1970, 4, 5), "andrey.serdtsev@gmail.com"));
    roster.add(new Person("Serdtseva Oksana", Person.Sex.FEMALE, LocalDate.of(1972, 5, 2), "oserdtseva@xxx.ru"));
    roster.add(new Person("Kharenko Anna", Person.Sex.FEMALE, LocalDate.of(1991, 4, 4), "akharenko@xxx.ru"));
    roster.add(new Person("Serdtseva Nadezda", Person.Sex.FEMALE, LocalDate.of(1995, 5, 20), "nserdtseva@xxx.ru"));

    System.out.println("\nПечатаем с помощью ананимного класса:");
    processPersons(
        roster,
        new Predicate<Person>() {
          @Override
          public boolean test(Person p) {
            return p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25;
          }
        },
        new Consumer<Person>() {
          @Override
          public void accept(Person person) {
            person.print();
          }
        }
    );

    System.out.println("\nА теперь печатаем с использованием лямбда-выражения:");
    processPersons(
        roster,
        p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25,
        p -> p.print());

    System.out.println("\nТеперь печатаем email:");
    processPersonsWithFunction(
        roster,
        p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25,
        p -> p.getEmail(),
        email -> System.out.println(email)
    );

    System.out.println("\nСнова печатаем email в функциональном стиле:");
    roster.stream()
        .filter(p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25)
        .map(p -> p.getEmail())
        .forEach(email -> System.out.println(email));

    System.out.println("\nЕсли вспомнить о задаче, получится еще короче:");
    roster.stream()
        .filter(p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25)
        .forEach(p -> System.out.println(p.getEmail()));

    System.out.println("\nМожно и так:");
    List<Person> filterPersons = roster.stream()
        .filter(p -> p.gender == Person.Sex.FEMALE && p.getAge() >= 18 && p.getAge() <= 25)
        .collect(Collectors.toList());
    filterPersons.forEach(p -> System.out.println(p.getEmail()));
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
