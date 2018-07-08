package com.fanweilin.coordinatemap.toolActivity;

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
        btnDic = findViewById(R.id.btn_dci);
        btnDms = findViewById(R.id.btn_dms);
        tvDic = findViewById(R.id.tv_dic);
        tvDms = findViewById(R.id.tv_dms);
        etDic = findViewById(R.id.et_dic);
        etdu = findViewById(R.id.et_du);
        etmiao = findViewById(R.id.et_miao);
        etfen = findViewById(R.id.et_fen);
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
                if (!etDic.getText().toString().equals("")) {
                    double dic = Double.parseDouble(etDic.getText().toString());
                    tvDic.setText(ConvertLatlng.convertToSexagesimal(dic));
                }
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
                tvDms.setText(String.valueOf(ConvertLatlng.convertToDecimal(du, fen, miao)));

                break;
        }

    }
}
