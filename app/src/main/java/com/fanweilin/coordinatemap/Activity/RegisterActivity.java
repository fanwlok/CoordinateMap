package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Class.CoordianteApi;
import com.fanweilin.coordinatemap.Class.HttpControl;
import com.fanweilin.coordinatemap.Class.Register;
import com.fanweilin.coordinatemap.Class.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Constants;
import com.fanweilin.coordinatemap.computing.ValidateUserInfo;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by AndreBTS on 20/08/2015.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edit_name, edit_email, edit_password, edit_passwordsure;
    TextView txt_alreadyHave;
    Button btn_registrar;
    private SharedPreferences spfUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        String email;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            email = extras == null ? "" : extras.getString(Constants.TAG_EMAIL);
        } else {
            email = savedInstanceState.getString(Constants.TAG_EMAIL);
        }
        spfUser = getSharedPreferences(User.SPFNAEM, Context.MODE_APPEND);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_email.setText(email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_passwordsure = (EditText) findViewById(R.id.edit_passwordsure);
        txt_alreadyHave = (TextView) findViewById(R.id.txt_already_have);
        txt_alreadyHave.setOnClickListener(this);

        btn_registrar = (Button) findViewById(R.id.btn_register);
        btn_registrar.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptCreate() {
        // Store values at the time of the login attempt.
        String name = edit_name.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        String passwordsure=edit_passwordsure.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            edit_name.setError(getString(R.string.error_field_required));
            focusView = edit_name;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        } else if (!validate.isEmailValid(email)) {
            edit_email.setError(getString(R.string.error_invalid_email));
            focusView = edit_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edit_password.setError(getString(R.string.error_field_required));
            focusView = edit_password;
            cancel = true;
        } else if (!validate.isPasswordValid(password)) {
            edit_password.setError(getString(R.string.error_invalid_password));
            focusView = edit_password;
            cancel = true;
        } else if (!password.equals(passwordsure)) {
            edit_passwordsure.setError(getString(R.string.error_password_diff));
            focusView = edit_passwordsure;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //TODO Create account logic
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            register(name, email, password);
        }
    }

    public void register(String name, String email, String password) {
        Retrofit retrofit = HttpControl.getInstance(getApplicationContext()).getRetrofit();
         CoordianteApi coordianteApi = retrofit.create(CoordianteApi.class);
        coordianteApi.RxRegister(name, password, email).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Register>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Register register) {
                Toast.makeText(RegisterActivity.this, register.getMessage(), Toast.LENGTH_SHORT).show();
                   if(register.getMessage().equals("注册成功")){
                       SharedPreferences.Editor edit = spfUser.edit();
                       edit.putString(User.UERNAME, register.getUsername());
                       edit.putString(User.PASSWORD, register.getPassword());
                       edit.putString(User.NIKENAEM, register.getUsername());
                       edit.commit();
                       Intent intent=new Intent();
                       intent.setClass(RegisterActivity.this,LoginActivity.class);
                       startActivity(intent);
                       finish();
                   }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                attemptCreate();
                break;
            case R.id.txt_already_have:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

}
