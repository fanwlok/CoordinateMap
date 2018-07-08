package com.fanweilin.coordinatemap.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ProgressBar;

import com.fanweilin.coordinatemap.R;

public class WebActivity extends AppCompatActivity {
    private WebView webView;
   private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web);
        bar= findViewById(R.id.myProgressBar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        Intent intent=getIntent();
        if (intent.getStringExtra("url").equals("")){
            webView.loadUrl("http://m.weather.com.cn");
        }else {
            webView.loadUrl(intent.getStringExtra("url"));
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 90) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.GONE == bar.getVisibility()) {
                        bar.setVisibility(View.INVISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


    }
}
