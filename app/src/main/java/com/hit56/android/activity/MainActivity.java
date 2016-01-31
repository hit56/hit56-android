package com.hit56.android.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hit56.android.GPSTracker;
import com.hit56.android.R;
import com.hit56.android.fragments.OneFragment;
import com.hit56.android.fragments.TwoFragment;

import org.natuan.androidupdaterlibrary.UpdateFormat;
import org.natuan.androidupdaterlibrary.UpdateManager;
import org.natuan.androidupdaterlibrary.UpdateOptions;
import org.natuan.androidupdaterlibrary.UpdatePeriod;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String IMEI = "NULL";
    public static double latitude;
    public static double longitude;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.ic_tab_favourite,
            R.mipmap.ic_tab_call
//            ,
//            R.mipmap.ic_tab_contacts
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MainActivity.IMEI = TelephonyMgr.getDeviceId();
        setGpsPosition();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "货源");
        adapter.addFrag(new TwoFragment(), "车源");
//        adapter.addFrag(new ThreeFragment(), "我的");
        viewPager.setAdapter(adapter);
    }


//    /**
//     * 监听返回--是否退出程序
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 是否退出应用
//            if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
//                return mDoubleClickExit.onKeyDown(keyCode, event);
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(),
                SearchResultsActivity.class)));


        return super.onCreateOptionsMenu(menu);
    }

    public void onCheckUpdateClick() {
        UpdateManager manager = new UpdateManager(this);
        UpdateOptions options = new UpdateOptions.Builder(this)
                .checkUrl("http://www.hit56.com:8083/getinfo/866946026709755/40.06338/116.351041")
                .updateFormat(UpdateFormat.JSON)
                .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                .checkPackageName(true)
                .build();
        manager.check(this, options);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            //新建一个Intent
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(MainActivity.this, SearchResultsActivity.class);
            //启动一个新的Activity
            startActivity(intent);
            //关闭当前的
//            MainActivity.this.finish();
        } else if (id == R.id.action_update) {
            onCheckUpdateClick();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setGpsPosition() {
        // create class object
        GPSTracker gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            MainActivity.latitude = gps.getLatitude();
            MainActivity.longitude = gps.getLongitude();
            // \n is for new line
//            Toast.makeText(getApplicationContext(), "\tYour Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
}
