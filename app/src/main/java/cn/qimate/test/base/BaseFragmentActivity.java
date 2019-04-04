package cn.qimate.test.base;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.test.activity.CurRoadBikedActivity;
import cn.qimate.test.activity.CurRoadBikingActivity;
import cn.qimate.test.activity.CurRoadStartActivity;
import cn.qimate.test.activity.FeedbackActivity;
import cn.qimate.test.activity.HistoryRoadDetailActivity;
import cn.qimate.test.activity.LoginActivity;
import cn.qimate.test.activity.Main2Activity;
import cn.qimate.test.activity.MainActivity;
import cn.qimate.test.ble.utils.ParseLeAdvData;
import cn.qimate.test.core.common.AppManager;
import cn.qimate.test.core.common.HttpHelper;
import cn.qimate.test.core.common.SharedPreferencesUrls;
import cn.qimate.test.core.common.UIHelper;
import cn.qimate.test.core.common.Urls;
import cn.qimate.test.core.widget.CustomDialog;
import cn.qimate.test.core.widget.LoadingDialog;
import cn.qimate.test.model.CurRoadBikingBean;
import cn.qimate.test.model.ResultConsel;
import cn.qimate.test.model.UserMsgBean;
import cn.qimate.test.util.ToastUtil;

import static cn.qimate.test.activity.CurRoadBikingActivity.bytes2hex03;
import static cn.qimate.test.core.common.Urls.schoolrangeList;

public class BaseFragmentActivity extends AppCompatActivity
		implements
//		LocationSource,
// 		AMapLocationListener,
 		OnConnectionListener
{

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;



	private TelephonyManager tm;
	protected Context context;

	public static String m_nowMac = "";  //"A8:1B:6A:B4:E7:C9"
	public static String oid = "";
	public static String osn = "";
	public static String type = "";


	private LatLng myLocation = null;
	private boolean mFirstFix = true;
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

	protected Handler m_myHandler = new MainHandler(this);


//	public static LoadingDialog loadingDialog;
//	public static LoadingDialog lockLoading;


//	protected InternalReceiver internalReceiver = null;

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
									SharedPreferencesUrls.getInstance().putString("access_token", bean.getAccess_token());
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

		Log.e("base===","connect1===");

		m_myHandler.sendEmptyMessage(0x99);

		Log.e("base===","connect2===");

		BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
			@Override
			public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
				if (device==null||TextUtils.isEmpty(device.getAddress()))return;

				Log.e("base===","connect3===");

				if (m_nowMac.equalsIgnoreCase(device.getAddress())){
					Log.e("base===","connect4===");
					m_myHandler.removeMessages(0x99);

					Log.e("base===","connect5===");
					BaseApplication.getInstance().getIBLE().stopScan();

					Log.e("base===","connect6===");
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, BaseFragmentActivity.this);

					Log.e("base===","connect7===");
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



//	public static Handler m_myHandler = new Handler(new Handler.Callback() {
//	public static
class MainHandler extends Handler {
		WeakReference<BaseFragmentActivity> softReference;

		public MainHandler(BaseFragmentActivity activity) {
			softReference = new WeakReference<BaseFragmentActivity>(activity);
		}

		@Override
		public void handleMessage(Message mes) {
			BaseFragmentActivity baseFragmentActivity = softReference.get();

			switch (mes.what) {
				case 0:
					if (!BaseApplication.getInstance().getIBLE().isEnable()){

						break;
					}
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, baseFragmentActivity);
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
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, baseFragmentActivity);
					break;
				default:
					break;
			}
//			return false;
		}
	}



}
