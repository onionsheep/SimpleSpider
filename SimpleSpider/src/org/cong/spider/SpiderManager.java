package org.cong.spider;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

public class SpiderManager {
  private static Logger   logger = LogManager.getLogger(SpiderManager.class);
  protected Set<Pattern>  ignorePatterns;
  protected Set<Pattern>  matchPatterns;
  protected Paser         paser;
  protected int           spiderNum;
  protected Queue<String> toVisitURL;
  protected Set<String>   visitedURL;
  protected Set<String>   visitingURL;

  public static int i = 0;
  
  public SpiderManager() {
    initData();
  }

  protected void initData() {
    this.toVisitURL = new LinkedList<>();
    this.toVisitURL.add("http://www.qq.com");
    this.toVisitURL.add("http://www.163.com");
    this.toVisitURL.add("http://www.taobao.com");
    
    final String p0 = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
    this.matchPatterns = new HashSet<>();
    this.matchPatterns.add(Pattern.compile(p0));
    ignorePatterns = null;
    paser = new Paser();
    spiderNum = 10;
    visitedURL = new HashSet<>();
    visitingURL = new HashSet<>();
    visitedURL.add(null);
  }

  /**
   * 蜘蛛完成抓取之后调用管理器的完成方法
   * 
   * @param spider
   */
  protected void complete(final Spider spider) {
    String currentURL = spider.getUrl();
    this.sRemove(this.visitingURL, currentURL);
    final HttpEntity entity = spider.getEntity();
    if (entity != null) {
      // 处理entity,得到所有链接
      LinkedList<String> urls = getAllLinks(spider);
      // 记录页面url和页面中的url的关系

      urls = new UrlFilter(this.matchPatterns, null, null).doFilter(urls);
      
      StringBuilder sb = new StringBuilder(100);
      for(String u : urls){
        sb.append(currentURL);
        sb.append(" ");
        sb.append(u);
        sb.append("\n");
        i++;
      }
      
      if(i > 50000){
        System.exit(0);
      }
      
      try {
        FileUtils.writeStringToFile(new File("test.txt"), sb.toString(), true);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      
      
      // FileUtils.writeStringToFile(null, null, true);
      // 筛选所有链接,返回筛选结果
      final UrlFilter filter = new UrlFilter(this.matchPatterns, null, this.visitedURL);
      urls = filter.doFilter(urls);
      // 把筛选结果添加到待访问集合中,spider处理的url添加到已访问集合中

      logger.debug(urls);

      this.sAdd(this.toVisitURL, urls);
      this.sAdd(this.visitedURL, currentURL);
    }
    // 派出去一个新的蜘蛛
    while (visitingURL.size() < spiderNum && this.toVisitURL.size() > 1) {
      nextSpider();
    }
  }

  /**
   * @param spider
   * @return
   */
  protected LinkedList<String> getAllLinks(final Spider spider) {
    final Document doc = this.paser.getDocumentFromEntity(spider);
    LinkedList<String> urls = this.paser.getAllLinks(doc);
    return urls;
  }

  /**
   * 新建一个线程,一个新蜘蛛程
   * 
   * @return
   */
  protected boolean nextSpider() {
    String nexturl = null;
    boolean ready = false;
    while (!ready) {
      nexturl = this.pullToVisitURL();
      if (nexturl != null
          && !this.sContains(this.visitingURL, nexturl)
          && !this.sContains(this.visitedURL, nexturl)) {
        ready = true;
      }
      if (this.toVisitURL.size() == 0) {
        return false;
      }
    }
    sAdd(visitingURL, nexturl);
    final Spider s = new Spider(this, nexturl);
    new Thread(s, nexturl).start();
    return true;
  }

  protected synchronized String pullToVisitURL() {
    return this.toVisitURL.poll();
  }

  protected synchronized boolean sAdd(final Collection<String> c0, final Collection<String> c1) {
    return c0.addAll(c1);
  }

  protected synchronized boolean sAdd(final Collection<String> c, final String str) {
    return c.add(str);
  }

  protected synchronized boolean sContains(final Collection<String> c, final String str) {
    return c.contains(str);
  }

  protected synchronized boolean sRemove(final Collection<String> c, final String str) {
    return c.remove(str);
  }

  public void start() {
    this.initData();
    for (int i = 0; i < this.spiderNum; i++) {
      final String url = this.toVisitURL.poll();
      if (url != null) {
        sAdd(this.visitingURL, url);
        new Thread(new Spider(this, url)).start();
      } else {
        break;
      }
    }
  }
}
