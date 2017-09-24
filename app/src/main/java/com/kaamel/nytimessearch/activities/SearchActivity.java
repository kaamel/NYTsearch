package com.kaamel.nytimessearch.activities;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.kaamel.nytimessearch.utils.Utils;
import com.kaamel.nytimessearch.dialogfragments.EditFilterDialogFragment;
import com.kaamel.nytimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.kaamel.nytimessearch.R;
import com.kaamel.nytimessearch.adapters.SearchResultsAdapter;
import com.kaamel.nytimessearch.data.NYTSearchFilter;
import com.kaamel.nytimessearch.databinding.ActivitySearchBinding;
import com.kaamel.nytimessearch.data.Article;
import com.kaamel.nytimessearch.model.NYTimesModel;
import com.kaamel.nytimessearch.model.NewsSourceAbst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {


    private ActivitySearchBinding binding;

    RecyclerView rvResults;
    NewsSourceAbst model;

    private static String lastQuery;

    private static List<Article> articles;
    private SearchResultsAdapter searchResultsAdapter;

    int numberOfColumns;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    ActionBar sab;

    private static NYTSearchFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayShowHomeEnabled(true);
            sab.setLogo(R.mipmap.ic_launcher_round);
            sab.setDisplayUseLogoEnabled(true);
            if (lastQuery == null || lastQuery.length() ==0)
                sab.setDisplayShowTitleEnabled(false);
            else {
                sab.setTitle(getSearchTitle());
                sab.setDisplayShowTitleEnabled(true);
            }
        }

        if (filter == null)
            filter = new NYTSearchFilter();

        rvResults = binding.included.rvResults;

        model = new NYTimesModel();

        // Initialize search results
        if (articles == null)
            articles = new ArrayList<>();
        // Create adapter passing in the sample user data
        searchResultsAdapter = new SearchResultsAdapter(this, articles);
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(searchResultsAdapter);
        // Set spinner_sort_order_item manager to position the items
        numberOfColumns = getResources().getInteger(R.integer.number_columns);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(staggeredGridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(lastQuery, page);
            }
        };
        rvResults.setOnScrollListener(scrollListener);
    }

    public void loadNextDataFromApi(String query, int offset) {
        downloadSearchPage(query, offset);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                articleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.filter) {
            //Toast.makeText(this, "filter ...", Toast.LENGTH_SHORT).show();
            DialogFragment dialog = EditFilterDialogFragment.newInstance(filter);
            dialog.show(getSupportFragmentManager(), "filter");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void articleSearch(String query) {
        lastQuery = query;
        articles.clear();
        searchResultsAdapter.notifyDataSetChanged();
        scrollListener.resetState();
        if (sab!=null) {
            sab.setTitle(getSearchTitle());
            sab.setDisplayShowTitleEnabled(true);
        }
        downloadSearchPage(query, 0);
    }

    public void filterUpdated(NYTSearchFilter filter) {
        this.filter = filter;
    }

    private void downloadSearchPage(String query, final int page) {
        if (!Utils.isNetworkAvailable(this)) {
            Snackbar.make(rvResults, "You are not connected to network", 0).show();
            return;
        }
        else {
            if (!Utils.isOnline()) {
                Snackbar.make(rvResults, "You don't have internet access", 0).show();
                return;
            }
        }
        model.getArticles(query, page, filter, new NewsSourceAbst.OnDownladArticles() {
            @Override
            public void onSuccessfulDownladArticles(List<? extends Article> a) {
                if (a.size() == 0) {
                    if (page == 0)
                        Toast.makeText(SearchActivity.this, "Didn't find any articles ...", Toast.LENGTH_LONG).show();
                    //Snackbar.make(findViewById(android.R.id.content).getRootView(), "Empty result ...", Snackbar.LENGTH_LONG).show();
                    else {
                        Toast.makeText(SearchActivity.this, "No more articles ...", Toast.LENGTH_LONG).show();
                    }
                    scrollListener.setEndIsReached();
                }
                else {
                    articles.addAll(a);
                    searchResultsAdapter.notifyItemRangeInserted(page * model.getPageSize(), a.size());
                }
            }

            @Override
            public void onFailedDownload(Throwable t) {
                String error = t.getLocalizedMessage();
                try {
                    JSONObject message = new JSONObject(error);
                    error = message.getString("message");
                } catch (JSONException e) {
                }
                //Snackbar.make(findViewById(android.R.id.content).getRootView(), error, Snackbar.LENGTH_LONG).show();
                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onArticleClicked(int position) {
        //***************** Use the DetailArticleActivity with a WebView
        /*
        Intent intent = new Intent(this, DetailArticleActivity.class);
        intent.putExtra("web_url", articles.get(position).getWebUrl());
        startActivity(intent);
        */

        //***************** Using CustomChromeTab
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        // set toolbar color
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        // add share action to menu list
        builder.addDefaultShareMenuItem();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_black_vector_24dp);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, articles.get(position).getWebUrl());

        int requestCode = 100;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Map the bitmap, text, and pending intent to this icon
        // Set tint to be true so it matches the toolbar color
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);


        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(articles.get(position).getWebUrl()));
    }

    public String getSearchTitle() {
        String title = lastQuery ==null?"":lastQuery.toUpperCase(Locale.getDefault());
        if (filter != null && filter.getCategories() != null && filter.getCategories().length > 0) {
            for (String string: filter.getCategories()) {
                title = title + " " + string.toUpperCase(Locale.getDefault());
            }
        }
        return title;
    }
}
