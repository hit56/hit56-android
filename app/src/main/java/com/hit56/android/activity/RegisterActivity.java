package com.hit56.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hit56.android.R;
import com.hit56.android.app.AppController;
import com.hit56.android.bean.RegisterResultBean;
import com.hit56.android.utils.CoreUtil;
import com.hit56.android.utils.L;
import com.hit56.android.utils.TextUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


/**
 * Created by Stone on 16/10/3.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBt,loginBt;
    private EditText userNameEt;
    private EditText userPasswordEt;
    private Toast mToast;
    private boolean isFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CoreUtil.addToActivityList(this);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registerBt = (Button) findViewById(R.id.login_button);
        loginBt = (Button) findViewById(R.id.register_button);
        loginBt.setVisibility(View.INVISIBLE);
        registerBt.setText("立即注册");
        registerBt.setOnClickListener(this);

        userNameEt = (EditText) findViewById(R.id.login_username);
        userPasswordEt = (EditText) findViewById(R.id.login_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button://注册
                    L.e("RegisterActivity", "zhuce");
                    judge();
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void judge(){

        String userName = userNameEt.getText().toString();
        String userPassword = userPasswordEt.getText().toString();

        if (TextUtil.stringIsNull(userName) || (userName.length() != 11)){
            L.e("RegisterActivity", userName.length());
            userNameEt.setError("请输入11位电话号码");
            if (mToast == null){
                mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            }
            mToast.setText("请输入11位电话号码");
            mToast.show();
            return;
        }

        if (TextUtil.stringIsNull(userPassword)){
            userPasswordEt.setError("请输入密码");
            return;
        }


        L.e("RegisterActivity", userName);
        L.e("RegisterActivity", userPassword);
        String url = "http://www.hit56.com:8083/getinfo/register";
        register(userName, userPassword,url);

    }

    private void register(final String userName, final String userPassword, final String url) {


        new AsyncTask<String, Void, RegisterResultBean>(){


            @Override
            protected RegisterResultBean doInBackground(String... params) {

                RegisterResultBean mResult = null;
                try {
                    String url = params[0];
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost postRequest = new HttpPost(url);

                    JSONObject json = new JSONObject();
                    json.put("passwd",userPassword);
                    json.put("name",userName);
//			StringEntity input = new StringEntity(
//					"{\"qty\":100,\"name\":\"iPad 4\"}");
                    StringEntity input = new StringEntity(json.toString());
                    input.setContentType("application/json");
                    postRequest.setEntity(input);

                    HttpResponse response = httpClient.execute(postRequest);

                    if (response.getStatusLine().getStatusCode() != 201) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatusLine().getStatusCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (response.getEntity().getContent())));

                    String output;
                    mResult = new RegisterResultBean();
//			System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                        JSONObject result = JSON.parseObject(output);
                        L.e("RegisterActivity", result.toString());
                        boolean isSuccess = result.getBoolean("success");
                        String imageUrl = result.getString("profilePic");
                        String info = result.getString("info");
                        String cell = result.getString("cell");
                        mResult.setCell(cell);
                        mResult.setRegister(isSuccess);
                        mResult.setImageUrl(imageUrl);
                        mResult.setInfo(info);
                        mResult.setUserName(userName);

                    }

                    httpClient.getConnectionManager().shutdown();

                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();

                }

                return mResult;

            }

            @Override
            protected void onPostExecute(RegisterResultBean registerResultBean) {

                if (registerResultBean == null){
                   return;
                }
                String info = registerResultBean.getInfo();
                if (mToast == null){
                    mToast = Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_LONG);
                }
                if (registerResultBean.isRegister()){
                    mToast.setText("注册成功");
                    mToast.show();
                    AppController appController= AppController.getInstance();
                    registerResultBean.setUserName(userName);
                    appController.saveUserData(registerResultBean);
                    L.e("RegisterActivity", registerResultBean.getImageUrl());
                    CoreUtil.finishActivityList();
                }else {

                    mToast.setText(info);
                    mToast.show();
                    userNameEt.setFocusable(true);
                    userNameEt.setError(info);
                }
            }

        }.execute(url);


    }

    @Override
    protected void onDestroy() {
        if (mToast != null){
            mToast.cancel();
        }
        super.onDestroy();
    }
}
