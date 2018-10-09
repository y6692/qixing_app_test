package cn.qimate.bike.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bumptech.glide.Glide;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CardinfoBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.service.MyService;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.SHA1;
import cn.qimate.bike.util.SystemUtil;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.umeng.analytics.AnalyticsConfig.getLocation;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity implements OnClickListener,LocationSource,
		AMapLocationListener,AMap.OnCameraChangeListener,AMap.OnMapTouchListener {

	static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private LoadingDialog lockLoading;
	private LoadingDialog loadingDialog;
	private LoadingDialog loadingDialog1;
	public static boolean isForeground = false;

	private ImageView leftBtn,rightBtn;
	private ImageView myLocationBtn,scanLock,linkBtn;
    private LinearLayout myLocationLayout,linkLayout;

//	private AMap aMap;
	private MapView mapView;
//	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;

	private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
	private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
	private boolean mFirstFix = true;
	private LatLng myLocation = null;
	private Circle mCircle;
	private BitmapDescriptor successDescripter;
	private BitmapDescriptor bikeDescripter;
	private Handler handler = new Handler();
	private Marker centerMarker;
	private boolean isMovingMarker = false;
	private Button authBtn;
	private Button rechargeBtn;
	private int Tag = 0;

	private Dialog dialog;

	private ImageView titleImage;
	private ImageView exImage_1;
	private ImageView exImage_2;
	private ImageView exImage_3;

	private LinearLayout marqueeLayout;
	private ImageView closeBtn;
	private Dialog advDialog;
	private ImageView advImageView;
	private ImageView advCloseBtn;
	private String imageUrl;
	private String ad_link;
	private String app_type;
	private String app_id;

	private List<Marker> bikeMarkerList;
	private boolean isUp = false;
	private LinearLayout refreshLayout;
	private Button cartBtn;
	private LinearLayout slideLayout;
	private int imageWith = 0;

	private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
	private static final int PRIVATE_CODE = 1315;//开启GPS权限



	@Override
	@TargetApi(23)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		context = this;

//		registerReceiver(broadcastReceiver2, Config.initFilter());

		isContainsList = new ArrayList<>();
		macList = new ArrayList<>();
		pOptions = new ArrayList<>();


		mapView = (MapView) findViewById(R.id.mainUI_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		bikeMarkerList = new ArrayList<>();
		//注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		imageWith = (int)(getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		new Thread(new Runnable() {
			@Override
			public void run() {
				getNetTime();
			}
		}).start();
		initView();

		ToastUtil.showMessage(this, SharedPreferencesUrls.getInstance().getString("uid","")+"<==>"+SharedPreferencesUrls.getInstance().getString("access_token",""));

	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
		context = this;

		ToastUtil.showMessage(this, "main====onResume");

		closeBroadcast();

		try {
			registerReceiver(Config.initFilter());
			GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
		} catch (Exception e) {
			ToastUtil.showMessage(this, "eee===="+e);
		}



		JPushInterface.onResume(this);
		mapView.onResume();
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		String specialdays = SharedPreferencesUrls.getInstance().getString("specialdays","");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
			authBtn.setVisibility(View.VISIBLE);
			authBtn.setText("您还未登录，点我快速登录");
			authBtn.setEnabled(true);
			cartBtn.setVisibility(View.GONE);
			refreshLayout.setVisibility(View.GONE);
			rechargeBtn.setVisibility(View.GONE);
		}else {
			refreshLayout.setVisibility(View.VISIBLE);
			if (SharedPreferencesUrls.getInstance().getString("iscert","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
				switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert",""))){
					case 1:
						authBtn.setEnabled(true);
						authBtn.setVisibility(View.VISIBLE);
						authBtn.setText("您还未认证，点我快速认证");
						break;
					case 2:
						getCurrentorder1(uid,access_token);
						break;
					case 3:
						authBtn.setEnabled(true);
						authBtn.setVisibility(View.VISIBLE);
						authBtn.setText("认证被驳回，请重新认证");
						break;
					case 4:
						authBtn.setEnabled(false);
						authBtn.setVisibility(View.VISIBLE);
						authBtn.setText("认证审核中");
						break;
				}
			}else {
				authBtn.setVisibility(View.GONE);
			}
			if ("0.00".equals(SharedPreferencesUrls.getInstance().getString("money", ""))
					|| "0".equals(SharedPreferencesUrls.getInstance().getString("money", "")) || SharedPreferencesUrls.getInstance().getString("money", "") == null ||
					"".equals(SharedPreferencesUrls.getInstance().getString("money", ""))){
				rechargeBtn.setVisibility(View.VISIBLE);
			}else {
				rechargeBtn.setVisibility(View.GONE);
			}
			if (("0".equals(specialdays) || specialdays == null || "".equals(specialdays))
					&& ("0".equals(specialdays) || specialdays == null || "".equals(specialdays))){
				cartBtn.setVisibility(View.GONE);
			}else {
				cartBtn.setVisibility(View.VISIBLE);
				cartBtn.setText("免费"+specialdays+"天,每次前半个小时免费,点击续费");
			}
		}

	}

	private void closeBroadcast(){
		try {
			if (internalReceiver != null) {
				unregisterReceiver(internalReceiver);
				internalReceiver = null;
			}
		} catch (Exception e) {
			ToastUtil.showMessage(this, "eee===="+e);
		}
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();

		JPushInterface.onPause(this);
		mapView.onPause();
		deactivate();
//		mFirstFix = false;

		ToastUtil.showMessage(this, "main====onPause");

	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			getCurrentorder1(SharedPreferencesUrls.getInstance().getString("uid",""),
					SharedPreferencesUrls.getInstance().getString("access_token",""));
		}
	};


	private void initView() {

//		LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
//		boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//		if (ok) {//开了定位服务
//			if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
//				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PERMISSION_GRANTED) {// 没有权限，申请权限。
////					ToastUtil.showMessage(this, "====");
//
////					ActivityCompat.requestPermissions(this, LOCATIONGPS, BAIDU_READ_PHONE_STATE);
//
//				} else {
//					getLocation();//getLocation为定位方法
//				}
//			} else {
//				getLocation();//getLocation为定位方法
//			}
//		} else {
//			ToastUtil.showMessageApp(this, "系统检测到未开启GPS定位服务,请开启");
//			Intent intent = new Intent();
//			intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			startActivityForResult(intent, PRIVATE_CODE);
//		}

		if (Build.VERSION.SDK_INT >= 23) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
							REQUEST_CODE_ASK_PERMISSIONS);
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							MainActivity.this.requestPermissions(
									new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
									REQUEST_CODE_ASK_PERMISSIONS);
						}
					});
					customBuilder.create().show();
				}
				return;
			}
		}


		loadingDialog = new LoadingDialog(context);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);

		lockLoading = new LoadingDialog(context);
		lockLoading.setCancelable(false);
		lockLoading.setCanceledOnTouchOutside(false);

		loadingDialog1 = new LoadingDialog(context);
		loadingDialog1.setCancelable(false);
		loadingDialog1.setCanceledOnTouchOutside(false);

		dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
		View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_frist_view, null);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);

		advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
		View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view, null);
		advDialog.setContentView(advDialogView);
		advDialog.setCanceledOnTouchOutside(false);

		marqueeLayout = findViewById(R.id.mainUI_marqueeLayout);

		titleImage = (ImageView)dialogView.findViewById(R.id.ui_fristView_title);
		exImage_1 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_1);
		exImage_2 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_2);
		exImage_3 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_3);
		closeBtn = (ImageView)dialogView.findViewById(R.id.ui_fristView_closeBtn);

		advImageView = (ImageView)advDialogView.findViewById(R.id.ui_adv_image);
		advCloseBtn = (ImageView)advDialogView.findViewById(R.id.ui_adv_closeBtn);

		LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) advImageView.getLayoutParams();
		params4.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		advImageView.setLayoutParams(params4);

		leftBtn = (ImageView) findViewById(R.id.mainUI_leftBtn);
		rightBtn = (ImageView) findViewById(R.id.mainUI_rightBtn);
        myLocationLayout =  (LinearLayout) findViewById(R.id.mainUI_myLocationLayout);
        linkLayout = (LinearLayout) findViewById(R.id.mainUI_linkServiceLayout);
		myLocationBtn = (ImageView) findViewById(R.id.mainUI_myLocation);
		scanLock = (ImageView) findViewById(R.id.mainUI_scanCode_lock);
		linkBtn = (ImageView) findViewById(R.id.mainUI_linkService_btn);
		authBtn = (Button)findViewById(R.id.mainUI_authBtn);
		cartBtn = (Button)findViewById(R.id.mainUI_cartBtn);
		rechargeBtn = (Button)findViewById(R.id.mainUI_rechargeBtn);
		refreshLayout = (LinearLayout) findViewById(R.id.mainUI_refreshLayout);
		slideLayout = findViewById(R.id.mainUI_slideLayout);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);
		aMap.getUiSettings()
				.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
		CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18);// 设置缩放监听
		aMap.moveCamera(cameraUpdate);
		successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
		bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
		setUpLocationStyle();

		aMap.setOnMapTouchListener(MainActivity.this);

		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		marqueeLayout.setOnClickListener(this);
		myLocationBtn.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        linkLayout.setOnClickListener(this);
		scanLock.setOnClickListener(this);
		linkBtn.setOnClickListener(this);
		authBtn.setOnClickListener(this);
		rechargeBtn.setOnClickListener(this);
		refreshLayout.setOnClickListener(this);
		advImageView.setOnClickListener(this);
		advCloseBtn.setOnClickListener(this);
		cartBtn.setOnClickListener(this);
		slideLayout.setOnClickListener(this);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
		params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
		titleImage.setLayoutParams(params);

		LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) exImage_1.getLayoutParams();
		params1.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
		exImage_1.setLayoutParams(params1);

		LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) exImage_2.getLayoutParams();
		params2.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
		exImage_2.setLayoutParams(params2);

		LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) exImage_3.getLayoutParams();
		params3.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
		exImage_3.setLayoutParams(params3);

		if (SharedPreferencesUrls.getInstance().getBoolean("ISFRIST",true)){
			SharedPreferencesUrls.getInstance().putBoolean("ISFRIST",false);
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
			dialog.getWindow().setAttributes(lp);
			dialog.show();
		}else {
			initHttp();
		}
		exImage_1.setOnClickListener(myOnClickLister);
		exImage_2.setOnClickListener(myOnClickLister);
		closeBtn.setOnClickListener(myOnClickLister);

	}
	private OnClickListener myOnClickLister = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_fristView_exImage_1:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    UIHelper.goWebViewAct(context,"使用说明",Urls.bluecarisee);
                    break;
                case R.id.ui_fristView_exImage_2:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    UIHelper.goWebViewAct(context,"使用说明",Urls.useHelp);
                    break;
                case R.id.ui_fristView_closeBtn:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

	/**
	 * 获取广告
	 * */
	private void initHttp(){
		RequestParams params = new RequestParams();
		params.put("adsid","11");
		if (SharedPreferencesUrls.getInstance().getString("uid","") != null &&
				!"".equals(SharedPreferencesUrls.getInstance().getString("uid",""))){
			params.put("uid",SharedPreferencesUrls.getInstance().getString("uid",""));
		}
		if (SharedPreferencesUrls.getInstance().getString("access_token","") != null &&
				!"".equals(SharedPreferencesUrls.getInstance().getString("access_token",""))){
			params.put("access_token",SharedPreferencesUrls.getInstance().getString("access_token",""));
		}
		HttpHelper.get(context, Urls.getIndexAd, params, new TextHttpResponseHandler() {
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
						JSONArray jsonArray = new JSONArray(result.getData());
						for (int i = 0; i < jsonArray.length();i++){
							imageUrl = jsonArray.getJSONObject(i).getString("ad_file");
							ad_link = jsonArray.getJSONObject(i).getString("ad_link");
							app_type = jsonArray.getJSONObject(i).getString("app_type");
							app_id = jsonArray.getJSONObject(i).getString("app_id");
							ad_link = jsonArray.getJSONObject(i).getString("ad_link");

						}
						if (!SharedPreferencesUrls.getInstance().getBoolean("ISFRIST",false)){
							if (imageUrl != null && !"".equals(imageUrl)){
								WindowManager windowManager = getWindowManager();
								Display display = windowManager.getDefaultDisplay();
								WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
								lp.width = (int) (display.getWidth() * 0.8);
								lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
								advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
								advDialog.getWindow().setAttributes(lp);
								advDialog.show();
								// 加载图片
								Glide.with(context).load(imageUrl).into(advImageView);
							}
						}
					}
				}catch (Exception e){
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}

	private void addChooseMarker() {
		// 加入自定义标签
		MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
		centerMarker = aMap.addMarker(centerMarkerOption);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				CameraUpdate update = CameraUpdateFactory.zoomTo(18);
				aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
					@Override
					public void onFinish() {
						aMap.setOnCameraChangeListener(MainActivity.this);
					}

					@Override
					public void onCancel() {

					}
				});
			}
		}, 1000);
	}

	private void setMovingMarker() {
		if (isMovingMarker)
			return;

		isMovingMarker = true;
		centerMarker.setIcon(successDescripter);
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		if (centerMarker != null) {
			setMovingMarker();
		}
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		if (isUp){
			initNearby(cameraPosition.target.latitude,cameraPosition.target.longitude);
			if (centerMarker != null) {
				animMarker();
			}
		}
	}

	@Override
	public void onTouch(MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
				motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE
				|| motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
			isUp = true;
		}else {
			isUp = false;
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}





	@Override
	public void onClick(View view) {
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		switch (view.getId()){
			case R.id.mainUI_leftBtn:

//				if (loadingDialog != null && loadingDialog.isShowing()){
//					loadingDialog.dismiss();
//				}
//				if (lockLoading != null && lockLoading.isShowing()){
//					lockLoading.dismiss();
//				}
//				if (loadingDialog1 != null && loadingDialog1.isShowing()){
//					loadingDialog1.dismiss();
//				}


//				UIHelper.goToAct(context, Main2Activity.class);
				UIHelper.goToAct(MainActivity.this, ActionCenterActivity.class);

//                if (loadingDialog != null && loadingDialog.isShowing()){
//					loadingDialog.dismiss();
//				}

//				UIHelper.goToAct(context, Main2Activity.class);
//				UIHelper.goToAct(context, CurRoadBikingActivity.class);


				break;
			case R.id.mainUI_rightBtn:
				if (SharedPreferencesUrls.getInstance().getString("uid","") == null || "".equals(
						SharedPreferencesUrls.getInstance().getString("access_token",""))){
					UIHelper.goToAct(context,LoginActivity.class);
					ToastUtil.showMessageApp(context,"请先登录你的账号");
					return;
				}
				UIHelper.goToAct(context, PersonAlterActivity.class);
				break;
			case R.id.mainUI_marqueeLayout:

				break;
            case R.id.mainUI_myLocationLayout:
			case R.id.mainUI_myLocation:
				if (myLocation != null) {
					CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
					aMap.animateCamera(update);
				}
				break;
			case R.id.mainUI_scanCode_lock:


				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
					ToastUtil.showMessageApp(context,"请先登录账号");
					UIHelper.goToAct(context,LoginActivity.class);
					return;
				}
				if (SharedPreferencesUrls.getInstance().getString("iscert","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
					switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert",""))){
						case 1:
							ToastUtil.showMessageApp(context,"您还未认证,请先认证");
							UIHelper.goToAct(context,RealNameAuthActivity.class);
							break;
						case 2:
							getCurrentorder(uid,access_token);
							break;
						case 3:
							ToastUtil.showMessageApp(context,"认证被驳回，请重新认证");
							UIHelper.goToAct(context,RealNameAuthActivity.class);
							break;
						case 4:
							ToastUtil.showMessageApp(context,"认证审核中");
							break;
					}
				}else {
					ToastUtil.showMessage(context,"您还未认证,请先认证");
				}
				break;
            case R.id.mainUI_linkServiceLayout:
			case R.id.mainUI_linkService_btn:
				initmPopupWindowView();
				break;
			case R.id.mainUI_authBtn:
				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
					UIHelper.goToAct(context,LoginActivity.class);
				}else {
					if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
						switch (Tag){
							case 0:
								closeBroadcast();

								UIHelper.goToAct(context, CurRoadBikingActivity.class);
								break;
							case 1:
								UIHelper.goToAct(context,CurRoadBikedActivity.class);
								break;
							default:
								break;
						}
					}else {
						UIHelper.goToAct(context,RealNameAuthActivity.class);
					}
				}
				break;
			case R.id.mainUI_rechargeBtn:
				UIHelper.goToAct(context,MyPurseActivity.class);
				break;
			case R.id.mainUI_refreshLayout:
				RefreshLogin();
				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
					UIHelper.goToAct(context,LoginActivity.class);
				}else {
					new MyAsyncTask().execute();
					if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
						authBtn.setVisibility(View.VISIBLE);
						authBtn.setText("您还未登录，点我快速登录");
						authBtn.setEnabled(true);
					} else {
						if (SharedPreferencesUrls.getInstance().getString("iscert", "") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
							switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
								case 1:
									authBtn.setEnabled(true);
									authBtn.setVisibility(View.VISIBLE);
									authBtn.setText("您还未认证，点我快速认证");
									break;
								case 2:
									getCurrentorder1(uid, access_token);
									break;
								case 3:
									authBtn.setEnabled(true);
									authBtn.setVisibility(View.VISIBLE);
									authBtn.setText("认证被驳回，请重新认证");
									break;
								case 4:
									authBtn.setEnabled(false);
									authBtn.setVisibility(View.VISIBLE);
									authBtn.setText("认证审核中");
									break;
							}
						} else {
							authBtn.setVisibility(View.GONE);
						}
					}
					if ("0.00".equals(SharedPreferencesUrls.getInstance().getString("money", ""))||
							"0".equals(SharedPreferencesUrls.getInstance().getString("money", "")) ||
							SharedPreferencesUrls.getInstance().getString("money", "") == null ||
							"".equals(SharedPreferencesUrls.getInstance().getString("money", ""))){
						rechargeBtn.setVisibility(View.VISIBLE);
					}else {
						rechargeBtn.setVisibility(View.GONE);
					}
				}
				break;
			case R.id.ui_adv_image:
				UIHelper.bannerGoAct(context,app_type,app_id,ad_link);
				break;
			case R.id.ui_adv_closeBtn:
				if (advDialog != null && advDialog.isShowing()) {
					advDialog.dismiss();
				}
				break;
			case R.id.mainUI_cartBtn:
				UIHelper.goToAct(context,PayMontCartActivity.class);
				break;
			case R.id.mainUI_slideLayout:
				UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + uid);
				break;
			default:
				break;
		}
	}



	private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			if (loadingDialog != null && !loadingDialog.isShowing()) {
				loadingDialog.setTitle("正在刷新");
				loadingDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			ToastUtil.showMessage(context, "刷新成功");
		}
	}

	public void initmPopupWindowView(){

		// 获取自定义布局文件的视图
		View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);
		// 创建PopupWindow宽度和高度
		RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
		ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
		// 获取截图的Bitmap
		Bitmap bitmap = UtilScreenCapture.getDrawing(this);
		if (bitmap != null) {
			// 将截屏Bitma放入ImageView
			iv_popup_window_back.setImageBitmap(bitmap);
			// 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
			UtilBitmap.blurImageView(this, iv_popup_window_back, 10,0xAA000000);
		} else {
			// 获取的Bitmap为null时，用半透明代替
			iv_popup_window_back.setBackgroundColor(0x77000000);
		}
		// 打开弹窗
		UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
		// 创建PopupWindow宽度和高度
		final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT, true);
		/**
		 * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
		 */
		popupwindow.setAnimationStyle(R.style.PopupAnimation);
		popupwindow.setOutsideTouchable(false);

		LinearLayout feedbackLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_feedbackLayout);
		LinearLayout helpLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_helpLayout);
		final LinearLayout callLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_callLayout);
		TextView cancleBtn = (TextView)customView.findViewById(R.id.pop_menu_cancleBtn);

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.pop_menu_feedbackLayout:
						UIHelper.goToAct(context,FeedbackActivity.class);
						break;
					case R.id.pop_menu_helpLayout:
						WindowManager windowManager = getWindowManager();
						Display display = windowManager.getDefaultDisplay();
						WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
						lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
						lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
						dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
						dialog.getWindow().setAttributes(lp);
						dialog.show();
						break;
					case R.id.pop_menu_callLayout:
						if (Build.VERSION.SDK_INT >= 23) {
							int checkPermission = MainActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE);
							if (checkPermission != PERMISSION_GRANTED) {
								if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
									requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
								} else {
									CustomDialog.Builder customBuilder = new CustomDialog.Builder(MainActivity.this);
									customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
											.setNegativeButton("取消", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
												}
											}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
											MainActivity.this.requestPermissions(
													new String[] { Manifest.permission.CALL_PHONE }, 0);
										}
									});
									customBuilder.create().show();
								}
								return;
							}
						}
						CustomDialog.Builder customBuilder = new CustomDialog.Builder(MainActivity.this);
						customBuilder.setTitle("温馨提示").setMessage("确认拨打" + "0519-86999222" + "吗?")
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								Intent intent=new Intent();
								intent.setAction(Intent.ACTION_CALL);
								intent.setData(Uri.parse("tel:" + "0519-86999222"));
								startActivity(intent);
							}
						});
						customBuilder.create().show();
						break;
					case R.id.pop_menu_cancleBtn:

						break;
				}
				popupwindow.dismiss();
			}
		};

		feedbackLayout.setOnClickListener(listener);
		helpLayout.setOnClickListener(listener);
		callLayout.setOnClickListener(listener);
		cancleBtn.setOnClickListener(listener);

		popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
						if ("[]".equals(result.getData()) || 0 == result.getData().length()){
							SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
							cardCheck();
						}else {
							if (loadingDialog != null && loadingDialog.isShowing()){
								loadingDialog.dismiss();
							}
							CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);

//							m_nowMac = bean.getMacinfo();
//							ToastUtil.showMessage(context, "###===="+m_nowMac);

							if ("1".equals(bean.getStatus())){
								SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
								if (loadingDialog != null && loadingDialog.isShowing()){
									loadingDialog.dismiss();
								}
								closeBroadcast();
								UIHelper.goToAct(context, CurRoadBikingActivity.class);
							}else {
								SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
								if (loadingDialog != null && loadingDialog.isShowing()){
									loadingDialog.dismiss();
								}
								UIHelper.goToAct(context,CurRoadBikedActivity.class);
							}
						}
					} else {
						ToastUtil.showMessage(context,result.getMsg());
					}
				} catch (Exception e) {
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}
	/**
	 *
	 * 保险接口
	 * */
	private void cardCheck() {

		String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
			ToastUtils.show("请先登录您的账号");
			UIHelper.goToAct(context, LoginActivity.class);
		} else {
			RequestParams params = new RequestParams();
			params.put("uid", uid);
			params.put("access_token", access_token);
			HttpHelper.get(context, Urls.useinfo, params, new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					if (loadingDialog1 != null && !loadingDialog1.isShowing()) {
						loadingDialog1.setTitle("正在提交");
						loadingDialog1.show();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					if (loadingDialog1 != null && loadingDialog1.isShowing()) {
						loadingDialog1.dismiss();
					}
					UIHelper.ToastError(context, throwable.toString());
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
						if (result.getFlag().equals("Success")) {
							CardinfoBean bean = JSON.parseObject(result.getData(), CardinfoBean.class);
							if (!"2".equals(bean.getCardcheck())){
								CustomDialog.Builder customBuilder = new CustomDialog.Builder(MainActivity.this);
								customBuilder.setTitle("温馨提示").setMessage("为了您的骑行安全，请上传身份证完善保险信息")
										.setNegativeButton("去上传", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												Intent intent1 = new Intent(context,InsureanceActivity.class);
												intent1.putExtra("isBack",true);
												context.startActivity(intent1);
												dialog.cancel();
											}
										}).setPositiveButton("直接用车", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										if (Build.VERSION.SDK_INT >= 23) {
											int checkPermission = MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA);
											if (checkPermission != PERMISSION_GRANTED) {
												if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
													requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
												} else {
													CustomDialog.Builder customBuilder1 = new CustomDialog.Builder(MainActivity.this);
													customBuilder1.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
															.setNegativeButton("取消", new DialogInterface.OnClickListener() {
																public void onClick(DialogInterface dialog, int which) {
																	dialog.cancel();
																}
															}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.cancel();
															MainActivity.this.requestPermissions(
																	new String[] { Manifest.permission.CAMERA },
																	100);
														}
													});
													customBuilder1.create().show();
												}
												if (loadingDialog1 != null && loadingDialog1.isShowing()){
													loadingDialog1.dismiss();
												}
												return;
											}
										}
										try {

											closeBroadcast();

											Intent intent = new Intent();
											intent.setClass(MainActivity.this, ActivityScanerCode.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivityForResult(intent, SCANNIN_GREQUEST_CODE);



										} catch (Exception e) {
											UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
										}
										dialog.cancel();
										if (loadingDialog1 != null && loadingDialog1.isShowing()){
											loadingDialog1.dismiss();
										}
									}
								});
								customBuilder.create().show();
							}else {
								if (Build.VERSION.SDK_INT >= 23) {
									int checkPermission = MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA);
									if (checkPermission != PERMISSION_GRANTED) {
										if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
											requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
										} else {
											CustomDialog.Builder customBuilder1 = new CustomDialog.Builder(MainActivity.this);
											customBuilder1.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
													.setNegativeButton("取消", new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.cancel();
														}
													}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
													MainActivity.this.requestPermissions(
															new String[] { Manifest.permission.CAMERA },
															100);
												}
											});
											customBuilder1.create().show();
										}
										if (loadingDialog1 != null && loadingDialog1.isShowing()){
											loadingDialog1.dismiss();
										}
										return;
									}
								}
								if (loadingDialog1 != null && loadingDialog1.isShowing()){
									loadingDialog1.dismiss();
								}
								try {
									closeBroadcast();

									Intent intent = new Intent();
									intent.setClass(MainActivity.this, ActivityScanerCode.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivityForResult(intent, SCANNIN_GREQUEST_CODE);


								} catch (Exception e) {
									UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
								}
							}
						} else {
							ToastUtils.show(result.getMsg());
						}
					} catch (Exception e) {
					}
					if (loadingDialog1 != null && loadingDialog1.isShowing()) {
						loadingDialog1.dismiss();
					}
				}
			});
		}
	}
	/**
	 *
	 * 附近车接口
	 *
	 * */
	private void initNearby(double latitude, double longitude){
		RequestParams params = new RequestParams();
		params.put("latitude",latitude);
		params.put("longitude",longitude);
		HttpHelper.get(context, Urls.nearby, params, new TextHttpResponseHandler() {
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
						JSONArray array = new JSONArray(result.getData());
						for (Marker marker : bikeMarkerList){
							if (marker != null){
								marker.remove();
							}
						}
						if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
							bikeMarkerList.clear();
						}
						if (0 == array.length()){
							ToastUtils.show("附近没有自行车");
						}else {
							for (int i = 0; i < array.length(); i++){
								NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
								// 加入自定义标签
								MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(
										Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
								Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
								bikeMarkerList.add(bikeMarker);
							}
						}
					} else {
						ToastUtils.show(result.getMsg());
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try{
				CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
				customBuilder.setTitle("温馨提示").setMessage("确认退出吗?")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						AppManager.getAppManager().AppExit(context);
					}
				});
				customBuilder.create().show();
				return true;
			}catch (Exception e){

			}
		}
		return super.onKeyDown(keyCode, event);
	}


	private void getCurrentorder1(String uid, String access_token){
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
						if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
							if ("[]".equals(result.getData()) || 0 == result.getData().length()){
								authBtn.setEnabled(false);
								authBtn.setVisibility(View.GONE);
							}else {
								CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
								if ("1".equals(bean.getStatus())){
									SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
									authBtn.setText("您有一条进行中的行程，点我查看");
									Tag = 0;
								}else {
									SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
									authBtn.setText("您有一条未支付的行程，点我查看");
									Tag = 1;
								}
								authBtn.setVisibility(View.VISIBLE);
								authBtn.setEnabled(true);
							}
						}
					} else {
						ToastUtils.show(result.getMsg());
					}
				} catch (Exception e) {
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		ToastUtil.showMessage(context, "main===onDestroy");

		mapView.onDestroy();
		if(null != mlocationClient){
			mlocationClient.onDestroy();
		}
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}

//		if (broadcastReceiver2 != null) {
//			unregisterReceiver(broadcastReceiver2);
//			broadcastReceiver2 = null;
//		}

		try {
			if (internalReceiver != null) {
				unregisterReceiver(internalReceiver);
				internalReceiver = null;
			}
		} catch (Exception e) {
		}

	}
	private void setUpLocationStyle() {
		// 自定义系统定位蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
		myLocationStyle.strokeWidth(0);
		myLocationStyle.strokeColor(R.color.main_theme_color);
		myLocationStyle.radiusFillColor(Color.TRANSPARENT);
		aMap.setMyLocationStyle(myLocationStyle);
	}


	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
	    super.activate(listener);
//		mListener = listener;
//		if (mlocationClient == null) {
//			mlocationClient = new AMapLocationClient(this);
//			mLocationOption = new AMapLocationClientOption();
//			//设置定位监听
//			mlocationClient.setLocationListener(this);
//			//设置为高精度定位模式
//			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
//			mLocationOption.setInterval(60 * 1000);
//			//设置定位参数
//			mlocationClient.setLocationOption(mLocationOption);
//			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//			// 在定位结束后，在合适的生命周期调用onDestroy()方法
//			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//			mlocationClient.startLocation();
//		}
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		super.onLocationChanged(amapLocation);
//		if (mListener != null && amapLocation != null) {
//			if (amapLocation != null
//					&& amapLocation.getErrorCode() == 0) {
//				if (mListener != null) {
//					mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//				}
//				myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//				if (mFirstFix){
//					mFirstFix = false;
//					addChooseMarker();
//					addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆
//					initNearby(amapLocation.getLatitude(),amapLocation.getLongitude());
//				} else {
//					centerMarker.setPosition(myLocation);
//					mCircle.setCenter(myLocation);
//				}
//				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
//				//保存经纬度到本地
//				SharedPreferencesUrls.getInstance().putString("latitude",""+amapLocation.getLatitude());
//				SharedPreferencesUrls.getInstance().putString("longitude",""+amapLocation.getLongitude());
//			}
//		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	/**
	 * 添加Circle
	 * @param latlng  坐标
	 * @param radius  半径
	 */
	private void addCircle(LatLng latlng, double radius) {
		CircleOptions options = new CircleOptions();
		options.strokeWidth(1f);
		options.fillColor(FILL_COLOR);
		options.strokeColor(STROKE_COLOR);
		options.center(latlng);
		options.radius(radius);
		mCircle = aMap.addCircle(options);
	}

	private ValueAnimator animator = null;

	private void animMarker() {
		isMovingMarker = false;
		if (animator != null) {
			animator.start();
			return;
		}
		animator = ValueAnimator.ofFloat(mapView.getHeight() / 2, mapView.getHeight() / 2 - 30);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.setDuration(150);
		animator.setRepeatCount(1);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float value = (Float) animation.getAnimatedValue();
				centerMarker.setPositionByPixels(mapView.getWidth() / 2, Math.round(value));
			}
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				centerMarker.setIcon(successDescripter);
			}
		});
		animator.start();
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 0:
				if (grantResults[0] == PERMISSION_GRANTED) {
					// Permission Granted
					if (permissions[0].equals(Manifest.permission.CALL_PHONE)){
						Intent intent=new Intent();
						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + "0519-86999222"));
						startActivity(intent);
					}
				}else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开电话权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
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
				break;
			case 100:
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				if (grantResults[0] == PERMISSION_GRANTED) {
					// Permission Granted
					if (permissions[0].equals(Manifest.permission.CAMERA)){
						try {
							closeBroadcast();

							Intent intent = new Intent();
							intent.setClass(MainActivity.this, ActivityScanerCode.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

						} catch (Exception e) {
							UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
						}
					}
				}else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取相机权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
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
				break;
			case REQUEST_CODE_ASK_PERMISSIONS:
				if (grantResults[0] == PERMISSION_GRANTED) {
					// Permission Granted
					if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
						if (aMap == null) {
							aMap = mapView.getMap();
							setUpMap();
						}
						aMap.getUiSettings().setZoomControlsEnabled(false);
						aMap.getUiSettings().setMyLocationButtonEnabled(false);
						aMap.getUiSettings()
								.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
						CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18);// 设置缩放监听
						aMap.moveCamera(cameraUpdate);
						setUpLocationStyle();
					}
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
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
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	//获取网络时间
	private void getNetTime() {
		URL url = null;//取得资源对象
		final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
//			url = new URL("http://www.baidu.com");
			url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
			//url = new URL("http://www.bjtime.cn");
			URLConnection uc = url.openConnection();//生成连接对象
			uc.connect(); //发出连接
			long ld = uc.getDate(); //取得网站日期时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(ld);
			final String format = formatter.format(calendar.getTime());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (SharedPreferencesUrls.getInstance().getString("date","") != null &&
							!"".equals(SharedPreferencesUrls.getInstance().getString("date",""))){
						if (!format.equals(SharedPreferencesUrls.getInstance().getString("date",""))){
							UpdateManager.getUpdateManager().checkAppUpdate(context, true);
							SharedPreferencesUrls.getInstance().putString("date",""+format);
						}
					}else {
						// 版本更新
						UpdateManager.getUpdateManager().checkAppUpdate(context, true);
						SharedPreferencesUrls.getInstance().putString("date",""+format);
					}
				}
			});
		} catch (Exception e) {
			String date = formatter.format(new Date());
			if (SharedPreferencesUrls.getInstance().getString("date","") != null &&
					!"".equals(SharedPreferencesUrls.getInstance().getString("date",""))){
				if (!date.equals(SharedPreferencesUrls.getInstance().getString("date",""))){
					UpdateManager.getUpdateManager().checkAppUpdate(context, true);
					SharedPreferencesUrls.getInstance().putString("date",""+date);
				}
			}else {
				// 版本更新
				UpdateManager.getUpdateManager().checkAppUpdate(context, true);
				SharedPreferencesUrls.getInstance().putString("date",""+date);
			}
			e.printStackTrace();
		}
	}

	@Override
	protected void handleReceiver(Context context, Intent intent) {
		// 广播处理
		if (intent == null) {
			return;
		}

		String action = intent.getAction();
		String data = intent.getStringExtra("data");
		switch (action) {
			case Config.TOKEN_ACTION:

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						BaseApplication.getInstance().getIBLE().getBattery();
					}
				}, 500);
				if (null != lockLoading && lockLoading.isShowing()) {
					lockLoading.dismiss();
				}
//					isStop = true;
				ToastUtil.showMessage(context,"main===设备连接成功");

				break;
			case Config.BATTERY_ACTION:
//					ToastUtil.showMessage(context,"####===2");
				break;
			case Config.OPEN_ACTION:
				ToastUtil.showMessage(context,"####===3");
				break;
			case Config.CLOSE_ACTION:
				ToastUtil.showMessage(context,"####===4");
				break;
			case Config.LOCK_STATUS_ACTION:
//				if (CurRoadBikingActivity.instance.loadingDialog != null && CurRoadBikingActivity.instance.loadingDialog.isShowing()){
//					CurRoadBikingActivity.instance.loadingDialog.dismiss();
//				}
//				if (CurRoadBikingActivity.instance.lockLoading != null && CurRoadBikingActivity.instance.lockLoading.isShowing()){
//					CurRoadBikingActivity.instance.lockLoading.dismiss();
//				}

				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				if (lockLoading != null && lockLoading.isShowing()){
					lockLoading.dismiss();
				}

				if (TextUtils.isEmpty(data)) {

					ToastUtil.showMessageApp(context,"main====锁已关闭");

					//锁已关闭
					submit(context, uid, access_token);

				} else {
					//锁已开启
					ToastUtil.showMessageApp(context,"main====您还未上锁，请给车上锁后还车");
				}
				break;
			case Config.LOCK_RESULT:
//				if (CurRoadBikingActivity.instance.loadingDialog != null && CurRoadBikingActivity.instance.loadingDialog.isShowing()){
//					CurRoadBikingActivity.instance.loadingDialog.dismiss();
//				}
//				if (CurRoadBikingActivity.instance.lockLoading != null && CurRoadBikingActivity.instance.lockLoading.isShowing()){
//					CurRoadBikingActivity.instance.lockLoading.dismiss();
//				}

				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				if (lockLoading != null && lockLoading.isShowing()){
					lockLoading.dismiss();
				}

//				if(context instanceof  CurRoadStartActivity){
//					ToastUtil.showMessage(context,"s===恭喜您，您已成功上锁");
//				}else{
//					ToastUtil.showMessage(context,"####===恭喜您，您已成功上锁");
//				}

				ToastUtil.showMessageApp(context,"main===恭喜您，您已成功上锁");

				endBtn(context);

				break;
		}
	}
}