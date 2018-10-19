package cn.qimate.bike.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.HistoryRoadDetailActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.Main2Activity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.ble.utils.ParseLeAdvData;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.util.ToastUtil;

import static cn.qimate.bike.activity.CurRoadBikingActivity.bytes2hex03;
import static cn.qimate.bike.core.common.Urls.schoolrangeList;

public class BaseFragmentActivity extends AppCompatActivity implements
//		LocationSource,
// 		AMapLocationListener,
 		OnConnectionListener
{

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;

	protected InternalReceiver internalReceiver = null;

	private TelephonyManager tm;
	protected Context context;

	public static String m_nowMac = "";  //"A8:1B:6A:B4:E7:C9"
	public static String oid = "";
	public static String osn = "";
	public static String type = "";

	public static List<Boolean> isContainsList;
	public static List<String> macList;
	public static List<Polygon> pOptions;
	private LatLng myLocation = null;
	private boolean mFirstFix = true;
//	protected OnLocationChangedListener mListener;
	protected AMapLocationClient mlocationClient;
	protected AMapLocationClientOption mLocationOption;
	protected AMap aMap;
	protected BitmapDescriptor successDescripter;
	private Marker centerMarker;
	private Circle mCircle;

	protected String uid = "";
    protected String access_token = "";

	public static double referLatitude = 0.0;
	public static double referLongitude = 0.0;

	protected BluetoothAdapter mBluetoothAdapter;
	public static LoadingDialog loadingDialog;
	public static LoadingDialog lockLoading;

	protected int tz = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;


//		registerReceiver(Config.initFilter());
//		GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		// 修改状态栏颜色，4.4+生效
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

//		ToastUtil.showMessage(this, "===="+m_nowMac);

        uid = SharedPreferencesUrls.getInstance().getString("uid","");
        access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

		m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
		oid = SharedPreferencesUrls.getInstance().getString("oid", "");
		osn = SharedPreferencesUrls.getInstance().getString("osn", "");
		type = SharedPreferencesUrls.getInstance().getString("type", "");


        //|| context instanceof CurRoadStartActivity || context instanceof ActivityScanerCode  || context instanceof CurRoadBikingActivity

//		if(context instanceof MainActivity ){
////		if(context instanceof MainActivity){
//
//			if("".equals(m_nowMac)){
//				getCurrentorder(uid, access_token);
//			}
////			else if(context instanceof MainActivity){
////				connect();
////			}
//		}


	}

	@Override
	protected void onResume() {
		super.onResume();
		RefreshLogin();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

//		ToastUtil.showMessage(context, "base===onDestroy==="+type);



//		try {
//			if (internalReceiver!= null) {
//				unregisterReceiver(internalReceiver);
//			}
//		} catch (Exception e) {
//			ToastUtil.showMessage(context, "eee==="+e);
//		}

		AppManager.getAppManager().finishActivity(this);
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
						ToastUtil.showMessageApp(context,"数据更新成功");
						if ("[]".equals(result.getData()) || 0 == result.getData().length()){
							ToastUtil.showMessage(context,"mmmmmmmm");

//							ToastUtil.showMessageApp(context,"当前无行程");
//							BaseApplication.getInstance().getIBLE().refreshCache();
//							BaseApplication.getInstance().getIBLE().close();
//							BaseApplication.getInstance().getIBLE().disconnect();
//
//							finish();
//							scrollToFinishActivity();
						}else {
							CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
//							bikeCode = bean.getCodenum();
//							bikeCodeText.setText(bikeCode);
//							time.setText(bean.getSt_time());
							oid = bean.getOid();
							osn = bean.getOsn();
//							password = bean.getPassword();
							type = bean.getType();





							if ("1".equals(bean.getType())){
//								hintText.setText("还车须至校园地图红色覆盖区，关锁并拨乱密码后点击结束！");
//								lookPsdBtn.setText("查看密码");
							}else {
//								hintText.setText("还车须至校园地图红色覆盖区，关锁后距车一米内点击结束！");
								m_nowMac = bean.getMacinfo();

//								ToastUtil.showMessage(context, "###===="+m_nowMac);

//								connect();

//								lookPsdBtn.setText("再次开锁");
//								if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//									ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//									finish();
//								}
//								//蓝牙锁
//								BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//
//								mBluetoothAdapter = bluetoothManager.getAdapter();
//
//								if (mBluetoothAdapter == null) {
//									ToastUtil.showMessageApp(context, "获取蓝牙失败");
//									finish();
//									return;
//								}
//
//								if (!mBluetoothAdapter.isEnabled()) {
//									Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//									startActivityForResult(enableBtIntent, 188);
//								}else if(context instanceof MainActivity){
//									connect();
//								}


								if (macList.size() != 0){
									macList.clear();
								}
								UUID[] uuids = {Config.xinbiaoUUID};
								mBluetoothAdapter.startLeScan(uuids,mLeScanCallback);
							}
//							if (isFrist){
//								isFrist = false;
//							}
						}
					} else {
						ToastUtil.showMessageApp(context, result.getMsg());
					}
				} catch (Exception e) {
					ToastUtil.showMessageApp(context, "ee>>>>"+e);
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}

	protected void submit(final Context context, String uid, String access_token){

		RequestParams params = new RequestParams();
		params.put("uid", uid);
		params.put("access_token", access_token);
		params.put("oid", oid);
		params.put("latitude", referLatitude);
		params.put("longitude", referLongitude);
		if (macList.size() > 0){
			params.put("xinbiao",macList.get(0));
		}
		HttpHelper.post(context, Urls.backBikescan, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.setTitle("正在提交");
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
				Log.e("base===","结束用车:"+responseString);
				try {
					ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
					if (result.getFlag().equals("Success")) {

						SharedPreferencesUrls.getInstance().putString("type","");
						SharedPreferencesUrls.getInstance().putString("m_nowMac","");
						SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
						SharedPreferencesUrls.getInstance().putString("biking_latitude","");
						SharedPreferencesUrls.getInstance().putString("biking_longitude","");

						if ("1".equals(result.getData())){
							ToastUtil.showMessageApp(context, result.getMsg());
							if ("已为您免单,欢迎反馈问题".equals(result.getMsg())){

								ToastUtil.showMessage(context,"context==="+context);

								if(context instanceof CurRoadStartActivity){
									CurRoadStartActivity.isEnd = true;
									CurRoadStartActivity.instance.finish();
								}

								tz = 1;
								UIHelper.goToAct(context, FeedbackActivity.class);
//								UIHelper.goToAct(context, Main2Activity.class);
//                              scrollToFinishActivity();

								Log.e("base===","base===Feedback");
							}else {
								tz = 2;
								Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
								intent.putExtra("oid",oid);
								startActivity(intent);

								Log.e("base===","base===HistoryRoadDetail==="+oid);
							}
						}else {
							ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");

							tz = 3;
							UIHelper.goToAct(context, CurRoadBikedActivity.class);

//							Intent intent = new Intent(getApplicationContext(),  CurRoadBikedActivity.class);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//							startActivity(intent);


							Log.e("base===","base===CurRoadBiked");
						}
//                        scrollToFinishActivity();

					}else {
						ToastUtil.showMessageApp(context, "base===="+result.getMsg());
					}
				}catch (Exception e){

				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}

	public void endBtn(final Context context){
		final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
			ToastUtil.showMessageApp(context,"请先登录账号");
			UIHelper.goToAct(context,LoginActivity.class);
		}else {
			ToastUtil.showMessage(context,uid+"==="+access_token);
			ToastUtil.showMessage(context,macList+"==="+isContainsList);
			ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));

			if (isContainsList.contains(true)){
				if ("1".equals(type)){
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁并拨乱数字密码，距车一米内在APP点击结束!")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							submit(context, uid,access_token);
						}
					});
					customBuilder.create().show();
				}else {
//                    flag = 2;
					if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
						ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
						finish();
//                        scrollToFinishActivity();
					}
					//蓝牙锁
					if (!BaseApplication.getInstance().getIBLE().isEnable()){
						BaseApplication.getInstance().getIBLE().enableBluetooth();
						return;
					}
					if (BaseApplication.getInstance().getIBLE().getConnectStatus()){

						if (loadingDialog != null && !loadingDialog.isShowing()){
							loadingDialog.setTitle("请稍等");
							loadingDialog.show();
						}

						BaseApplication.getInstance().getIBLE().getLockStatus();
					}else {

						if (lockLoading != null && !lockLoading.isShowing()){
							lockLoading.setTitle("正在连接");
							lockLoading.show();
						}

						m_myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								ToastUtil.showMessage(context, BaseApplication.getInstance().getIBLE().getConnectStatus()+"==="+BaseApplication.getInstance().getIBLE().getLockStatus());

								if (lockLoading != null && lockLoading.isShowing()){
									lockLoading.dismiss();
								}

								if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
									CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
									customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
											.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
												}
											});
									customBuilder.create().show();
								}

							}
						}, 10 * 1000);

						connect();

					}
				}
			}else {
				if (macList.size() > 0 && !"1".equals(type)){
					if (!TextUtils.isEmpty(m_nowMac)) {
						//蓝牙锁
						if (!BaseApplication.getInstance().getIBLE().isEnable()){
							BaseApplication.getInstance().getIBLE().enableBluetooth();
							return;
						}
						if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
							BaseApplication.getInstance().getIBLE().getLockStatus();
						}else {
							if (lockLoading != null && !lockLoading.isShowing()){
								lockLoading.setTitle("正在连接");
								lockLoading.show();
							}
							connect();
						}
					}
				}else {
//					ToastUtil.showMessageApp(context,"请停放至校内公共停车区域，或重启手机定位服务");

					CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
					customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域，或打开手机GPS并重启软件再试")
							.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					customBuilder.create().show();

				}
			}
		}
	}

	protected BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (!macList.contains(parseAdvData(rssi,scanRecord))){
				macList.add(parseAdvData(rssi,scanRecord));
			}
		}
	};

	private String parseAdvData(int rssi, byte[] scanRecord) {
		byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);
		if (bytes[0] == 0x01 && bytes[1] == 0x02) {
			return bytes2hex03(bytes);
		}
		return "";
	}

	


	public void finishMine() {
		AppManager.getAppManager().finishActivity(this);
	}

	// 用户已经登录过没有退出刷新登录
	public void RefreshLogin() {
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
		String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
		if (access_token == null || "".equals(access_token) || uid == null || "".equals(uid)) {
			setAlias("");
		} else {
			RequestParams params = new RequestParams();
			params.add("uid", uid);
			params.add("access_token", access_token);
			HttpHelper.post(AppManager.getAppManager().currentActivity(), Urls.accesslogin, params,
					new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, String responseString) {
							try {
								ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
								if (result.getFlag().equals("Success")) {
									UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);
									// 极光标记别名
									setAlias(bean.getUid());
									SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
									SharedPreferencesUrls.getInstance().putString("access_token",
											bean.getAccess_token());
									SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
									SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
									SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
									SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
									SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
									SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
									SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
									SharedPreferencesUrls.getInstance().putString("specialdays", bean.getSpecialdays());
									SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
								} else {
									setAlias("");
									if (BaseApplication.getInstance().getIBLE() != null){
										if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
											BaseApplication.getInstance().getIBLE().refreshCache();
											BaseApplication.getInstance().getIBLE().close();
											BaseApplication.getInstance().getIBLE().stopScan();
										}
									}
									SharedPreferencesUrls.getInstance().putString("uid", "");
									SharedPreferencesUrls.getInstance().putString("access_token","");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString,
											  Throwable throwable) {
						}
					});
		}
	}

	// 极光推送===================================================================
	private void setAlias(String uid) {
		// 调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, uid));
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, null);
				break;

			case MSG_SET_TAGS:
				JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, null);
				break;

			default:
			}
		}
	};




	protected   void connect() {
//		BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();

		BaseApplication.getInstance().getIBLE().stopScan();
		m_myHandler.sendEmptyMessage(0x99);
		BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
			@Override
			public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
				if (device==null||TextUtils.isEmpty(device.getAddress()))return;
				if (m_nowMac.equalsIgnoreCase(device.getAddress())){
					m_myHandler.removeMessages(0x99);
					BaseApplication.getInstance().getIBLE().stopScan();
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, BaseFragmentActivity.this);
				}
			}
		});
	}





	/**
	 * 激活定位
	 */
//	@Override
//	public void activate(OnLocationChangedListener listener) {
//		mListener = listener;
//		if (mlocationClient == null) {
//			mlocationClient = new AMapLocationClient(this);
//			mLocationOption = new AMapLocationClientOption();
//			//设置定位监听
//			mlocationClient.setLocationListener(this);
//			mLocationOption.setInterval(2 * 1000);
//			//设置为高精度定位模式
//			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//			//设置定位参数
//			mlocationClient.setLocationOption(mLocationOption);
//			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//			// 在定位结束后，在合适的生命周期调用onDestroy()方法
//			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//			mlocationClient.startLocation();
//		}
//	}

	/**
	 * 定位成功后回调函数
	 */
//	@Override
//	public void onLocationChanged(AMapLocation amapLocation) {
//		if (mListener != null && amapLocation != null) {
//			if (amapLocation != null
//					&& amapLocation.getErrorCode() == 0) {
//				if (0.0 != amapLocation.getLatitude() && 0.0 != amapLocation.getLongitude()){
//					String latitude = SharedPreferencesUrls.getInstance().getString("biking_latitude","");
//					String longitude = SharedPreferencesUrls.getInstance().getString("biking_longitude","");
//					if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)){
//						if (AMapUtils.calculateLineDistance(new LatLng(
//								Double.parseDouble(latitude),Double.parseDouble(longitude)
//						),new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude())) > 10){
//
//							SharedPreferencesUrls.getInstance().putString("biking_latitude",""+amapLocation.getLatitude());
//							SharedPreferencesUrls.getInstance().putString("biking_longitude",""+amapLocation.getLongitude());
//							addMaplocation(amapLocation.getLatitude(),amapLocation.getLongitude());
//						}
//					}
//					if (mListener != null) {
//						mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//					}
//					referLatitude = amapLocation.getLatitude();
//					referLongitude = amapLocation.getLongitude();
//					myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//					if (mFirstFix) {
//						mFirstFix = false;
//						schoolrangeList();
//						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
//					} else {
//						centerMarker.remove();
//						mCircle.remove();
//						if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//							isContainsList.clear();
//						}
//						for ( int i = 0; i < pOptions.size(); i++){
//							isContainsList.add(pOptions.get(i).contains(myLocation));
//						}
//					}
//					addChooseMarker();
//					addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆
//				}else {
//					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
//					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
//							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int which) {
//									dialog.cancel();
//									finish();
////									scrollToFinishActivity();
//								}
//							}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.cancel();
//							BaseFragmentActivity.this.requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//						}
//					});
//					customBuilder.create().show();
//				}
//			}
//		}
//	}

	private void addChooseMarker() {
		// 加入自定义标签
		MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
		centerMarker = aMap.addMarker(centerMarkerOption);
	}

	private void addCircle(LatLng latlng, double radius) {
		CircleOptions options = new CircleOptions();
		options.strokeWidth(1f);
		options.fillColor(CurRoadBikingActivity.FILL_COLOR);
		options.strokeColor(CurRoadBikingActivity.STROKE_COLOR);
		options.center(latlng);
		options.radius(radius);
		mCircle = aMap.addCircle(options);
	}

	/**
	 * 停止定位
	 */
//	@Override
//	public void deactivate() {
//		mListener = null;
////		if (mlocationClient != null) {
////			mlocationClient.stopLocation();
////			mlocationClient.onDestroy();
////		}
////		mlocationClient = null;
//	}

	/**
	 *
	 * 上传骑行坐标
	 * */
	private void addMaplocation(double latitude,double longitude){
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
			RequestParams params = new RequestParams();
			params.put("uid",uid);
			params.put("access_token",access_token);
			params.put("oid",oid);
			params.put("osn",osn);
			params.put("latitude",latitude);
			params.put("longitude",longitude);
			HttpHelper.post(context, Urls.addMaplocation, params, new TextHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
						if (result.getFlag().equals("Success")) {
							if (myLocation != null){
								SharedPreferencesUrls.getInstance().putString("biking_latitude",""+myLocation.latitude);
								SharedPreferencesUrls.getInstance().putString("biking_longitude",""+myLocation.longitude);
							}
						}
					}catch (Exception e){

					}
				}
			});
		}
	}



	/**
	 * 学校范围电子栅栏
	 *
	 * */
	private void schoolrangeList(){
		RequestParams params = new RequestParams();
		HttpHelper.get(context, schoolrangeList, params, new TextHttpResponseHandler() {
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
						if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
							isContainsList.clear();
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							List<LatLng> list = new ArrayList<>();
							for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
								JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
								LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
										Double.parseDouble(jsonObject.getString("longitude")));
								list.add(latLng);
							}
							Polygon polygon = null;
							PolygonOptions pOption = new PolygonOptions();
							pOption.addAll(list);
							polygon = aMap.addPolygon(pOption.strokeWidth(2)
									.strokeColor(Color.argb(160, 255, 0, 0))
									.fillColor(Color.argb(160, 255, 0, 0)));
							pOptions.add(polygon);
							isContainsList.add(polygon.contains(myLocation));
						}
					}else {
						ToastUtil.showMessageApp(context,result.getMsg());
					}
				}catch (Exception e){
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onDisconnect(int state) {
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
	@Override
	public void onServicesDiscovered(String name, String address) {
		getToken();
	}
	@Override
	public void onTimeOut() {

	}
	/**
	 * 获取token
	 */
	private void getToken() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				BaseApplication.getInstance().getIBLE().getToken();
			}
		}, 500);
	}



	protected Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				case 0:
					if (!BaseApplication.getInstance().getIBLE().isEnable()){

						break;
					}
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, BaseFragmentActivity.this);
					break;
				case 1:

					break;
				case 2:
					break;
				case 3:
					break;
				case 9:
					break;
				case 0x99://搜索超时
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, BaseFragmentActivity.this);
					break;
				default:
					break;
			}
			return false;
		}
	});

	protected void handleReceiver(Context context, Intent intent) {
		// 广播处理
		if (intent == null) {
			return;
		}

//		String action = intent.getAction();
//		String data = intent.getStringExtra("data");
//		switch (action) {
//			case Config.TOKEN_ACTION:
//
//				mHandler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						BaseApplication.getInstance().getIBLE().getBattery();
//					}
//				}, 500);
//				if (null != lockLoading && lockLoading.isShowing()) {
//					lockLoading.dismiss();
//				}
////					isStop = true;
//				ToastUtil.showMessageApp(context,"base===设备连接成功");
//
//				break;
//			case Config.BATTERY_ACTION:
////					ToastUtil.showMessage(context,"####===2");
//				break;
//			case Config.OPEN_ACTION:
//				ToastUtil.showMessage(context,"####===3");
//				break;
//			case Config.CLOSE_ACTION:
//				ToastUtil.showMessage(context,"####===4");
//				break;
//			case Config.LOCK_STATUS_ACTION:
////				if (CurRoadBikingActivity.instance.loadingDialog != null && CurRoadBikingActivity.instance.loadingDialog.isShowing()){
////					CurRoadBikingActivity.instance.loadingDialog.dismiss();
////				}
////				if (CurRoadBikingActivity.instance.lockLoading != null && CurRoadBikingActivity.instance.lockLoading.isShowing()){
////					CurRoadBikingActivity.instance.lockLoading.dismiss();
////				}
//
//				if (loadingDialog != null && loadingDialog.isShowing()){
//					loadingDialog.dismiss();
//				}
//				if (lockLoading != null && lockLoading.isShowing()){
//					lockLoading.dismiss();
//				}
//
//				if (TextUtils.isEmpty(data)) {
//
//					ToastUtil.showMessageApp(context,"====锁已关闭");
//
//					//锁已关闭
//					submit(context, uid, access_token);
//
//				} else {
//					//锁已开启
//					ToastUtil.showMessageApp(context,"您还未上锁，请给车上锁后还车");
//				}
//				break;
//			case Config.LOCK_RESULT:
////				if (CurRoadBikingActivity.instance.loadingDialog != null && CurRoadBikingActivity.instance.loadingDialog.isShowing()){
////					CurRoadBikingActivity.instance.loadingDialog.dismiss();
////				}
////				if (CurRoadBikingActivity.instance.lockLoading != null && CurRoadBikingActivity.instance.lockLoading.isShowing()){
////					CurRoadBikingActivity.instance.lockLoading.dismiss();
////				}
//
//				if (loadingDialog != null && loadingDialog.isShowing()){
//					loadingDialog.dismiss();
//				}
//				if (lockLoading != null && lockLoading.isShowing()){
//					lockLoading.dismiss();
//				}
//
//				ToastUtil.showMessageApp(context,"####===恭喜您，您已成功上锁");
//
//				endBtn(context);
//
//				break;
//		}
	}

	protected void registerReceiver(IntentFilter intentfilter) {
//		if (actionArray == null) {
//			return;
//		}
//		IntentFilter intentfilter = new IntentFilter();
//		for (String action : actionArray) {
//			intentfilter.addAction(action);
//		}
		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		registerReceiver(internalReceiver, intentfilter);
	}

	private class InternalReceiver extends BroadcastReceiver {

//	protected BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			handleReceiver(context, intent);

		}
	};


}
