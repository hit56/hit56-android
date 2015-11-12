package com.hit56.android.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.hit56.android.R;
import com.hit56.android.helper.SwipeListAdapter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.ArrayList;


public class MainActivity extends BaseHit56Activity{
    // action bar
    private android.support.v7.app.ActionBar actionBar;
    // Title navigation Spinner data


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Hide the action bar title
//        actionBar.setDisplayShowTitleEnabled(false);

        super.use_gps = true;
        super.listView = (ListView) findViewById(R.id.listView);
        super.swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        super.feedItemList = new ArrayList<>();
        super.adapter = new SwipeListAdapter(this, super.feedItemList);
        super.listView.setAdapter(adapter);
        super.swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        super.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchFeedItems("top");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), SearchResultsActivity.class)));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_share) {
            return true;
        } else if (id == R.id.menu_search){
            //新建一个Intent
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(MainActivity.this, SearchResultsActivity.class);
            //启动一个新的Activity
            startActivity(intent);
//            //关闭当前的
//            MainActivity2.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
