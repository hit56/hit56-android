package com.hit56.android.activity;

import android.content.Intent;
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
import com.hit56.android.constants.FileData;
import com.hit56.android.constants.IntentConstants;
import com.hit56.android.utils.CoreUtil;
import com.hit56.android.utils.FileLocalCache;
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
 * Created by Stone on 16/10/2.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBt, registerBt;
    private EditText userNameEt;
    private EditText userPasswordEt;
    private Toast mToast;
    private String from;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CoreUtil.addToActivityList(this);
        initView();
        L.e("LoginActivity", "登录界面");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            from = bundle.getString(FileData.FROM);
            L.e("LoginActivity", from);
        }


    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginBt = (Button) findViewById(R.id.login_button);
        registerBt = (Button) findViewById(R.id.register_button);
        loginBt.setOnClickListener(this);
        registerBt.setOnClickListener(this);
        userNameEt = (EditText) findViewById(R.id.login_username);
        userPasswordEt = (EditText) findViewById(R.id.login_password);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login_button://登录

                judge();
                break;
            case R.id.register_button://去往注册

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void judge(){

        String userName = userNameEt.getText().toString();
        String userPassword = userPasswordEt.getText().toString();

        if (TextUtil.stringIsNull(userName)){
            userNameEt.setError("请输入用户名");
            return;
        }

        if (TextUtil.stringIsNull(userPassword)){
            userPasswordEt.setError("请输入密码");
            return;
        }


        L.e("LoginActivity", userName);
        L.e("LoginActivity", userPassword);
        String url = "http://www.hit56.com:8083/getinfo/login";
        login(userName, userPassword,url);

    }

    private void login(final String userName, final String userPassword, final String url) {


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
                        boolean isSuccess = result.getBoolean("success");
                        String info = result.getString("info");
                        String cell = result.getString("cell");
                        String profilePic = result.getString("profilePic");
                        mResult.setRegister(isSuccess);
                        mResult.setImageUrl(profilePic);
                        mResult.setInfo(info);
                        mResult.setCell(cell);

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
                String info;
                if (registerResultBean != null){
                    info= registerResultBean.getInfo();
                }else{
                    return;
                }

                if (mToast == null){
                    mToast = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_LONG);
                }
                if (registerResultBean.isRegister()){
                    mToast.setText("登录成功");
                    mToast.show();
                    AppController appController = AppController.getInstance();
                    registerResultBean.setUserName(userName);
                    appController.saveUserData(registerResultBean);
                    L.e("LoginActivity",registerResultBean.getImageUrl());
                    setResult(34);
                    CoreUtil.finishActivityList();
                }else {

                    mToast.setText(info);
                    mToast.show();

                }
            }

        }.execute(url);


    }



}
