package org.cong.spider;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Paser {

  public LinkedList<String> getAllLinks(final Document doc) {
    LinkedList<String> urls = null;
    if (doc != null) {
      urls = new LinkedList<>();
      final Elements links = doc.select("a[href]");
      for (final Element e : links) {
        urls.add(e.attr("abs:href"));
      }
    }
    return urls;
  }

  public Document getDocumentFromEntity(Spider spider) {
    HttpEntity entity = spider.getEntity();
    HttpResponse res = spider.getResponse();
    Document doc = null;
    String pageEncoding = null;
    if (res != null) {
      Header contentType = res.getFirstHeader("Content-Type");
      if (contentType != null) {
        String[] strEncoding = contentType.getValue().split("charset=");
        if (strEncoding.length > 1) {
          pageEncoding = strEncoding[1];
        }
      }else{
        Header contentEncoding = entity.getContentEncoding();
        if (contentEncoding != null) {
          pageEncoding = contentEncoding.getValue();
        } else {
          pageEncoding = "UTF-8";
        }
      }
    }
    if (entity != null) {
      try {
        doc = Jsoup.parse(entity.getContent(), pageEncoding, spider.getUrl());
      }
      catch (final IllegalStateException e) {
        e.printStackTrace();
      }
      catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return doc;
  }
}
