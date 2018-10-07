package cn.qimate.bike.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

public class Main2Activity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        context = this;
    }


    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(this, "main2====" + internalReceiver, Toast.LENGTH_SHORT).show();

//        try {
//            if (internalReceiver != null) {
//                unregisterReceiver(internalReceiver);
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "eee====" + e, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
