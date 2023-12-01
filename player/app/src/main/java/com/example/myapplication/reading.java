package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class reading extends AppCompatActivity {

    private WebView webView;
    private ImageView back;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    boolean wl=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reading);

        webView = (WebView)findViewById(R.id.wv);
        back=findViewById(R.id.back);





        //本地网页
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("http://kepub.net");

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver=new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);


    }


    class MyWebViewClient extends WebViewClient {


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if(Build.VERSION.SDK_INT <=  Build.VERSION_CODES.LOLLIPOP){//安卓5.0的加载方法
                view.loadUrl(request.toString());
            }else {//5.0以上的加载方法
                view.loadUrl(request.getUrl().toString());
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("WebView", "onPageStarted: ");
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isAvailable()){
                if(wl) Toast.makeText(reading.this,"网络已连接...",Toast.LENGTH_SHORT).show();

            }else{
                wl=true;
                Toast.makeText(reading.this,"网络未连接...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}