package com.serdtsev;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Andrey Serdtsev
 */
public class AppTest {
  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testMain() throws Exception {
    String[] args = new String[] {"groups:10", "handlers:7", "items:50"};
    App.main(args);
  }
}
