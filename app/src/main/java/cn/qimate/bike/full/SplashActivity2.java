/*
 * Copyright © 2017 Hubcloud.com.cn. All rights reserved.
 * SplashActivity.java
 * AdHubSDK
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 */

package cn.qimate.bike.full;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hubcloud.adhubsdk.AdHub;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.SplashAd;

import cn.jpush.android.api.JPushInterface;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.Main4Activity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.util.ToastUtil;

/**
 * 实时开屏，广告实时请求并且立即展现
 */
public class SplashActivity2 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        String app_id = (String) SharedPreferencesUtils.get(this, "app_id", "277");
//        String splashAdUnitId = (String) SharedPreferencesUtils.get(this, "splash_ad_unit_id", "974");

        CrashHandler.getInstance().init(this);

        if (SharedPreferencesUrls.getInstance().getBoolean("isStop", true)) {
            SharedPreferencesUrls.getInstance().putString("m_nowMac", "");
        }
        if ("".equals(SharedPreferencesUrls.getInstance().getString("m_nowMac", ""))) {
            SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
            SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
        }

        ToastUtil.showMessage(this, SharedPreferencesUrls.getInstance().getBoolean("isStop", true) + "===" + SharedPreferencesUrls.getInstance().getString("m_nowMac", ""));

        init();
    }


    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                }
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
                }
                return;
            }
        }
        // <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                return;
            }
        }

//		if (null == locationOption) {
//			locationOption = new AMapLocationClientOption();
//		}
        initjpush();
        registerMessageReceiver();
//		initLocation();


        String app_id = "2597";
        String splashAdUnitId = "7502";

        AdHub.initialize(this, app_id);
        // adUnitContainer
        FrameLayout adsParent = (FrameLayout) this.findViewById(R.id.adsFl);

        // the observer of AD
        AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("SplashActivity", "onAdLoaded");
                Toast.makeText(SplashActivity2.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShown() {
                Log.i("SplashActivity", "onAdShown");
                Toast.makeText(SplashActivity2.this, "onAdShown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("SplashActivity", "onAdFailedToLoad");
                Toast.makeText(SplashActivity2.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                jump();
            }

            @Override
            public void onAdClosed() {
                Log.i("SplashActivity", "onAdClosed");
                jumpWhenCanClick(); // 跳转至您的应用主界面
                Toast.makeText(SplashActivity2.this, "onAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.i("SplashActivity", "onAdClick");
                // 设置开屏可接受点击时，该回调可用
            }
        };
        SplashAd splashAd = new SplashAd(this, adsParent, listener, splashAdUnitId);
        splashAd.setCloseButtonPadding(10, 20, 10, 10);

    }

    // 初始化极光
    private void initjpush() {
        JPushInterface.init(getApplicationContext()); // 初始化 JPush
    }




    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (extras != null && !"".equals(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        Log.d("SplashActivity", "canJumpImmediately:" + canJumpImmediately);
        if (canJumpImmediately) {
//            this.startActivity(new Intent(SplashActivity2.this, MainActivity.class));
//            this.finish();

            jump();
        } else {
            canJumpImmediately = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);

        Log.d("SplashActivity", "onPause:" + canJumpImmediately);
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
//        this.startActivity(new Intent(SplashActivity2.this, MainActivity.class));
//        this.finish();

        if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true) && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
            UIHelper.goToAct(this, MainActivity.class);
        } else {
            SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
            SharedPreferencesUrls.getInstance().putInt("version", getVersion());
            UIHelper.goToAct(this, EnterActivity.class);
        }

//        UIHelper.goToAct(this, Main4Activity.class);
        finishMine();
    }

    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        Log.d("SplashActivity", "onPause:" + canJumpImmediately);
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//								return;
//							}
//						}
//					}
                    init();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开存储空间权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                return;
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//										Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//												Manifest.permission.WRITE_EXTERNAL_STORAGE },
//										0);
//								return;
//							}
//						}
//					}
                    init();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许设备信息权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                return;
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里定位权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
