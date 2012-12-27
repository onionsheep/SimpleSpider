package org.cong.spider.test;

import org.apache.http.Header;
import org.cong.spider.Spider;
import org.cong.spider.SpiderManager;

public class TestSpider {

  public static void main(final String[] args) {
//    Test.t0();
    t1();
  }

  public static void t0() {
    final Spider s = new Spider(null, "http://www.qq.com");
    final String str = s.get().getFirstHeader("Content-Type").getValue();
    System.out.println(str);
    for (final Header i : s.getResponse().getAllHeaders()) {
      System.out.println(i.getName() + "\t" + i.getValue());
    }
  }
  
  public static void t1(){
    SpiderManager sm = new SpiderManager();
    sm.start();
  }
}
