package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.activity.PhonePwdLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.PassWord;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class PhonePwdRegisterService {

    private final static String TAG = "PhonePwdRegisterService";

    private Context context;
    private EditText txt_countryCode;
    private EditText txt_phoneNumber;
    private EditText txt_checkCode;
    private EditText txt_password;
    private EditText txt_password1;
    private Button btn_requireCheckCode;
    private Button btn_verify;

    private String countryCode;
    private String phoneNumber;
    private String checkCode;
   private String password;
   private String password1;
    private PwdRegListener pwdRegListener;
    private TLSService tlsService;

    public PhonePwdRegisterService(Context context,
                                   EditText txt_countryCode,
                                   EditText txt_phoneNumber,
                                   EditText txt_checkCode,
                                   final EditText txt_password,
                                   EditText txt_password1,
                                   Button btn_requireCheckCode,
                                   Button btn_verify) {
        this.context = context;
        this.txt_countryCode = txt_countryCode;
        this.txt_phoneNumber = txt_phoneNumber;
        this.txt_checkCode = txt_checkCode;
        this.txt_password=txt_password;
        this.txt_password1=txt_password1;
        this.btn_requireCheckCode = btn_requireCheckCode;
        this.btn_verify = btn_verify;

        tlsService = TLSService.getInstance();
        pwdRegListener = new PwdRegListener();

        btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = "中国大陆 +86";
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = PhonePwdRegisterService.this.txt_phoneNumber.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入有效的手机号");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdRegAskCode(countryCode, phoneNumber, pwdRegListener);
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = "中国大陆 +86";
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = PhonePwdRegisterService.this.txt_phoneNumber.getText().toString();
                checkCode = PhonePwdRegisterService.this.txt_checkCode.getText().toString();
                 password=PhonePwdRegisterService.this.txt_password.getText().toString();
                 password1=PhonePwdRegisterService.this.txt_password1.getText().toString();
                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入有效的手机号");
                    return;
                }
                if(PassWord.NumberCount(password)<6){
                    Util.showToast(PhonePwdRegisterService.this.context, "密码不能少于六位");
                    return;
                }
                if(!password.equals(password1)){
                    Util.showToast(PhonePwdRegisterService.this.context, "两次密码不相等");
                }
                if(!PassWord.Test(password)){
                    Util.showToast(PhonePwdRegisterService.this.context, "密码只能字母和数字");

                }
                if (checkCode.length() == 0) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入验证码");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdRegVerifyCode(checkCode, pwdRegListener);
            }
        });
    }

    public class PwdRegListener implements TLSPwdRegListener {
        @Override
        public void OnPwdRegAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "注册短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdRegVerifyCodeSuccess() {
            tlsService.TLSPwdRegCommit(password,pwdRegListener);
        }

        @Override
        public void OnPwdRegCommitSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "注册验证通过");
            Intent intent = new Intent(context, PhonePwdLoginActivity.class);
            intent.putExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_REGISTER);
            intent.putExtra(Constants.PHONE_NUMBER, txt_phoneNumber.getText().toString());
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        @Override
        public void OnPwdRegFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdRegTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
