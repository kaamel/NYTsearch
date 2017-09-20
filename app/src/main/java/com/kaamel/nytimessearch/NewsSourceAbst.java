package com.kaamel.nytimessearch;

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

    public abstract void getArticles(String query, OnDownladArticles onDownladArticles);
    protected abstract String getBaseUrl();

    interface OnDownladArticles {
        abstract void onSuccessfulDownladArticles(List<? extends Article> articles);
        abstract void onFailedDownload(Throwable t);
    }

}
