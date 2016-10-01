package com.hit56.android.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.WindowManager;

import com.hit56.android.GPSTracker;
import com.hit56.android.R;
import com.hit56.android.fragments.LoginFragment;
import com.hit56.android.fragments.OneFragment;
import com.hit56.android.fragments.ThreeFragment;
import com.hit56.android.fragments.TwoFragment;

import org.natuan.androidupdaterlibrary.UpdateFormat;
import org.natuan.androidupdaterlibrary.UpdateManager;
import org.natuan.androidupdaterlibrary.UpdateOptions;
import org.natuan.androidupdaterlibrary.UpdatePeriod;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
//        setupTabIcons();

//        getAllAppPermissions(this.getApplicationContext());
        onCheckUpdateClick(false);
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "货源");
        adapter.addFrag(new TwoFragment(), "车源");
        adapter.addFrag(new LoginFragment(), "我的");
        viewPager.setAdapter(adapter);
    }

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

    public void onCheckUpdateClick(boolean user_initiative_click) {
        UpdateManager manager = new UpdateManager(this);
        UpdateOptions options =null;
        if(user_initiative_click){
            options= new UpdateOptions.Builder(this)
                    .checkUrl("http://www.hit56.com:8083/getinfo/"+MainActivity.IMEI+"/true")
                    .updateFormat(UpdateFormat.JSON)
                    .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                    .checkPackageName(true)
                    .build();
        } else {
            options= new UpdateOptions.Builder(this)
                    .checkUrl("http://www.hit56.com:8083/getinfo/"+MainActivity.IMEI+"/false")
                    .updateFormat(UpdateFormat.JSON)
                    .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                    .checkPackageName(true)
                    .build();
        }

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
            onCheckUpdateClick(true);
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

    /**
     * get all <uses-permission> tags included under <manifest>
     *
     * @param context
     */
    public void getAllAppPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            //Array of all <uses-permission> tags included under <manifest>, or null if there were none.
            packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String permissions[] = packageInfo.requestedPermissions;

            if (permissions != null) { //to list permission
                for (String permission : permissions) {
                    boolean hasPerm = checkPermission(context, permission);
                    System.out.println("检查权限");
                    if (!hasPerm) {
                        System.out.println("没有权限");
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("提示")
                                .setMessage("请添加权限："+permission)
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        dialog.show();
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * check permission at runtime
     *
     * @param context
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return PackageManager.PERMISSION_GRANTED ==  pm.checkPermission(permission, context.getPackageName());
    }
}
