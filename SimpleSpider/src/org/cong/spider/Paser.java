package org.cong.spider;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
        urls.add(e.attr("href"));
      }
    }
    return urls;
  }

  public Document getDocumentFromEntity(final HttpEntity entity,
                                        final String baseUri,
                                        String encoding) {
    Document doc = null;
    String pageEncoding = encoding;
    if (entity != null) {
      try {
        if (pageEncoding == null) {
          Header contentEncoding = entity.getContentEncoding();
          if (contentEncoding != null) {
            pageEncoding = contentEncoding.getValue();
          } else {
            pageEncoding = "UTF-8";
          }
        }
        doc = Jsoup.parse(entity.getContent(), pageEncoding, baseUri);
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
