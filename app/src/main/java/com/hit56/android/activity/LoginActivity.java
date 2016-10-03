package com.hit56.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hit56.android.R;
import com.hit56.android.utils.CoreUtil;

/**
 * Created by Stone on 16/10/2.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBt, registerBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CoreUtil.addToActivityList(this);
        initView();

    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginBt = (Button) findViewById(R.id.login_button);
        registerBt = (Button) findViewById(R.id.register_button);

        loginBt.setOnClickListener(this);
        registerBt.setOnClickListener(this);

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
                break;
            case R.id.register_button://去往注册

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;


        }

    }
}
