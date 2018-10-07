package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.core.widget.WebViewWithProgress;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class WebviewActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private ImageView backImg;
    private TextView title;

    private WebViewWithProgress mWebViewWithProgress;
    private WebView webview;

    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_webview);
        context = this;
        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText(getIntent().getExtras().getString("title"));

        link = getIntent().getExtras().getString("link");

        mWebViewWithProgress = (WebViewWithProgress) findViewById(R.id.webViewUI_webView);
        webview = mWebViewWithProgress.getWebView();

        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);

        // 设置WebView属性，取消密码保存提示
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        // 设置Web视图
        webview.loadUrl(link);
        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
