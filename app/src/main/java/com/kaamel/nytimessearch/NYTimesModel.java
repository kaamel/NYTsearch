package com.kaamel.nytimessearch;

import com.google.gson.annotations.SerializedName;
import com.kaamel.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by kaamel on 9/19/17.
 */

public class NYTimesModel extends NewsSourceAbst {

    private static final String API_KEY =  "227c750bb7714fc39ef1559ef1bd8329"; //"da9e50c7db454f93b8e15e49cdae794c";
    private static final String BASE_URL = "https://api.nytimes.com/svc/search/v2/" ;
    private static final String ARTICLES = "articlesearch.json"; //+ "?api-key=" + API_KEY;

    private static final int pageSize = 10;

    NYTimesModel() {
        super();
    }

    @Override
    public void getArticles(String query, int page, final OnDownladArticles onDownladArticles) {
        ((NYTApiService) apiService).getArticles(query, page).enqueue(new Callback<NYTResponse>() {
            @Override
            public void onResponse(Call<NYTResponse> call, Response<NYTResponse> response) {
                int statusCode = response.code();
                if (response.isSuccessful()) {
                    NYTResponse nytResponse = response.body();
                    onDownladArticles.onSuccessfulDownladArticles((List<? extends Article>) nytResponse.response.articles);
                }
                else {
                    String error = "Unknown error";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {

                    }
                    onDownladArticles.onFailedDownload(new Throwable(error));
                }
            }

            @Override
            public void onFailure(Call<NYTResponse> call, Throwable t) {
                onDownladArticles.onFailedDownload(t);
            }
        });
    }

    @Override
    public void getArticles(String query, int page, SearchFilter filter, final OnDownladArticles onDownladArticles) {
        Map<String, String> queryMap = new HashMap<>();
        if (filter.sortOrder != SearchFilter.SortOrder.NONE) {
            String sort = "newest";
            if (filter.sortOrder == SearchFilter.SortOrder.OLDEST) {
                sort = "oldest";
            }
            queryMap.put("sort", sort);
        }

        if (filter.beginDate >0)
            queryMap.put ("begin_date", Utils.longToNYTDateString(filter.beginDate));

        String fq = "";
        if (filter.newsDesk != null && filter.newsDesk.length>0) {
            boolean b = false;
            fq = "news_desk:(\"";
            for (String item: filter.newsDesk) {
                if (b)
                    fq = fq + " \"";
                fq = fq + item + "\"";
                b = true;
            }
            fq = fq + ")";
            queryMap.put("fq", fq);
        }

        if (queryMap.size()>0)
            ((NYTApiService) apiService).getArticles(query, page, queryMap).enqueue(new Callback<NYTResponse>() {
            @Override
            public void onResponse(Call<NYTResponse> call, Response<NYTResponse> response) {
                int statusCode = response.code();
                if (response.isSuccessful()) {
                    NYTResponse nytResponse = response.body();
                    onDownladArticles.onSuccessfulDownladArticles((List<? extends Article>) nytResponse.response.articles);
                }
                else {
                    String error = "Unknown error";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {

                    }
                    onDownladArticles.onFailedDownload(new Throwable(error));
                }
            }

            @Override
            public void onFailure(Call<NYTResponse> call, Throwable t) {
                onDownladArticles.onFailedDownload(t);
            }
        });
        else
            ((NYTApiService) apiService).getArticles(query, page).enqueue(new Callback<NYTResponse>() {
                @Override
                public void onResponse(Call<NYTResponse> call, Response<NYTResponse> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        NYTResponse nytResponse = response.body();
                        onDownladArticles.onSuccessfulDownladArticles((List<? extends Article>) nytResponse.response.articles);
                    }
                    else {
                        String error = "Unknown error";
                        try {
                            error = response.errorBody().string();
                        } catch (IOException e) {

                        }
                        onDownladArticles.onFailedDownload(new Throwable(error));
                    }
                }

                @Override
                public void onFailure(Call<NYTResponse> call, Throwable t) {
                    onDownladArticles.onFailedDownload(t);
                }
            });
    }

    @Override
    int getPageSize() {
        return pageSize;
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

    interface NYTApiService {
        @Headers({"api-key: " + API_KEY})
        @GET(ARTICLES)
        public Call<NYTResponse> getArticles(@Query("q") String query, @Query("page") int page);

        @Headers({"api-key: " + API_KEY})
        @GET(ARTICLES)
        public Call<NYTResponse> getArticles(@Query("q") String query, @Query("page") int page, @QueryMap Map<String, String> mp);

        @Headers({"api-key: " + API_KEY})
        @GET(ARTICLES)
        public Call<NYTResponse> getArticles(
                @Query("q") String query,
                @Query("page") int page,
                @Query("sort") String sort,
                @Query("begin_date") String beginDate,
                @Query("fq") String newsDesks);
    }

    private class NYTResponse {
        @SerializedName("status")
        String status;

        @SerializedName("copyright")
        String copyright;

        @SerializedName("response")
        Resp response;

        private class Resp {
            @SerializedName("docs")
            ArrayList<NYTArticle> articles;
        }
    }

    private class NYTArticle implements Article {

        @SerializedName("web_url")
        String webUrl;

        @SerializedName("snippet")
        String snippet;

        @SerializedName("headline")
        HeadLine headlineObj;

        @SerializedName("multimedia")
        List<MM> multimedia;

        private class MM {
            @SerializedName("url")
            String url;

            @SerializedName("subtype")
            String subtype;
        }

        public NYTArticle(String webUrl, HeadLine headline, List<MM> multimedia ) {
            this.webUrl = webUrl;
            headlineObj = headline;
            this.multimedia = multimedia;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        @Override
        public String getWebUrl() {
            return webUrl;
        }

        @Override
        public String getHeadline() {
            return headlineObj.headline;
        }

        @Override
        public String getThumbNail() {
            for (MM mm:multimedia) {
                if (mm.subtype.equals("thumbnail"))
                    return mm.url;
            }
            return null;
        }

        private class HeadLine {
            @SerializedName("main")
            String main;

            @SerializedName("kicker")
            String kicker;

            @SerializedName("print_headline")
            String headline;
        }
    }

}
