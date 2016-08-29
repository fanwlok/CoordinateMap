package com.fanweilin.coordinatemap.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Class.CoordianteApi;
import com.fanweilin.coordinatemap.Class.HttpControl;
import com.fanweilin.coordinatemap.Class.Register;
import com.fanweilin.coordinatemap.Class.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.CheckNetwork;
import com.fanweilin.coordinatemap.computing.Constants;
import com.fanweilin.coordinatemap.computing.ValidateUserInfo;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener

{


    // UI references.
    private AutoCompleteTextView mUserName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;


    private Button mEmailSignInButton;

    private TextView txt_create, txt_forgot;
    private SharedPreferences spfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initInstances();
    }

    private void initInstances() {
        // Set up the login form.
        spfUser = getSharedPreferences(User.SPFNAEM, Context.MODE_APPEND);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mUserName = (AutoCompleteTextView) findViewById(R.id.txt_name);
        mPasswordView = (EditText) findViewById(R.id.txt_password);
        mUserName.setText(spfUser.getString(User.UERNAME, ""));
        mPasswordView.setText(spfUser.getString(User.PASSWORD, ""));
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        txt_create = (TextView) findViewById(R.id.txt_create);
        txt_create.setOnClickListener(this);

        txt_forgot = (TextView) findViewById(R.id.txt_forgot);
        txt_forgot.setOnClickListener(this);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUserName.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserName.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validateUserInfo = new ValidateUserInfo();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !validateUserInfo.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (CheckNetwork.isConnected(getApplicationContext())) {
                showProgress(true);
                login(username, password);
            }else {
                Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void login(String username, String password) {
        Retrofit retrofit = HttpControl.getInstance(this).getRetrofit();
        final CoordianteApi login = retrofit.create(CoordianteApi.class);
        login.RxLog(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Register>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this,"网络异常", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Register register) {
                        switch (register.getMessage()) {
                            case "登陆成功":
                                SharedPreferences.Editor edit = spfUser.edit();
                                edit.putString(User.UERNAME, mUserName.getText().toString());
                                edit.putString(User.PASSWORD, mPasswordView.getText().toString());
                                edit.putString(User.NIKENAEM, register.getUsername());
                                edit.putInt(User.VIP,register.getVip());
                                edit.commit();
                                showProgress(false);
                                Toast.makeText(LoginActivity.this, register.getMessage(), Toast.LENGTH_LONG).show();
                                finish();
                            case "登陆失败":
                                showProgress(false);
                                Toast.makeText(LoginActivity.this, register.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        String email = mUserName.getText().toString();

        switch (v.getId()) {
            case R.id.email_sign_in_button:
                attemptLogin();
                break;
            case R.id.txt_create:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(Constants.TAG_EMAIL, email);
                startActivity(intent);
                finish();
                break;
            case R.id.txt_forgot:
                Intent intentForgot = new Intent(LoginActivity.this, ForgotPassActivity.class);
                intentForgot.putExtra(Constants.TAG_EMAIL, email);
                startActivity(intentForgot);
                finish();
                break;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }
}

