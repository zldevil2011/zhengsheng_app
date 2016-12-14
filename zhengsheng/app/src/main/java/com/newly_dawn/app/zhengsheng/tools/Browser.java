package com.newly_dawn.app.zhengsheng.tools;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.newly_dawn.app.zhengsheng.R;

public class Browser extends AppCompatActivity {
    private WebView father_webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("新闻");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.this.finish();
            }
        });
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        loadUrl(url);
    }
    public void loadUrl(String url){
        father_webView = (WebView)findViewById(R.id.webBrowser);
        father_webView.getSettings().setJavaScriptEnabled(true);
        father_webView.getSettings().setSupportZoom(true);
        father_webView.getSettings().setBuiltInZoomControls(true);
        father_webView.setInitialScale(100);
        Log.i("AAAAA", "12345");
//        WebView加载web资源
        try {
            father_webView.loadUrl(url);
        }catch (Exception e){
            Log.i("mylog", "" + e);
        }
//        覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        father_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                 TODO Auto-generated method stub
//                返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }
    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try{
            if ((keyCode == KeyEvent.KEYCODE_BACK) && father_webView.canGoBack()) {
                father_webView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            }
        }catch (Exception e){

        }
        return super.onKeyDown(keyCode, event);
    }

}
