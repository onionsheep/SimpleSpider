package org.cong.spider;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Spider implements Runnable {
  protected HttpEntity    entity;

  protected SpiderManager manager;
  protected HttpResponse  response;
  protected String        url;
  private static Logger   logger = LogManager.getLogger(Spider.class);

  /**
   * 用URI来创建一个蜘蛛
   * 
   * @param url
   */
  public Spider(final SpiderManager manager, final String url) {
    this.manager = manager;
    this.url = url.trim();
    this.entity = null;
    this.response = null;
  }

  public HttpResponse get() {
    final HttpClient httpclient = new DefaultHttpClient();
    try {
      final HttpGet httpget = new HttpGet(this.url);
      this.response = httpclient.execute(httpget);
      this.entity = this.response.getEntity();
      this.entity = new BufferedHttpEntity(this.entity);
    }
    catch (final ClientProtocolException e) {
      Spider.logger.error("协议异常" + this.url);
      e.printStackTrace();
    }
    catch (final IOException e) {
      Spider.logger.error("IO异常" + this.url);
      e.printStackTrace();
    }
    catch (java.lang.IllegalArgumentException e) {
      Spider.logger.error("GET参数异常" + this.url);
      e.printStackTrace();
    }
    return this.response;
  }

  public HttpEntity getEntity() {
    return this.entity;
  }

  public HttpResponse getResponse() {
    return this.response;
  }

  public String getUrl() {
    return this.url;
  }

  @Override
  public void run() {
    logger.debug("new spider run with " + this.url);
    this.get();
    this.manager.complete(this);
  }
}
