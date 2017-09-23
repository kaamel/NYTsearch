package com.kaamel.nytimessearch;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaamel on 9/21/17.
 */


public class SearchFilter {

    public static final String FILTER = "filter";
    public static final String BEGIN_DATE = "begin_date";
    private static final String NEWS_DESK = "news_desk";
    private static final String SORT_ORDER = "sort_order";

    long beginDate;
    String[] newsDesk;
    SortOrder sortOrder;
    boolean[] checkboxes;

    public static SearchFilter getFilter(Bundle bundle) {
        if (bundle == null) {
            return new SearchFilter();
        }
        return new SearchFilter(
                bundle.getLong(BEGIN_DATE),
                bundle.getStringArray(NEWS_DESK),
                SortOrder.valueOf(bundle.getString(SORT_ORDER)),
                bundle.getBooleanArray("checkboxes"));
    }

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

    public SearchFilter() {
        beginDate = 0;
        newsDesk = new String[0];
        sortOrder = SortOrder.NONE;
    }

    public SearchFilter(long beginDate, String[] newsDesk, SortOrder sortOrder, boolean[] checkboxes) {
        this.beginDate = beginDate;
        this.newsDesk = newsDesk;
        this.sortOrder = sortOrder;
        this.checkboxes = checkboxes;
    }

    public static Bundle getBundle(SearchFilter filter) {
        Bundle bundle = new Bundle();
        if (filter == null)
            filter = new SearchFilter();
        bundle.putLong(BEGIN_DATE, filter.beginDate);
        bundle.putStringArray(NEWS_DESK, filter.newsDesk);
        bundle.putString(SORT_ORDER, filter.sortOrder.name());
        bundle.putBooleanArray("checkboxes", filter.getCheckboxes());
        return bundle;
    }

    public void setCheckboxes(boolean[] checkboxes) {
        this.checkboxes = checkboxes;
    }

    public boolean[] getCheckboxes() {
        return checkboxes;
    }

}
