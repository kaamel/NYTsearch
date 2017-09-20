package com.kaamel.nytimessearch;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kaamel on 9/19/17.
 */

public class NYTimesModel extends NewsSourceAbst {

    private static final String API_KEY = "da9e50c7db454f93b8e15e49cdae794c";
    private static final String BASE_URL = "https://api.nytimes.com/svc/search/v2/" ;
    private static final String ARTICLES = "articlesearch.json"+ "?api-key=" + API_KEY;

    NYTimesModel() {
        super();
    }

    @Override
    public void getArticles(String query, final OnDownladArticles onDownladArticles) {
        ((NYTApiService) apiService).getArticles(query).enqueue(new Callback<NYTResponse>() {
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
    protected String getBaseUrl() {
        return BASE_URL;
    }

    interface NYTApiService {
        @GET(ARTICLES)
        public Call<NYTResponse> getArticles(@Query("q") String query);
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
