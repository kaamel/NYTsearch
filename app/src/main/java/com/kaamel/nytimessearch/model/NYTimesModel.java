package com.kaamel.nytimessearch.model;

import android.os.Handler;

import com.google.gson.annotations.SerializedName;
import com.kaamel.nytimessearch.utils.Utils;
import com.kaamel.nytimessearch.data.Article;
import com.kaamel.nytimessearch.data.SearchFilter;

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

    private static final String API_KEY =  "227c750bb7714fc39ef1559ef1bd8329";
    private static final String BASE_URL = "https://api.nytimes.com/svc/search/v2/" ;
    private static final String ARTICLES = "articlesearch.json";

    private static final int pageSize = 10;

    private static final int RETRY = 3;

    public NYTimesModel() {
        super();
    }

    @Override
    public void getArticles(String query, int page, final OnDownladArticles onDownladArticles) {
        callSimple(query, page, onDownladArticles, RETRY);
    }

    @Override
    public void getArticles(String query, int page, SearchFilter filter, final OnDownladArticles onDownladArticles) {
        Map<String, String> queryMap = new HashMap<>();
        if (filter.getSortorder() != SearchFilter.SortOrder.NONE) {
            String sort = "newest";
            if (filter.getSortorder() == SearchFilter.SortOrder.OLDEST) {
                sort = "oldest";
            }
            queryMap.put("sort", sort);
        }

        if (filter.getBeginDate() >0)
            queryMap.put ("begin_date", Utils.longToNYTDateString(filter.getBeginDate()));

        String fq = "";
        if (filter.getCategories() != null && filter.getCategories().length>0) {
            boolean b = false;
            fq = "news_desk:(\"";
            for (String item: filter.getCategories()) {
                if (b)
                    fq = fq + " \"";
                fq = fq + item + "\"";
                b = true;
            }
            fq = fq + ")";
            queryMap.put("fq", fq);
        }

        if (queryMap.size()>0)
            callFull(query, page, filter, queryMap, onDownladArticles, RETRY);
        else
            callSimple(query, page, onDownladArticles, RETRY);
    }

    private void callSimple (String query, int page, final OnDownladArticles onDownladArticles, int retry) {
        ((NYTApiService) apiService).getArticles(query, page).enqueue(new Callback<NYTResponse>() {
            @Override
            public void onResponse(Call<NYTResponse> call, Response<NYTResponse> response) {
                int statusCode = response.code();
                if (response.code() == 429) {
                    //Rate limit is exceeded
                    if (retry > 0) {
                        retrySimple(query, page, onDownladArticles, retry - 1);
                        return;
                    }
                }
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

    private void callFull(String query, int page, SearchFilter filter, Map<String, String> queryMap, OnDownladArticles onDownladArticles, int retry) {
        ((NYTApiService) apiService).getArticles(query, page, queryMap).enqueue(new Callback<NYTResponse>() {
            @Override
            public void onResponse(Call<NYTResponse> call, Response<NYTResponse> response) {
                int statusCode = response.code();
                if (response.code() == 429) {
                    //Rate limit is exceeded
                    if (retry > 1) {
                        retryFull(query, page, filter, queryMap, onDownladArticles, retry-1);
                    }
                    return;
                }
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

    private void retryFull(String query, int page, SearchFilter filter, Map<String, String> queryMap, OnDownladArticles onDownladArticles, int retry) {
        Handler handler = new Handler();
        Runnable runnableCode = () -> callFull(query, page, filter, queryMap, onDownladArticles, retry);
        handler.postDelayed(runnableCode, 1000);
    }

    private void retrySimple(String query, int page, OnDownladArticles onDownladArticles, int retry) {
        Handler handler = new Handler();
        Runnable runnableCode = () -> callSimple(query, page, onDownladArticles, retry);
        handler.postDelayed(runnableCode, 1000);
    }

    @Override
    public int getPageSize() {
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

        @SerializedName(("pub_date"))
        String pubDate;

        @SerializedName(("byline"))
        By by;

        private class By {
            @SerializedName("original")
            String original;
        }

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
        public String getByLine() {
            return by==null?"":by.original;
        }

        @Override
        public String getPubDate() {
            return pubDate;
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
            String headline;

            @SerializedName("kicker")
            String kicker;

            @SerializedName("print_headline")
            String print_headline;
        }
    }

}
