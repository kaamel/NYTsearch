package com.kaamel.nytimessearch.model;

import com.kaamel.nytimessearch.data.Article;
import com.kaamel.nytimessearch.data.SearchFilter;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kaamel on 9/19/17.
 */

public abstract class NewsSourceAbst {

    final Retrofit retrofit;
    Object apiService;

    NewsSourceAbst() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NYTimesModel.NYTApiService.class);
    }

    public abstract void getArticles(String query, int page, OnDownladArticles onDownladArticles);
    public abstract void getArticles(String query, int page, SearchFilter filter, OnDownladArticles onDownladArticles);
    public abstract int getPageSize();

    protected abstract String getBaseUrl();

    public interface OnDownladArticles {
        abstract void onSuccessfulDownladArticles(List<? extends Article> articles);
        abstract void onFailedDownload(Throwable t);
    }

}
