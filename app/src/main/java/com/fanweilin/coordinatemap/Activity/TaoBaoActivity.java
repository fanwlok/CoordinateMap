package com.fanweilin.coordinatemap.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.BuildConfig;
import com.fanweilin.coordinatemap.Class.AlipayZeroSdk;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Computer;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;


public class TaoBaoActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textView;
    private Toolbar toolbar;
    String szImei;
    ProgressDialog dialog;
    String content = "去除广告";
    Button btnpay;
    Button btnset;
    // 应用或游戏商自定义的支付订单(每条支付订单数据不可相同)
    String orderId = "";
    // 用户标识
    String userId = "";
    // 支付商品名称
    String goodsName = "经纬度定位";
    // 支付金额
    float price = (float) 30.0;
    private SharedPreferences spf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);


        initView();
        initData();


    }

    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        textView =  findViewById(R.id.tv_appid);
        btnpay=findViewById(R.id.btn_pay);
        btnpay.setOnClickListener(this);
        btnset=findViewById(R.id.btn_setvip);
        btnset.setOnClickListener(this);
    }

    public void initData() {
        spf = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
        int vip = spf.getInt(UserVip.SPFVIP, 1);
        if(vip==100){
            btnset.setVisibility(View.VISIBLE);
        }
        //text
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        szImei = TelephonyMgr.getDeviceId();
        textView.setText(szImei);
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    public void copy(View view){
        ClipboardManager copy = (ClipboardManager) TaoBaoActivity.this
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("appID",szImei);
        copy.setPrimaryClip(mClipData);
        Toast.makeText(getApplicationContext(), "文本已经复制成功！",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pay:
         /*       if (AlipayZeroSdk.hasInstalledAlipayClient(TaoBaoActivity.this)) {
                    AlipayZeroSdk.startAlipayClient(TaoBaoActivity.this, "FKX02046VWFA691JGQEH0C\n");
                } else {
                    Toast.makeText(this,"谢谢，您没有安装支付宝客户端",Toast.LENGTH_SHORT);
                }*/
              String k="8ynTtS_cO911_S7WNO_WzME66z1cxfQD";
                joinQQGroup( k);
                    break;
            case R.id.btn_setvip:
                Intent intent=new Intent();
                intent.setClass(TaoBaoActivity.this,Payctivity.class);
                startActivity(intent);
                break;
        }
    }
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
