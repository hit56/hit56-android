package com.hit56.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hit56.android.R;
import com.hit56.android.utils.L;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                L.e("zhuce");
                judge();
                break;

        }
    }

    private void judge(){

        String userName = userNameEt.getText().toString();
        String userPassword = userPasswordEt.getText().toString();

        L.e(userName);
        L.e(userPassword);
        //register(userName, userPassword);

    }

    private void register(String userName, String userPassword) {

        try {


            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                    "http://www.hit56.com:8083/getinfo/register");

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
//			System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                JSONObject result = JSON.parseObject(output);
                if(result.getBoolean("success") ==  true){
                    L.e("注册成功");
                } else {
                    L.e("注册失败");
                }
            }

            httpClient.getConnectionManager().shutdown();

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
    }


}
