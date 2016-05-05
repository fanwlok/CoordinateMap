package com.fanweilin.coordinatemap.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;

public class ConvertActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnDic;
    private Button btnDms;
    private TextView tvDic;
    private TextView tvDms;
    private EditText etDic;
    private EditText etdu;
    private EditText etfen;
    private EditText etmiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        init();
    }

    public void init()

    {
        btnDic = (Button) findViewById(R.id.btn_dci);
        btnDms = (Button) findViewById(R.id.btn_dms);
        tvDic = (TextView) findViewById(R.id.tv_dic);
        tvDms = (TextView) findViewById(R.id.tv_dms);
        etDic = (EditText) findViewById(R.id.et_dic);
        etdu = (EditText) findViewById(R.id.et_du);
        etmiao = (EditText) findViewById(R.id.et_miao);
        etfen = (EditText) findViewById(R.id.et_fen);
        btnDms.setOnClickListener(this);
        btnDic.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        ConvertLatlng convertLatlng = new ConvertLatlng();
        switch (v.getId()) {
            case R.id.btn_dci:
//                if (!etDic.getText().toString().equals("")) {
//                    double dic = Double.parseDouble(etDic.getText().toString());
//                    tvDic.setText(convertLatlng.convertToSexagesimal(dic));
//                }
                String context=etDic.getText().toString();
                String bdla[]=context.split(",");
                JZLocationConverter.LatLng bdlat=new JZLocationConverter.LatLng(Double.parseDouble(bdla[0]),Double.parseDouble(bdla[1]));
                JZLocationConverter.LatLng WGS=JZLocationConverter.bd09ToWgs84(bdlat);
                tvDms.setText(String.valueOf(WGS.getLatitude())+ String.valueOf(WGS.getLongitude()));
                break;
            case R.id.btn_dms:
                double du;
                double fen;
                double miao;
                if (!etdu.getText().toString().equals("")) {
                    du = Double.parseDouble(etdu.getText().toString());
                } else {
                    du = 0;
                }
                if (!etfen.getText().toString().equals("")) {
                    fen = Double.parseDouble(etfen.getText().toString());
                } else {
                    fen = 0;
                }
                if (!etmiao.getText().toString().equals("")) {
                    miao = Double.parseDouble(etmiao.getText().toString());
                } else {
                    miao = 0;
                }
                tvDms.setText(String.valueOf(convertLatlng.convertToDecimal(du, fen, miao)));

                break;
        }

    }
}
