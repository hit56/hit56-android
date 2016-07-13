package com.hit56.android.activity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hit56.android.GPSTracker;
import com.hit56.android.app.AppController;
import com.hit56.android.helper.FeedItem;
import com.hit56.android.helper.SwipeListAdapter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zhenghong on 11/7/15.
 */
public class BaseHit56Activity extends AppCompatActivity
        implements SwipyRefreshLayout.OnRefreshListener {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String URL_PREIFX = "http://www.hit56.com:8083/getinfo/" + MainActivity.IMEI;
//    private final String URL_PREIFX = "127.0.0.1:8083/getinfo/" + MainActivity.IMEI;
    private String TAG = BaseHit56Activity.class.getSimpleName();
    private GPSTracker gps;
    // initially offset will be 0, later will be updated while parsing the json
    private long min_offSet = Long.MAX_VALUE;
    private long max_offSet = Long.MIN_VALUE;
    private double latitude;
    private double longitude;

    protected SwipeListAdapter adapter;
    protected List<FeedItem> feedItemList;
    protected ListView listView;
    protected SwipyRefreshLayout swipeRefreshLayout;

    //Theses two viriables needs to be modified by subclasses
    protected boolean use_gps = true;
    protected String query = "";
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

        Log.d("BaseHit56Activity", "Refresh triggered at "
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
                        swipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    @NonNull
    private String getGpsUrl(String direction) {
        // create class object
        gps = new GPSTracker(BaseHit56Activity.this);
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

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);


        if(direction == "top"){
            return URL_PREIFX + "/goods/" +latitude + "/" + longitude + "/" + max_offSet + "/" + direction;
        }
        return URL_PREIFX + "/goods/" + latitude + "/" + longitude + "/" + min_offSet + "/" + direction;
    }

    @NonNull
    private String getCityUrl(String direction) throws UnsupportedEncodingException {

        if(direction == "top"){
                return URL_PREIFX + "/"+ URLEncoder.encode(query, "utf-8") + "/" +  max_offSet + "/" + direction;
        }
            return URL_PREIFX + "/" + URLEncoder.encode(query, "utf-8") + "/" + min_offSet + "/" +  direction;
    }
    /**
     * Fetching feed items json by making http call
     */
    public void fetchFeedItems(String direction) {
        String url = "";
        if(use_gps == true){
            url = getGpsUrl(direction);
        } else {
            try {
                url = getCityUrl(direction);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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



    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    private void parseJsonFeed(JSONArray response) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("detail"));
                item.setProfilePic(feedObj.getString("profilePic"));
                long ts = 0;
                try {
                    ts = BaseHit56Activity.DATE_FORMAT.parse(feedObj.getString("time")).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setTimeStamp(String.valueOf(ts));


                //update min_offset and max_offset
                if(ts > max_offSet){
                    max_offSet = ts;
                }
                if(ts < min_offSet){
                    min_offSet = ts;
                }

                // url might be null sometimes
                String feedCell = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setCell(feedCell);

                feedItemList.add(item);
            }

            // notify data changes to list adapater
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
