package cn.qimate.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.test.R;
import cn.qimate.test.core.common.HttpHelper;
import cn.qimate.test.core.common.SharedPreferencesUrls;
import cn.qimate.test.core.common.UIHelper;
import cn.qimate.test.core.common.Urls;
import cn.qimate.test.core.widget.LoadingDialog;
import cn.qimate.test.model.CurRoadBikingBean;
import cn.qimate.test.model.ResultConsel;
import cn.qimate.test.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class CurRoadBikedActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private LoadingDialog payLoadingDialog;
    private ImageView backImg;
    private TextView title;

    private TextView bikeCode;
    private TextView bikeNum;
    private TextView startTime;
    private TextView endTime;
    private TextView timeText;
    private TextView moneyText;
    private TextView balanceText;
    private LinearLayout payBalanceLayout;
    private String oid = "";
    private String user_money = "";
    private String prices = "";
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_cur_road_biked);
        context = this;
        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
        initView();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT);
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            getCurrentorder(uid,access_token);
        }

        Log.e("history===","biked===onResume");
    }

    @Override
    protected void onDestroy() {
        isForeground = false;
        super.onDestroy();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        payLoadingDialog = new LoadingDialog(context);
        payLoadingDialog.setCancelable(false);
        payLoadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("当前行程");

        bikeCode = (TextView)findViewById(R.id.curRoadUI_biked_code);
        bikeNum = (TextView)findViewById(R.id.curRoadUI_biked_num);
        startTime = (TextView)findViewById(R.id.curRoadUI_biked_startTime);
        endTime = (TextView)findViewById(R.id.curRoadUI_biked_endTime);
        timeText = (TextView)findViewById(R.id.curRoadUI_biked_time);
        moneyText = (TextView)findViewById(R.id.curRoadUI_biked_money);
        balanceText = (TextView)findViewById(R.id.curRoadUI_biked_balance);
        payBalanceLayout = (LinearLayout) findViewById(R.id.curRoadUI_biked_payBalanceLayout);

        backImg.setOnClickListener(this);
        payBalanceLayout.setOnClickListener(this);
    }



    private void getCurrentorder(String uid, String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(context, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                        bikeCode.setText("行程编号:"+bean.getOsn());
                        bikeNum.setText(bean.getCodenum());
                        startTime.setText(bean.getSt_time());
                        endTime.setText(bean.getEd_time());
                        timeText.setText(bean.getTotal_mintues());
                        if (bean.getPrices() != null && !"".equals(bean.getPrices())){
                            prices = bean.getPrices();
                        }else {
                            prices = "0.00";
                        }
                        moneyText.setText(prices);
                        if (bean.getUser_money() != null && !"".equals(bean.getUser_money())){
                            user_money = bean.getUser_money();
                        }else {
                            user_money = "0.00";
                        }
                        balanceText.setText(user_money);
                        oid = bean.getOid();
                        if (Double.parseDouble(prices) <= Double.parseDouble(user_money)
                                && Double.parseDouble(prices) <= 5){
                            paySubmit();
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.curRoadUI_biked_payBalanceLayout:
                if (Double.parseDouble(user_money) < Double.parseDouble(prices)){
                    Toast.makeText(context,"当前余额不足,请先充值!",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,RechargeActivity.class);
                }else {
                    paySubmit();
                }
                break;
        }
    }
    private void paySubmit(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT);
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("oid",oid);
            HttpHelper.post(context, Urls.orderPaybalance, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (payLoadingDialog != null && !payLoadingDialog.isShowing()) {
                        payLoadingDialog.setTitle("正在加载");
                        payLoadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (payLoadingDialog != null && payLoadingDialog.isShowing()){
                        payLoadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您,支付成功!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context,HistoryRoadDetailActivity.class);
                            intent.putExtra("oid",oid);
                            startActivity(intent);
                            scrollToFinishActivity();
                        } else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (payLoadingDialog != null && payLoadingDialog.isShowing()){
                        payLoadingDialog.dismiss();
                    }
                }
            });
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
