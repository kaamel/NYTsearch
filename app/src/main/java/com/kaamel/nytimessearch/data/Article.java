package com.kaamel.nytimessearch.data;

/**
 * Created by kaamel on 9/19/17.
 */

public interface Article {
    String getWebUrl();
    String getHeadline();
    String getThumbNail();
    String getSnippet();
    String getPubDate();
}
