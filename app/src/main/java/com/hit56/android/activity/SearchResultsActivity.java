package com.hit56.android.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.hit56.android.R;
import com.hit56.android.helper.SwipeListAdapter;
import com.hit56.android.utils.L;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.ArrayList;

public class SearchResultsActivity  extends BaseHit56Activity{

	//private TextView txtQuery;
	private String searchText = "北京";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		initView();
	}

	@Override
	protected void onResume() {
		L.e("SearchResultsActivity", "onresume");
		super.use_gps = false;
		//super.query = getIntent().getStringExtra(SearchManager.QUERY);
		super.query = searchText;
		super.listView = (ListView) findViewById(R.id.listView);
		super.swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
		super.feedItemList = new ArrayList<>();
		super.adapter = new SwipeListAdapter(this, super.feedItemList);
		super.listView.setAdapter(adapter);
		super.swipeRefreshLayout.setOnRefreshListener(this);

		/**
		 * Showing Swipe Refresh animation on activity create
		 * As animation won't start on onCreate, post runnable is used
		 * */
		super.swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				fetchFeedItems("top");
			}
		});
		super.onResume();
	}

	private void initView(){
		// get the action bar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

	}

	@Override
	protected void onNewIntent(Intent intent) {
		L.e("SearchResultsActivity", "getIntent");
		if (intent != null){
			setIntent(intent);
			handleIntent(intent);
			searchText = intent.getStringExtra(SearchManager.QUERY);
		}

	}

	/**
	 * Handling intent data
	 */
	private void handleIntent(Intent intent) {
		/**
		 * Use this query to display search results like
		 * 1. Getting the data from SQLite and showing in listview
		 * 2. Making webrequest and displaying the data
		 * For now we just display the query only
		 */
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			//String searchText = intent.getStringExtra(SearchManager.QUERY);

		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_search, menu);

		// Associate searchable configuration with the SearchView
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchManager searchManager = (SearchManager) SearchResultsActivity.this.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint("搜索");
		searchView.setSubmitButtonEnabled(true);//提交button可见
		searchView.onActionViewExpanded();//展开searchView
		searchView.setIconifiedByDefault(true);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
			default:
	 			return super.onOptionsItemSelected(item);
		}

	}

}
