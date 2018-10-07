package cn.qimate.bike.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseFragmentActivity;

public class Main2Activity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        context = this;

    }
}
