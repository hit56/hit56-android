package com.hit56.android.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hit56.android.GPSTracker;
import com.hit56.android.R;
import com.hit56.android.app.AppController;
import com.hit56.android.helper.FeedItem;
import com.hit56.android.helper.SwipeListAdapter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements SwipyRefreshLayout.OnRefreshListener {

    private String TAG = MainActivity.class.getSimpleName();

    private SwipyRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<FeedItem> feedItemList;

    // action bar
    private android.support.v7.app.ActionBar actionBar;
    // Title navigation Spinner data

    // initially offset will be 0, later will be updated while parsing the json
    private long min_offSet = Long.MAX_VALUE;
    private long max_offSet = Long.MIN_VALUE;
    private double latitude;
    private double longitude;

    GPSTracker gps;

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



        // create class object
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
//            Toast.makeText(getApplicationContext(), "\tYour Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        feedItemList = new ArrayList<>();
        adapter = new SwipeListAdapter(this, feedItemList);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
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

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//        }

        return super.onCreateOptionsMenu(menu);
    }
//    @Override
//    public boolean onSearchRequested() {
//
//        // your logic here
//
//        return false;  // don't go ahead and show the search box
//    }

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
//            MainActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

        Log.d("MainActivity", "Refresh triggered at "
                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            fetchFeedItems("top");
        } else {
            fetchFeedItems("bottom");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, 5000);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    private void parseJsonFeed(JSONArray response) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("cell"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("detail"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("time"));

                //update min_offset and max_offset
                if(Long.parseLong(feedObj.getString("time")) > max_offSet){
                    max_offSet = Long.parseLong(feedObj.getString("time"));
                }
                if(Long.parseLong(feedObj.getString("time")) < min_offSet){
                    min_offSet = Long.parseLong(feedObj.getString("time"));
                }

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItemList.add(item);
            }

            // notify data changes to list adapater
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetching feed items json by making http call
     */
    public void fetchFeedItems(String direction) {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = "http://www.hit56.com:8083/getinfo/goods/";
        if(direction == "top"){
            url += latitude + "/" + longitude + "/" + max_offSet + "/" + direction;
        } else {
            url += latitude + "/" + longitude + "/" + min_offSet + "/" + direction;
        }

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null) {
                            parseJsonFeed(response);
                        }
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(
                    NetworkResponse response) {
                try {
                    JSONArray jsonObject = new JSONArray(
                            new String(response.data, "UTF-8"));
                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
