package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.R;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.service.TLSService;

public class PhonePwdRegisterActivity extends Activity {

    private final static String TAG = "PhonePwdRegisterActivity";

    private TLSService tlsService;
    private SmsContentObserver smsContentObserver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tencent_tls_ui_activity_phone_pwd_register);
        // 设置返回按钮
        findViewById(R.id.btn_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhonePwdRegisterActivity.this.onBackPressed();
                    }
                });

        tlsService = TLSService.getInstance();
        tlsService.initPhonePwdRegisterService(this,
                (EditText) findViewById(R.id.selectCountryCode),
                (EditText) findViewById(R.id.phone),
                (EditText) findViewById(R.id.txt_checkcode),
                (EditText) findViewById(R.id.txt_password),
                (EditText) findViewById(R.id.txt_password1),
                (Button) findViewById(R.id.btn_requirecheckcode),
                (Button) findViewById(R.id.btn_verify)
        );

/*        smsContentObserver = new SmsContentObserver(new Handler(),
                this,
                (EditText) findViewById(R.id.txt_checkcode")),
                Constants.PHONEPWD_REGISTER_SENDER);

        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);*/
    }

    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
