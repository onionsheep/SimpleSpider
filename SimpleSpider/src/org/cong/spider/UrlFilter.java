package org.cong.spider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlFilter {

  public Collection<String>  existedUrls;
  public Collection<Pattern> ignorePatterns;
  public Collection<Pattern> matchPatterns;

  public UrlFilter(final Collection<Pattern> matchPatterns,
                   final Collection<Pattern> ignorePatterns,
                   final Collection<String> existedUrls) {
    super();
    this.matchPatterns = matchPatterns;
    this.ignorePatterns = ignorePatterns;
    this.existedUrls = existedUrls;
  }

  public LinkedList<String> doFilter(final Collection<String> urls) {
    LinkedList<String> t = this.notinFilter(urls);
    t = this.patternFilter(t, this.matchPatterns, false);
    t = this.patternFilter(t, this.ignorePatterns, true);
    t = this.notinFilter(urls);
    return t;
  }

  public LinkedList<String> notinFilter(final Collection<String> urls) {
    final LinkedList<String> result = new LinkedList<>();
    if(existedUrls == null || existedUrls.size() == 0){
      result.addAll(urls);
      return result;
    }
    for (final String url : urls) {
      if (!this.existedUrls.contains(url)) {
        result.add(url);
      }
    }
    return result;
  }

  public LinkedList<String> patternFilter(final Collection<String> urls,
                                          final Collection<Pattern> patterns,
                                          final boolean ignore) {
    final LinkedList<String> result = new LinkedList<>();
    if ((patterns == null) || (patterns.size() == 0)) {
      result.addAll(urls);
      return result;
    }
    for (final String url : urls) {
      boolean add = true;
      for (final Pattern pattern : patterns) {
        final Matcher mather = pattern.matcher(url);
        if (ignore) {
          if (mather.find()) {
            add = false;
            break;
          }
        } else {
          if (!mather.find()) {
            add = false;
            break;
          }
        }
      }
      if (add) {
        result.add(url);
      }
    }
    return result;
  }
}
