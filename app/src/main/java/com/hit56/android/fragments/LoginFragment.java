package com.hit56.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.hit56.android.R;
import com.hit56.android.activity.LoginActivity;
import com.hit56.android.activity.ReleaseActivity;
import com.hit56.android.activity.ReleaseMessageActivity;
import com.hit56.android.app.AppController;
import com.hit56.android.bean.RegisterResultBean;
import com.hit56.android.constants.FileData;
import com.hit56.android.constants.IntentConstants;
import com.hit56.android.utils.L;
import com.hit56.android.widget.RLView;
import com.hit56.android.widget.RoundImageView;

/**
 * Created by Stone on 16/9/28.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private BroadcastReceiver broadcastReceiver;
    private Button loginBt;
    private RoundImageView headIv;
    private TextView userNameTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        refreshUserData();
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     *  绑定登录界面的组件
     * */
    private void initView(View view){
        loginBt = (Button) view.findViewById(R.id.me_login_buton);
        headIv = (RoundImageView) view.findViewById(R.id.me_user_head);
        userNameTv = (TextView) view.findViewById(R.id.me_user_name);
        RLView focus = (RLView) view.findViewById(R.id.me_focus);
        RLView sent = (RLView) view.findViewById(R.id.me_sent);
        RLView message = (RLView) view.findViewById(R.id.me_message);
        RLView logout = (RLView) view.findViewById(R.id.me_logout);

        focus.setOnClickListener(this);
        sent.setOnClickListener(this);
        message.setOnClickListener(this);
        logout.setOnClickListener(this);
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        L.e("fragment login");
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, 1234);
            }
        });

        registerBroadcastReceiver();

    }



    private void registerBroadcastReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConstants.USER_BROADCAST_RECEIVER);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null){
                    refreshUserData();
                }

            }
        };

        getActivity().registerReceiver(broadcastReceiver, intentFilter);

    }

    private void refreshUserData(){
        AppController appController = AppController.getInstance();
        final RegisterResultBean registerResultBean = appController.getUserData();
        if (registerResultBean != null){
            String url = registerResultBean.getImageUrl();
            RequestQueue requestQueue = appController.getRequestQueue();
            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    headIv.setImageBitmap(response);
                    loginBt.setVisibility(View.INVISIBLE);
                    userNameTv.setVisibility(View.VISIBLE);
                    userNameTv.setText(registerResultBean.getCell());
                }
            }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(imageRequest);
        }else {
            loginBt.setVisibility(View.VISIBLE);
            userNameTv.setVisibility(View.INVISIBLE);
            headIv.setImageResource(R.mipmap.head_nor);
        }

    }



    @Override
    public void onClick(View v) {
        AppController appController = AppController.getInstance();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        Bundle bundle = new Bundle();
        boolean isLogin = appController.isLogin();
        switch (v.getId()){
            case R.id.me_focus://我的关注
                if (isLogin){

                }else {

                    bundle = setBundle(bundle, FileData.FROM_FOCUS);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.me_sent://我的发布

                if (isLogin){

                    intent = new Intent(getActivity(), ReleaseMessageActivity.class);

                    startActivity(intent);

                }else {
                    bundle = setBundle(bundle, FileData.FROM_RELEASE);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.me_message://我的消息

                if (isLogin){

                }else {
                    bundle = setBundle(bundle, FileData.FROM_MESSAGE);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.me_logout://退出
                if (isLogin){
                    appController.deleteUserData();
                }
                break;
            default:
                break;

        }

    }

    private Bundle setBundle(Bundle bundle, String from){
        bundle.putString("from", from);
        return bundle;
    }

    @Override
    public void onDestroy() {

        if (broadcastReceiver != null){
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }
}
