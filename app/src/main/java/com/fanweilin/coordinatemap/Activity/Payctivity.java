package com.fanweilin.coordinatemap.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.R;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Payctivity extends AppCompatActivity {
  private EditText editText;
  private EditText editvip;
  private Button btnPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payctivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    editText=findViewById(R.id.edt_username);
    editvip=findViewById(R.id.edt_vip);
     btnPay=findViewById(R.id.btnpay);
       btnPay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                    putVip();
           }
       });
    }
public void putVip(){
        String title="86-"+editText.getText().toString();
        int vip=Integer.valueOf(editvip.getText().toString());
    Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
    BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
    baiduDataApi.RxCreateVIP(title,30,120,
            1,
           vip, Constants.geomapVip,Constants.ak).
            subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
            subscribe(new Observer<ReasonCreate>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ReasonCreate reasonCreate) {
                    if(reasonCreate.getStatus()==0){
                        Toast.makeText(Payctivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
}
}
