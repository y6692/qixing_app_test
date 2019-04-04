/*
 * Copyright © 2017 Hubcloud.com.cn. All rights reserved.
 * InterstitialActivity.java
 * AdHubSDK
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 */

package cn.qimate.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hubcloud.adhubsdk.AdHub;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.AdRequest;
import com.hubcloud.adhubsdk.InterstitialAd;
import com.hubcloud.adhubsdk.internal.utilities.StringUtil;

import cn.qimate.test.R;

public class InterstitialActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        Intent intent = getIntent();
        String appId = "2597", adUnitId = "7503";

//        String appId = "277", adUnitId = "960";

        Boolean userDefined = false;
//        if (intent.hasExtra("appId")) {
//            appId = intent.getStringExtra("appId");
//        }
//        if (StringUtil.isEmpty(appId)) {
//            appId = getString(R.string.app_id);
//        }
//        if (intent.hasExtra("adUnitId")) {
//            adUnitId = intent.getStringExtra("adUnitId");
//        }
//        if (StringUtil.isEmpty(adUnitId)) {
//            adUnitId = "7503";
//        }
        if (intent.hasExtra("userDefined")) {
            userDefined = intent.getBooleanExtra("userDefined", false);
        }

        // Initialize the Mobile Ads SDK.
        //不建议在activity里面初始化，请放在application里面
        AdHub.initialize(this, appId);

        // Create the InterstitialAd and set the adUnitId.
        // The second parameter is for userDefined type of ad.
        //创建插屏广告
        mInterstitialAd = new InterstitialAd(this, userDefined);
        // Defined in res/values/strings.xml
        //设置广告位id
        mInterstitialAd.setAdUnitId(adUnitId);
        //注册广告监听
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.i("lance", "onAdClosed");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.i("lance", "onAdClicked");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.i("lance", "onAdFailedToLoad");
            }

            @Override
            public void onAdShown() {
                super.onAdShown();
                Log.i("lance", "onAdShown");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //开发者可以在广告反回的回调中直接调用mInterstitialAd.show()方法展示广告,也可以在其他时机调用
                Log.i("lance", "onAdLoaded");
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        //下载插屏广告资源
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }
}
