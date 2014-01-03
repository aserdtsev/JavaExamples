package com.serdtsev;

import java.util.Currency;
import java.util.Locale;

/**
 * Hello world!
 *
 */
public class App {
  public static void main( String[] args ) {
    System.out.println( "Hello World!" );
    Locale locale =Locale.getDefault();
    System.out.println(Currency.getInstance(locale).getDisplayName(locale));
  }
}
