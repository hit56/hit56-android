package com.hit56.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.hit56.android.R;
import com.hit56.android.utils.L;

/**
 * Created by Stone on 16/10/30.
 */

public class WebActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            L.e("webVIew  ---------------------");
            String url = bundle.getString("Url");
            L.e(url);
            webView.loadUrl(url);

        }
    }

    private void initView(){
        webView = (WebView) findViewById(R.id.my_webview);
    }
}
