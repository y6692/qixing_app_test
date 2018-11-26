package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.SharedPreferencesUrls;

public class WebActivity extends AppCompatActivity {
    Context mContext;

//    private static final String url ="https://item.taobao.com/item.htm?spm=a230r.1.14.16.1b075e4fooLHwL&id=581644369639&ns=1&abbucket=1#detail";
    private String url = SharedPreferencesUrls.getInstance().getString("ad_link", "");

    private WebView myWebView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web);
        mContext=this;

        myWebView = (WebView) findViewById(R.id.myWebView);
        WebSettings mysettings = myWebView.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mysettings.setJavaScriptEnabled(true);
        mysettings.setDomStorageEnabled(true);

        myWebView.setClickable(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(url);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {  //表示按返回键
                        myWebView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

    }

}
