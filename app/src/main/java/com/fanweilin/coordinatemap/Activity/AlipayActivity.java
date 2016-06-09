package com.fanweilin.coordinatemap.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

public class AlipayActivity extends AppCompatActivity {
    String APPID = "4092f407bdff17349291781f070efb3f";
    ProgressDialog dialog;
    String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        BP.init(this, APPID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("快捷支付");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    public void zhifubao(View view){
        payByAli();
    }
    public void weixin(View view){
        payByWeiXin();

    }
    // 调用支付宝支付
    void payByAli() {
        showDialog("正在获取订单...");
        final String name = getName();

        // BmobPay.init(this, APPID);

        BP.pay(this, name, getBody(), getPrice(), true, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(AlipayActivity.this, "支付结果未知,请稍后手动查询",
                        Toast.LENGTH_SHORT).show();
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(AlipayActivity.this, "支付成功!", Toast.LENGTH_SHORT)
                        .show();
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {
                Toast.makeText(AlipayActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                        .show();
                hideDialog();
            }
        });
    }

    // 调用微信支付
    void payByWeiXin() {
        showDialog("正在获取订单...");
        final String name = getName();

        BP.pay(this, name, getBody(), getPrice(), false, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(AlipayActivity.this, "支付结果未知,请稍后手动查询",
                        Toast.LENGTH_SHORT).show();
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(AlipayActivity.this, "支付成功!", Toast.LENGTH_SHORT)
                        .show();
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    new AlertDialog.Builder(AlipayActivity.this)
                            .setMessage(
                                    "监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)还是用支付宝支付")
                            .setPositiveButton("安装",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            installBmobPayPlugin("bp_wx.db");
                                        }
                                    })
                            .setNegativeButton("支付宝支付",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            payByAli();
                                        }
                                    }).create().show();
                } else {
                    Toast.makeText(AlipayActivity.this, "支付中断!",
                            Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }
        });
    }

    // 执行订单查询
    void query() {
        showDialog("正在查询订单...");
        final String orderId = getOrder();

        BP.query(this, orderId, new QListener() {

            @Override
            public void succeed(String status) {
                Toast.makeText(AlipayActivity.this, "查询成功!该订单状态为 : " + status,
                        Toast.LENGTH_SHORT).show();
                hideDialog();
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(AlipayActivity.this, "查询失败", Toast.LENGTH_SHORT)
                        .show();
                hideDialog();
            }
        });
    }


    double getPrice() {
        double price = 1.0;
        return price;
    }

    // 商品详情(可不填)
    String getName() {
        return "广告去除";
    }

     //商品详情(可不填)
    String getBody() {
        return null;
    }

     //订单号(查询时必填)
    String getOrder() {
        return orderId;
    }

    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
