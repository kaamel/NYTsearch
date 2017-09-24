package com.kaamel.nytimessearch.data;

import android.os.Bundle;

/**
 * Created by kaamel on 9/21/17.
 */


public class NYTSearchFilter implements SearchFilter {

    private static final String BEGIN_DATE = "begin_date";
    private static final String NEWS_DESK = "news_desk";
    private static final String SORT_ORDER = "sort_order";

    long beginDate;
    String[] newsDesk;
    SortOrder sortOrder;
    boolean[] checkboxes;

    public static NYTSearchFilter getFilter(Bundle bundle) {
        if (bundle == null) {
            return new NYTSearchFilter();
        }
        return new NYTSearchFilter(
                bundle.getLong(BEGIN_DATE),
                bundle.getStringArray(NEWS_DESK),
                SortOrder.valueOf(bundle.getString(SORT_ORDER)),
                bundle.getBooleanArray("checkboxes"));
    }

    @Override
    public SearchFilter.SortOrder getSortorder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(SearchFilter.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public long getBeginDate() {
        return beginDate;
    }

    @Override
    public void setBeginDate(long date) {
        beginDate = date;
    }

    @Override
    public String[] getCategories() {
        return newsDesk;
    }

    @Override
    public void setCategories(String[] categories) {
        newsDesk = categories;
    }

    public NYTSearchFilter() {
        beginDate = 0;
        newsDesk = new String[0];
        sortOrder = SortOrder.NONE;
    }

    public NYTSearchFilter(long beginDate, String[] newsDesk, SortOrder sortOrder, boolean[] checkboxes) {
        this.beginDate = beginDate;
        this.newsDesk = newsDesk;
        this.sortOrder = sortOrder;
        this.checkboxes = checkboxes;
    }

    public static Bundle getBundle(NYTSearchFilter filter) {
        Bundle bundle = new Bundle();
        if (filter == null)
            filter = new NYTSearchFilter();
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
