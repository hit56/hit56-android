package com.hit56.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hit56.android.R;
import com.hit56.android.activity.MainActivity;
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
import java.util.ArrayList;
import java.util.List;


public class OneFragment extends Fragment  implements SwipyRefreshLayout.OnRefreshListener {
    private final String URL_PREIFX = "http://www.hit56.com:8083/getinfo/" + MainActivity.IMEI;
//    private final String URL_PREIFX = "127.0.0.1:8083/getinfo/" + MainActivity.IMEI;
    private String TAG = OneFragment.class.getSimpleName();

    // initially offset will be 0, later will be updated while parsing the json
    private long min_offSet = Long.MAX_VALUE;
    private long max_offSet = Long.MIN_VALUE;


    protected SwipeListAdapter adapter;
    protected List<FeedItem> feedItemList;
    protected ListView listView;
    protected SwipyRefreshLayout swipeRefreshLayout;

    //Theses two viriables needs to be modified by subclasses
    protected boolean use_gps;
    protected String query = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        use_gps = true;
        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.swipyrefreshlayout);
        feedItemList = new ArrayList<>();

        adapter = new SwipeListAdapter(getActivity(), feedItemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                
            }
        });
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
        return rootView;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

        Log.d(TAG, "Refresh triggered at "
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


        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);


        if(direction == "top"){
            return URL_PREIFX + "/goods/" + MainActivity.latitude + "/" + MainActivity.longitude + "/" + max_offSet + "/" + direction;
        }
        return URL_PREIFX + "/goods/" + MainActivity.latitude + "/" + MainActivity.longitude + "/" + min_offSet + "/" + direction;
    }

    @NonNull
    private String getCityUrl(String direction) throws UnsupportedEncodingException {

        if(direction == "top"){
            return URL_PREIFX + "/" + URLEncoder.encode(query, "utf-8") + "/" +  max_offSet + "/" + direction;
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
                item.setTimeStamp(feedObj.getString("time"));

                //update min_offset and max_offset
                if(Long.parseLong(feedObj.getString("time")) > max_offSet){
                    max_offSet = Long.parseLong(feedObj.getString("time"));
                }
                if(Long.parseLong(feedObj.getString("time")) < min_offSet){
                    min_offSet = Long.parseLong(feedObj.getString("time"));
                }

                // url might be null sometimes
                String feedCell = feedObj.isNull("cell") ? null : feedObj
                        .getString("cell");
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
