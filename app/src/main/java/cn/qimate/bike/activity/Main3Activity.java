package cn.qimate.bike.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.base.BaseFragmentActivity;

public class Main3Activity extends BaseActivity {

    TextView title;
//    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3);

        title = (TextView) findViewById(R.id.mainUI_title);
        title.setText("xxx8");

    }

}
