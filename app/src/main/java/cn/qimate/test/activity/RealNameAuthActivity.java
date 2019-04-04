package cn.qimate.test.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.vondear.rxtools.RxFileTool;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.test.BuildConfig;
import cn.qimate.test.R;
import cn.qimate.test.core.common.BitmapUtils1;
import cn.qimate.test.core.common.HttpHelper;
import cn.qimate.test.core.common.SharedPreferencesUrls;
import cn.qimate.test.core.common.UIHelper;
import cn.qimate.test.core.common.Urls;
import cn.qimate.test.core.widget.CustomDialog;
import cn.qimate.test.core.widget.LoadingDialog;
import cn.qimate.test.img.NetUtil;
import cn.qimate.test.model.AuthStateBean;
import cn.qimate.test.model.GradeListBean;
import cn.qimate.test.model.ResultConsel;
import cn.qimate.test.model.SchoolListBean;
import cn.qimate.test.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.test.util.SHA1;
import cn.qimate.test.util.UtilAnim;
import cn.qimate.test.util.UtilBitmap;
import cn.qimate.test.util.UtilScreenCapture;

/**
 * Created by Administrator1 on 2017/2/15.
 */

public class RealNameAuthActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

    private RelativeLayout schoolLayout,sexLatout,classLayout;
    private TextView schoolText,sexText,classText;
    private EditText realNameEdit, stuNumEdit;
    private Button submitBtn;

    private RelativeLayout uploadImageLayout;
    private ImageView uploadImage;

    private RelativeLayout addImageLayout;
    private LinearLayout headLayout;

    private String imgUrl = Urls.uploadsImg;
    private String imageurl = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    /**
     * 弹窗背景
     */
    private ImageView iv_popup_window_back;
    /**
     * 弹窗容器
     */
    private RelativeLayout rl_popup_window;

    private OptionsPickerView pvOptions;
    private OptionsPickerView pvOptions1;
    private OptionsPickerView pvOptions2;
    private String sex = "";
    private String school = "";

    // 输入法
    private List<SchoolListBean> schoolList;
    static ArrayList<String> item1 = new ArrayList<>();
    static ArrayList<String> item2 = new ArrayList<>();
    static ArrayList<String> item3 = new ArrayList<>();

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions.setPicker(item1);
            pvOptions.setCyclic(false, false, false);
            pvOptions.setSelectOptions(0, 0, 0);
            sexLatout.setClickable(true);
        };
    };

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions1.setPicker(item2);
            pvOptions1.setCyclic(false, false, false);
            pvOptions1.setSelectOptions(0, 0, 0);
            schoolLayout.setClickable(true);
        };
    };
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions2.setPicker(item3);
            pvOptions2.setCyclic(false, false, false);
            pvOptions2.setSelectOptions(0, 0, 0);
            classLayout.setClickable(true);
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ui_real_name_auth);
        context = this;
        schoolList = new ArrayList<>();
        initView();
    }

    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = (ImageView)findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout)findViewById(R.id.popupWindow);

        // 选项选择器
        pvOptions = new OptionsPickerView(context,false);
        pvOptions1 = new OptionsPickerView(context,false);
        pvOptions2 = new OptionsPickerView(context,false);

        pvOptions.setTitle("选择学校");
        pvOptions1.setTitle("选择性别");
        pvOptions2.setTitle("选择入学时间");

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("实名认证");

        takePhotoBtn = (Button)findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button)findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        headLayout = (LinearLayout)findViewById(R.id.ui_realNameAuth_headLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.2);
        headLayout.setLayoutParams(params);

        schoolLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_schoolLayout);
        sexLatout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_sexLayout);
        classLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_classLayout);
        schoolText = (TextView)findViewById(R.id.ui_realNameAuth_schoolText);
        sexText = (TextView)findViewById(R.id.ui_realNameAuth_sexText);
        classText = (TextView)findViewById(R.id.ui_realNameAuth_class);
        realNameEdit = (EditText)findViewById(R.id.ui_realNameAuth_realName);
        stuNumEdit = (EditText)findViewById(R.id.ui_realNameAuth_stuNum);
        submitBtn = (Button) findViewById(R.id.ui_realNameAuth_submitBtn);

        uploadImageLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_uploadImageLayout);
        uploadImage = (ImageView)findViewById(R.id.ui_realNameAuth_uploadImage);
        addImageLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_addImageLayout);

        if (schoolList.isEmpty() || item1.isEmpty()){
            getSchoolList();
        }
        backImg.setOnClickListener(this);
        schoolLayout.setOnClickListener(this);
        sexLatout.setOnClickListener(this);
        classLayout.setOnClickListener(this);
        uploadImageLayout.setOnClickListener(this);
        uploadImage.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (item2!= null && !item2.isEmpty() && 0 != item2.size()) {
                    handler1.sendEmptyMessage(0x123);
                    return;
                }
                if (!item2.isEmpty() || 0 != item2.size()) {
                    item2.clear();
                }
                item2.add("男");
                item2.add("女");
                handler1.sendEmptyMessage(0x123);
            }
        }).start();
        // 设置默认选中的三级项目
        // 监听确定选择按钮
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                school = item1.get(options1);
                schoolText.setText(school);
            }
        });

        pvOptions1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                sex = item2.get(options1);
                sexText.setText(item2.get(options1));
            }
        });
        pvOptions2.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                classText.setText(item3.get(options1));
            }
        });
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            if (!"1".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))
                    && SharedPreferencesUrls.getInstance().getString("iscert","") != null &&
                    !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
                initHttp(uid,access_token);
            }
        }
        getGradeList();
    }

    @Override
    public void onClick(View v){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.ui_realNameAuth_schoolLayout:
                pvOptions.show();
                break;
            case R.id.ui_realNameAuth_sexLayout:
                pvOptions1.show();
                break;
            case R.id.ui_realNameAuth_classLayout:
                pvOptions2.show();
                break;
            case R.id.ui_realNameAuth_uploadImageLayout:
            case R.id.ui_realNameAuth_uploadImage:
                clickPopupWindow();
                break;
            case R.id.ui_realNameAuth_submitBtn:
                String uid = SharedPreferencesUrls.getInstance().getString("uid","");
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
                String realname = realNameEdit.getText().toString();
                String stunum = stuNumEdit.getText().toString();
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    if (school == null || "".equals(school)){
                        Toast.makeText(context,"请选择院校",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (classText.getText().toString().trim() == null ||
                            "".equals(classText.getText().toString().trim())){
                        Toast.makeText(context,"请选择年级",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (realname == null || "".equals(realname)){
                        Toast.makeText(context,"请填写您的真实姓名",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (stunum == null || "".equals(stunum)){
                        Toast.makeText(context,"请填写您的学号/工号",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sex == null || "".equals(sex)){
                        Toast.makeText(context,"请选择您的性别",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (imageurl == null || "".equals(imageurl)){
                        Toast.makeText(context,"请上传您的学生证/教师证",Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    Toast.makeText(context, "==="+imageurl, Toast.LENGTH_SHORT).show();

                    AutoSubmitBtn(uid, access_token, realname, classText.getText().toString().trim(), stunum);

//                    SubmitBtn(uid,access_token,realname,classText.getText().toString().trim(),stunum);


                }
                break;
        }
    }

    private void initHttp(String uid,String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.get(context, Urls.getAuthentication, params, new TextHttpResponseHandler() {
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
                        AuthStateBean bean = JSON.parseObject(result.getData(),AuthStateBean.class);
                        school = bean.getSchool();
                        sex = bean.getSex();
                        imageurl = bean.getStunumfile();
                        schoolText.setText(school);
                        classText.setText(bean.getGrade());
                        realNameEdit.setText(bean.getRealname());
                        stuNumEdit.setText(bean.getStunum());
                        sexText.setText(sex);
                        if (bean.getStunumfile() == null || "".equals(bean.getStunumfile()) ||
                                "/Public/stunumfile.png".equals(bean.getStunumfile())){
                            addImageLayout.setVisibility(View.VISIBLE);
                            uploadImage.setVisibility(View.GONE);
                        }else {
                            uploadImage.setVisibility(View.VISIBLE);
                            addImageLayout.setVisibility(View.GONE);
                            Glide.with(context).load(Urls.host+imageurl).crossFade().into(uploadImage);
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 自动认证
     * */
    private void AutoSubmitBtn(final String uid, final String access_token, final String realname, String grade, final String stunum) {
        RequestParams params = new RequestParams();
        params.put("xm", realname);
        params.put("xh", stunum);

        HttpHelper.postWithHead(context, Urls.autoauthentication, params, new TextHttpResponseHandler() {
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
                //UIHelper.ToastError(context, throwable.toString());

//                Toast.makeText(context, "===zzz", Toast.LENGTH_SHORT).show();

                SubmitBtn(uid, access_token, realname, classText.getText().toString().trim(), stunum);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {



                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Toast.makeText(context,"==="+result.status,Toast.LENGTH_SHORT).show();

                    if("0".equals(result.status)){
                        SubmitBtn(uid, access_token, realname, classText.getText().toString().trim(), stunum);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });

    }

    /**
     * 手动认证
     * */
    private void SubmitBtn(String uid,String access_token,String realname,
                           String grade,String stunum){

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("realname",realname);
        params.put("sex",sex);
        params.put("school",school);
        params.put("grade",grade);
        params.put("stunum",stunum);
        params.put("stunumfile",imageurl);
        HttpHelper.post(context, Urls.authentication, params, new TextHttpResponseHandler() {
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

                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context,"恭喜您,信息提交成功",Toast.LENGTH_SHORT).show();
                        SharedPreferencesUrls.getInstance().putString("iscert","4");
                        scrollToFinishActivity();
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    /**
     *
     * 获取学校
     * */
    private void getSchoolList(){

        HttpHelper.get(context, Urls.schoolList, new TextHttpResponseHandler() {

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
                        JSONArray JSONArray = new JSONArray(result.getData());
                       if (schoolList.size() != 0 || !schoolList.isEmpty()){
                           schoolList.clear();
                       }
                        if (item1.size() != 0 || !item1.isEmpty()){
                            item1.clear();
                        }
                       for (int i = 0; i < JSONArray.length();i++){
                           SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
                           schoolList.add(bean);
                           item1.add(bean.getSchool());
                       }
                       handler.sendEmptyMessage(0x123);
                    }else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    //获取入学时间
    private void getGradeList(){

        HttpHelper.get(context, Urls.gradeList, new TextHttpResponseHandler() {

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
                        JSONArray JSONArray = new JSONArray(result.getData());
                        if (item3.size() != 0){
                            item3.clear();
                        }
                        for (int i = 0; i < JSONArray.length();i++){
                            GradeListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),GradeListBean.class);
                            item3.add(bean.getName());
                        }
                        handler2.sendEmptyMessage(0x123);
                    }else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                if (data != null){
                    try {
                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                            if (imageUri != null) {
                                urlpath = getRealFilePath(context, data.getData());
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("请稍等");
                                    loadingDialog.show();
                                }
                                new Thread(uploadImageRunnable).start();
                            }
                        }else {
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();// 用户点击取消操作
                    }
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(context, Uri.fromFile(temp));
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("请稍等");
                            loadingDialog.show();
                        }
                          new Thread(uploadImageRunnable).start();
                        }
                }else {
                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 900);
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null,
                    null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        if (imageUri != null) {
            urlpath = getRealFilePath(context, imageUri);
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setTitle("请稍等");
                loadingDialog.show();
            }
            new Thread(uploadImageRunnable).start();
        }

    }

    /**
     * 使用HttpUrlConnection模拟post表单进行文件 上传平时很少使用，比较麻烦 原理是：
     * 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {

            if (TextUtils.isEmpty(imgUrl)) {
                Toast.makeText(context, "还没有设置上传服务器的路径！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> textParams = new HashMap<>();
            Map<String, File> fileparams = new HashMap<>();
            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                textParams = new HashMap<>();
                fileparams = new HashMap<>();
                // 要上传的图片文件
                File file = new File(urlpath);
                if (file.length() >= 2097152 / 2) {
                    file = new File(BitmapUtils1.compressImageUpload(urlpath,480f,800f));
                }
                fileparams.put("key1", file);
                textParams.put("uid", SharedPreferencesUrls.getInstance().getString("uid", ""));
                textParams.put("access_token", SharedPreferencesUrls.getInstance().getString("access_token", ""));
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");// 设置编码
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    resultStr = NetUtil.readString(is);
                } else {
                    Toast.makeText(context, "请求URL失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            mHandler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        // 返回数据示例，根据需求和后台数据灵活处理
                        JSONObject jsonObject = new JSONObject(resultStr);
                        // 服务端以字符串“1”作为操作成功标记
                        if (jsonObject.optString("flag").equals("Success")) {
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
                            option.inSampleSize = 1;
                            imageurl = jsonObject.optString("data");
                            addImageLayout.setVisibility(View.GONE);
                            uploadImage.setVisibility(View.VISIBLE);
//                            Glide.with(context).load(Urls.host+imageurl).crossFade().into(uploadImage);
                            ImageLoader.getInstance().displayImage(Urls.host + imageurl, uploadImage);
                            Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage.setVisibility(View.GONE);
                            addImageLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        addImageLayout.setVisibility(View.VISIBLE);
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
            clickClosePopupWindow();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[] { Manifest.permission.CAMERA }, 101);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        RealNameAuthActivity.this.requestPermissions(new String[] { Manifest.permission.CAMERA },
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, RxFileTool.getUriForFile(context,
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        }else {
//                            // 下面这句指定调用相机拍照后的照片存储的路径
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                        }

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }


//                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
//                        File file = new File(Environment.getExternalStorageDirectory()+"/", IMAGE_FILE_NAME);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(RealNameAuthActivity.this,
                                    BuildConfig.APPLICATION_ID + ".fileprovider",
                                    file));

                        }else {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        }


                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)) {

                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


//                            String path = getFilesDir() + File.separator + "images" + File.separator;
//                            File file = new File(path, "test.jpg");
//                            if(!file.getParentFile().exists())
//                                file.getParentFile().mkdirs();

//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                //步骤二：Android 7.0及以上获取文件 Uri
//                                mUri = FileProvider.getUriForFile(RealNameAuthActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
//                            }

//                            File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);

                            File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                            if(!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(RealNameAuthActivity.this,
                                        BuildConfig.APPLICATION_ID + ".fileprovider",
//                                        "com.vondear.rxtools.fileprovider",
                                        file));


//                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


//                                ContentValues contentValues = new ContentValues(1);
//                                contentValues.put(MediaStore.Images.Media.DATA, new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME).getAbsolutePath());
//                                Uri uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


                            }else {
                                // 下面这句指定调用相机拍照后的照片存储的路径
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));

                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            }
                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        }else {
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示弹窗
     */
    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(this, iv_popup_window_back, 5,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }

        // 打开弹窗
        UtilAnim.showToUp(rl_popup_window, iv_popup_window_back);

    }

    /**
     * 关闭弹窗
     */
    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(rl_popup_window, iv_popup_window_back);
    }
}
