package com.hit56.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Window;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = MainActivity.class.getSimpleName();

    private String URL_TOP_250 = "http://www.hit56.com:8083/getinfo/";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<FeedItem> feedItemList;

    // initially offset will be 0, later will be updated while parsing the json
    private long offSet = 0;
    double latitude;
    double longitude;

    GPSTracker gps;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // create class object
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

             latitude = gps.getLatitude();
             longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "\tYour Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
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
                fetchMovies();
            }
        });
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchMovies();
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
     * Fetching movies json by making http call
     */
    private void fetchMovies() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
//        String url = URL_TOP_250 + offSet;
        String url = URL_TOP_250;
//        mylistener.getCity(String.valueOf(location.getLatitude()),
//                String.valueOf(location.getLongitude())
//        );
//        String fuck = url + city + "/0";
        String fuck = "http://www.hit56.com:8083/getinfo/"+latitude+"/"+longitude+"/"+offSet;
        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(fuck,
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
