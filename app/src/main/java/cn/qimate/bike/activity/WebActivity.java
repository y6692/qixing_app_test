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

public class WebActivity extends AppCompatActivity {
    Context mContext;
    public static int f=7;

//    private static final String url ="http://m.ctrip.com/webapp/hotel/orderdetail?orderid=7274235733&pageorigin=25120100100020&from=%2Fwebapp%2Fmyctrip%2Forders%2Fhotelorderlist";
//    private static final String url ="https://m.ctrip.com/webapp/myctrip/index";
//    private static final String url ="https://www.baidu.com/";
//    private static final String url ="https://m.baidu.com/?from=1013112a";
//    private static final String url ="http://m.ctrip.com/webapp/hotel";
//    private static final String url ="https://m.ctrip.com/webapp/myctrip/orders/hotelorderlist";
//    private static final String url ="http://m.ctrip.com/webapp/hotel/orderdetail?orderid=7274235733&pageorigin=25120100100020&from=%2Fwebapp%2Fmyctrip%2Forders%2Fhotelorderlist";
//    private static final String url ="http://m.ctrip.com/webapp/hotel/orderdetail";
//    private static final String url ="https://m.ctrip.com/webapp/hotel/?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F";
//    private static final String url ="https://m.ctrip.com/webapp/myctrip/orders/hotelorderlist?from=https%3A%2F%2Fm.ctrip.com%2Fwebapp%2Fhotel%2F%3Ffrom%3Dhttps%253A%252F%252Fm.ctrip.com%252Fhtml5%252F";
//    private static final String url ="https://app.etongdai.com/";
//    private static final String url ="http://www.ctrip.com/?utm_source=baidu&utm_medium=cpc&utm_campaign=baidu81&campaign=CHNbaidu81&adid=index&gclid=&isctrip=T";
//    private static final String url ="https://www.taobao.com/";
//    private static final String url ="https://h5.m.taobao.com/";
//    private static final String url ="http://blog.csdn.net/harvic880925";

    private static final String url ="https://item.taobao.com/item.htm?spm=a230r.1.14.16.1b075e4fooLHwL&id=581644369639&ns=1&abbucket=1#detail";

    private EditText et_address;
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
//        mysettings.setAllowFileAccess(true);

//        mysettings.setDatabaseEnabled(true);
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        mysettings.setGeolocationEnabled(true);
//        mysettings.setGeolocationDatabasePath(dir);
        mysettings.setJavaScriptEnabled(true);
        mysettings.setDomStorageEnabled(true);
//        mysettings.setDefaultTextEncodingName("UTF-8");
//        mysettings.setUseWideViewPort(true);
//        mysettings.setLoadWithOverviewMode(true);
//        mysettings.setAppCacheEnabled(true);
//        mysettings.setCacheMode(WebSettings.LOAD_DEFAULT);


//        mysettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        mysettings.setDefaultTextEncodingName("utf-8");// 避免中文乱码
//        myWebView.setScrollBarStyle(0);
//        myWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
//        myWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
//        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        mysettings.setCacheMode(WebSettings.LOAD_DEFAULT | WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        mysettings.setNeedInitialFocus(false);
//        myWebView.setBackgroundColor(Color.TRANSPARENT);// 设置其背景为透明


        myWebView.setClickable(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }


//        mysettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false

        String baseUrl = "file:///android_asset";
//        myWebView.loadDataWithBaseURL(baseUrl, strHtml, "text/html", "utf-8",	null);

//        myWebView.addJavascriptInterface(new JsInterface(), "mv");

//        mysettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//
//        mysettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//
//        mysettings.setAppCacheEnabled(true);//是否使用缓存
//
//        mysettings.setDomStorageEnabled(true);//DOM Storage

        // displayWebview.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用
//        mysettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//加载https和http混合模式


//        ;
        myWebView.setWebViewClient(new WebViewClient());

//        myWebView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onReceivedIcon(WebView view, Bitmap icon) {
//                super.onReceivedIcon(view, icon);
//            }
//
//            @Override
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                callback.invoke(origin,true,false);
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
//            }
//        });

        /*
        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
////                Toast.makeText(mContext, "==="+url, Toast.LENGTH_SHORT).show();
//
//                view.loadUrl(url);
//                return true;
//
//            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (shouldOverrideUrlLoadingByApp(view, url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            //根据url的scheme处理跳转第三方app的业务
            private boolean shouldOverrideUrlLoadingByApp(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
                    //不处理http, https, ftp的请求
                    return false;
                }
                Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    Log.e("===1", "URISyntaxException: " + e.getLocalizedMessage());
                    return false;
                }
                intent.setComponent(null);
                try {
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("===2", "ActivityNotFoundException: " + e.getLocalizedMessage());
                    return false;
                }
                return true;
            }
        });
        */
//



//
//        myWebView.setJavascriptInterface(new JsApi());

//        myWebView.clearCache(true);
//        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//            }
//        });



        myWebView.loadUrl(url);
//        myWebView.loadUrl("http://wxpay.weixin.qq.com/pub_v2/pay/wap.v2.php");

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
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {  //表示按返回键

                        myWebView.goBack();   //后退

                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });


//        myWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                //此处为 js 返回的结果
//            }
//        });

//        TextView tv=(TextView)findViewById(R.id.tv);
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                Intent intent = new Intent(mContext, Main2Activity.class);
//                mContext.startActivity(intent);
//                finish();
//
//            }
//        });
    }

//    protected final class LoadHtml {
//        public void returnValue(final String htmls) {
//            handler.post(new Runnable() {
//                public void run() {
//                    // 这里判断htmls的值，然后显示告诉用户提交状态。
//                }
//            });
//        }
//    }


    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互

    }



}
