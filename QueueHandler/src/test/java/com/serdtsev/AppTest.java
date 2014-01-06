package com.serdtsev;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

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
    String[] args = new String[] {"groups:10", "handlers:7", "items:50", "packetSize:3"};
    App.main(args);

    SortedSet<Item> items = App.getProcessedItems();
    Comparator<Integer> comparator = (o1, o2) -> o1-o2;
    Map<Integer, GroupStat> groups = new TreeMap<>(comparator);
    items.forEach(item -> {
      GroupStat stat = groups.get(item.getGroupId());
      if (stat == null) {
        stat = new GroupStat(item.getGroupId(), item);
      } else {
        stat.putItem(item);
      }
      groups.put(item.getGroupId(), stat);
    });

    groups.forEach((id, stat) -> System.out.println(stat));

    items.forEach(System.out::println);


  }
}
