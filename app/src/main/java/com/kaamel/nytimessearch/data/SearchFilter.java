package com.kaamel.nytimessearch.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaamel on 9/24/17.
 */

public interface SearchFilter {

    enum SortOrder {
        NONE,
        NEWEST,
        OLDEST;

        static String[] titles = {
                "Articles Not Sorted",
                "Latest First",
                "Oldest First"
        };

        public static List<String> getTitles() {
            List<String> ts = new ArrayList<>();
            for (int i=0 ; i <titles.length; i++) {
                ts.add(titles[i]);
            }
            return ts;
        }

        public String getTitle() {
            return titles[ordinal()];
        }
    }

    public static final String FILTER = "filter";
    public SortOrder getSortorder();
    public void setSortOrder(SortOrder sortOrder);
    public long getBeginDate();
    public void setBeginDate(long date);
    public String[] getCategories();
    public void setCategories(String[] categories);

}
