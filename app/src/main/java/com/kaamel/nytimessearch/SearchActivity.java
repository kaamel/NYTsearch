package com.kaamel.nytimessearch;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kaamel.nytimessearch.databinding.ActivitySearchBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private ActivitySearchBinding binding;

    EditText etQuery;
    Button btnSearch;
    RecyclerView rvResults;
    NewsSourceAbst model;

    private static List<Article> articles;
    private SearchResultsAdapter searchResultsAdapter;

    int numberOfColumns;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayShowHomeEnabled(true);
            sab.setLogo(R.mipmap.ic_launcher);
            sab.setDisplayUseLogoEnabled(true);
            sab.setDisplayShowTitleEnabled(false);
        }

        etQuery = binding.included.etQuery;
        btnSearch = binding.included.btnSearch;
        rvResults = binding.included.rvResults;

        model = new NYTimesModel();

        // Initialize search results
        if (articles == null)
            articles = new ArrayList<>();
        // Create adapter passing in the sample user data
        searchResultsAdapter = new SearchResultsAdapter(this, articles);
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(searchResultsAdapter);
        // Set layout manager to position the items
        numberOfColumns = getResources().getInteger(R.integer.number_columns);
        gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        rvResults.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //// TODO: 9/20/17 will search through the headers (and snippets?) of the downloaded articles
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //// TODO: 9/20/17 will search through the headers of the downloaded articles
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

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        model.getArticles(query, new NewsSourceAbst.OnDownladArticles() {
            @Override
            public void onSuccessfulDownladArticles(List<? extends Article> a) {
                List<Article> list = searchResultsAdapter.getArticles();
                int count = articles.size();
                articles.clear();
                searchResultsAdapter.notifyItemRangeRemoved(0, count);
                articles.addAll(a);
                searchResultsAdapter.notifyItemRangeInserted(0, a.size());
            }

            @Override
            public void onFailedDownload(Throwable t) {
                String error = t.getLocalizedMessage();
                try {
                    JSONObject message = new JSONObject(error);
                    error = message.getString("message");
                } catch (JSONException e) {
                }
                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
