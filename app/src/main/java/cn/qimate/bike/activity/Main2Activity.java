package cn.qimate.bike.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

public class Main2Activity extends BaseFragmentActivity implements View.OnClickListener{

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        title = (TextView) findViewById(R.id.mainUI_title);
        title.setText("main2");

        title.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mainUI_title:
                Intent intent = new Intent(this, Main3Activity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        ToastUtil.showMessage(this, "main2====");
//
////        try {
////            if (internalReceiver != null) {
////                unregisterReceiver(internalReceiver);
////            }
////        } catch (Exception e) {
////            Toast.makeText(this, "eee====" + e, Toast.LENGTH_SHORT).show();
////        }
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            scrollToFinishActivity();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }


}
