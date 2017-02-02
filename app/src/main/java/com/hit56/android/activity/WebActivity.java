package com.hit56.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hit56.android.R;
import com.hit56.android.utils.L;

/**
 * Created by Stone on 16/10/30.
 */

public class WebActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initWebView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            L.e("WebActivity", "webVIew  ---------------------");
            String url = bundle.getString("Url");
            L.e("WebActivity", url);
            webView.loadUrl(url);

        }

    }

    private void initView(){
        webView = (WebView) findViewById(R.id.my_webview);
        progressBar = (ProgressBar) findViewById(R.id.my_progress);
    }

    private void initWebView(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress >= 100){
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }

            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (webView !=null && webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
