package com.hit56.android.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hit56.android.bean.RegisterResultBean;
import com.hit56.android.constants.CoreConstants;
import com.hit56.android.constants.FileData;
import com.hit56.android.constants.IntentConstants;
import com.hit56.android.utils.FileLocalCache;
import com.hit56.android.utils.FileUtil;
import com.hit56.android.utils.L;
import com.hit56.android.volley.LruBitmapCache;

/**
 * Created by Ravi on 13/05/15.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static Context context;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;
    private RegisterResultBean registerResultBean;

    public static boolean IS_EXIST_SDCARD;
    public static String CACHE_DIR_SD;                      //SD卡缓存目录
    public static String CACHE_DIR_SYSTEM;                  //系统目录
    public static String IMAGE_DIR;                   		//图片目录
    public static String FILE_DIR;                     	    //文件目录
    public static String LOG_DIR;                     	    //日志目录
    public static String IMAGE_UPLOAD_TEMP;   				//上传图片临时目录
    public static String LOG;                           //日志保存的SD卡的目录
    public static String AllLOG;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = this;
        init();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }


    public static Context getContext(){
        return context;

    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void init(){
        if(FileUtil.isExistSD()){
            //SD存在
            CACHE_DIR_SD = FileUtil.getCacheDirectory(context);
            IS_EXIST_SDCARD=true;
        }else{
            //不存在则使用系统目录
            CACHE_DIR_SD = context.getCacheDir().getPath();
        }
        CACHE_DIR_SD += "/";
        L.e("----SD卡目录---->>>:" +CACHE_DIR_SD);
        LOG=CACHE_DIR_SD+"cache.log";
        AllLOG=CACHE_DIR_SD+"allcache.log";
        IMAGE_DIR=CACHE_DIR_SD+"image/";
        FILE_DIR=CACHE_DIR_SD+"file/";
        LOG_DIR=CACHE_DIR_SD+"log/";
        IMAGE_UPLOAD_TEMP=CACHE_DIR_SD+"imageUploadTemp/";
        CACHE_DIR_SYSTEM=context.getCacheDir().getPath()+"/file/";

        FileUtil.checkDir(CACHE_DIR_SD,IMAGE_DIR,
                FILE_DIR,LOG_DIR,IMAGE_UPLOAD_TEMP,CACHE_DIR_SYSTEM);
    }

    /**
     *   保持用户登录数据
     * @param registerResultBean
     */
    public void saveUserData(RegisterResultBean registerResultBean){
        this.registerResultBean = registerResultBean;
        FileLocalCache.setSerializableData(CoreConstants.CACHE_DIR_SYSTEM, registerResultBean, FileData.USER);
        Intent intent = new Intent(IntentConstants.USER_BROADCAST_RECEIVER);
        sendBroadcast(intent);
    }

    /**
     * 注销用户
     */
    public void deleteUserData(){
        this.registerResultBean = null;
        FileLocalCache.deleteSerializableData(CoreConstants.CACHE_DIR_SYSTEM, FileData.USER);
        L.e("移除了用户信息");
        Intent intent = new Intent(IntentConstants.USER_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putInt(IntentConstants.USER_BROADCAST_STYLE, IntentConstants.USER_LOGOUT);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 判断用户是否已登录
     * @return
     */
    public boolean isLogin(){
        if (this.registerResultBean != null){
            return true;
        }
        this.registerResultBean = (RegisterResultBean) FileLocalCache.getSerializableData(CoreConstants.CACHE_DIR_SYSTEM, FileData.USER);
        if (registerResultBean != null){
            return true;
        }
        return false;
    }

    /**
     * 获取用户信息
     * @return
     */
    public RegisterResultBean getUserData(){
        if (this.registerResultBean != null){
            return registerResultBean;
        }
        this.registerResultBean = (RegisterResultBean) FileLocalCache.getSerializableData(CoreConstants.CACHE_DIR_SYSTEM, FileData.USER);
        return registerResultBean;
    }






}