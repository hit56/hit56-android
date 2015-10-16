package com.hit56.android.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.hit56.android.R;

public class SearchResultsActivity extends AppCompatActivity {

	private TextView txtQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		// get the action bar
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		// Enabling Back navigation on Action Bar icon
//		actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// These two lines not needed,
		// just to get the look of facebook (changing background color & hiding the icon)
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
		actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		txtQuery = (TextView) findViewById(R.id.txtQuery);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	/**
	 * Handling intent data
	 */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			/**
			 * Use this query to display search results like 
			 * 1. Getting the data from SQLite and showing in listview 
			 * 2. Making webrequest and displaying the data 
			 * For now we just display the query only
			 */
			txtQuery.setText("Search Query: " + query);

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
		SearchView searchView = null;
		if (searchItem != null) {
			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchResultsActivity.this.getComponentName()));
		}
		searchView.setSearchableInfo(searchManager.getSearchableInfo(
				new ComponentName(getApplicationContext(), SearchResultsActivity.class)));

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
