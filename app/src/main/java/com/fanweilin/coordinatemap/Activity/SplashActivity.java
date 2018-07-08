package com.fanweilin.coordinatemap.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Computer;
import com.fanweilin.coordinatemap.gpsTest.GpsTestActivity;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.tencent.bugly.Bugly;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.tencent.qcloud.sdk.Constant;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timchat.model.UserInfo;
import timchat.ui.customview.DialogActivity;

public class SplashActivity extends AppCompatActivity implements SplashADListener,SplashView, TIMCallBack {

    SplashPresenter presenter;
    private SplashAD splashAD;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private ViewGroup container;
    private TextView skipView;
    private static final String SKIP_TEXT = "点击跳过 %d";
    public boolean canJump = false;
    private SharedPreferences spf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        clearNotification();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initview();
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0) {
                init();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

   private void initview(){
       container = this.findViewById(R.id.splash_container);
       skipView = findViewById(R.id.skip_view);
       spf = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
   }
   private void startIntent(){
       Intent intent=new Intent();
       intent.setClass(SplashActivity.this,MainMapsActivity.class);
       startActivity(intent);
       finish();
   }
    private void init() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(), loglvl);
        //初始化TLS
        Bugly.init(getApplicationContext(),String.valueOf( Constant.SDK_APPID), false);
        TlsBusiness.init(getApplicationContext());
        String id = TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
        isshowAd();
     /*   Intent intent = new Intent(SplashActivity.this, MainMapsActivity.class);
        startActivity(intent);
        finish();*/
        presenter = new SplashPresenter(this);
        presenter.start();

    }
    private void isshowAd(){
        int vip = spf.getInt(UserVip.SPFVIP, 1);
        Log.d("vip", String.valueOf(vip));
        if (vip > 1) {
            startIntent();
        } else {
            getvip();
            if (data.time >= 2) {
                fetchSplashAD(this, container, skipView, Constants.APP_ID, Constants.SplashPosID, this, 0);
            }else {
               Intent intent=new Intent();
               intent.setClass(SplashActivity.this,MainActivity.class);
               startActivity(intent);
               finish();
            }
        }
    }
    private void getvip() {
        File key = new File(data.BASE_PATH, "key");
        if (key.exists()) {
            int vip = spf.getInt(User.VIP, 0);
            if (vip < 2) {
                try {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String szImei=null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    szImei = TelephonyMgr.getDeviceId();
                    FileReader fileReader =new FileReader(key.getAbsoluteFile());
                    BufferedReader bfr = new BufferedReader(fileReader);
                    String vipID=bfr.readLine();
                    String vipId2= Computer.getvip(szImei);
                    if (vipID.equals(vipId2)){
                        SharedPreferences.Editor editor=spf.edit();
                        editor.putInt(User.VIP,3);
                        editor.commit();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity        展示广告的activity
     * @param adContainer     展示广告的大容器
     * @param skipContainer   自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId           应用ID
     * @param posId           广告位ID
     * @param adListener      广告状态监听器
     * @param fetchDelay      拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */

    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }


    @Override
    public void onNoAD(AdError error) {
        Log.i(
                "AD_DEMO",
                String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(),
                        error.getErrorMsg()));
        /** 如果加载广告失败，则直接跳转 */
        startIntent();
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
        skipView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }
    private void next() {
        if (canJump) {
            startIntent();
        } else {
            canJump = true;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onError(int i, String s) {
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            case 6200:
                Toast.makeText(this, getString(R.string.login_error_timeout), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
            default:
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void navToHome() {
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Intent intent = new Intent(SplashActivity.this, DialogActivity.class);
                startActivity(intent);
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            logout();
                    }
                });
            }
        })
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {

                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                    }
                });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);

    }

    @Override
    public void navToLogin() {
      /*  Intent intent = new Intent(getApplicationContext(), PhonePwdLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);*/
       /*    Intent intent = new Intent(SplashActivity.this, MainMapsActivity.class);
        startActivity(intent);
        finish();*/

    }

    @Override
    public boolean isUserLogin() {
        return UserInfo.getInstance().getId() != null && (!TLSService.getInstance().needLogin(UserInfo.getInstance().getId()));
    }
   public int LOGIN_RESULT_CODE=1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOGIN_RESULT_CODE == requestCode) {
            if (0 == TLSService.getInstance().getLastErrno()) {
                String id = TLSService.getInstance().getLastUserIdentifier();
                UserInfo.getInstance().setId(id);
                UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}