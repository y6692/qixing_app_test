package cn.qimate.test.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.qimate.test.R;
import cn.qimate.test.core.common.UIHelper;
import cn.qimate.test.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class ChangePasswordPhoneActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private TextView title;
    private ImageView backImg;

    private RelativeLayout passwordLayout;
    private RelativeLayout phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_change_password_phone);
        context = this;
        initView();
    }

    private void initView(){
        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("修改密码、手机号");

        passwordLayout = (RelativeLayout)findViewById(R.id.settingUI_passwordLayout);
        phoneLayout = (RelativeLayout)findViewById(R.id.settingUI_phoneLayout);

        backImg.setOnClickListener(this);
        passwordLayout.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.settingUI_passwordLayout:
                UIHelper.goToAct(context, ChangePasswordActivity.class);
                break;
            case R.id.settingUI_phoneLayout:
                UIHelper.goToAct(context,ChangePhoneNumActivity.class);
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
