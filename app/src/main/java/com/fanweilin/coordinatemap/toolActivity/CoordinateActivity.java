package com.fanweilin.coordinatemap.toolActivity;

import android.graphics.AvoidXfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Computer;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.google.android.gms.common.server.FavaDiagnosticsEntity;

import java.text.DecimalFormat;

public class CoordinateActivity extends AppCompatActivity {
    private EditText beginx;
    private EditText beginy;
    private EditText endx;
    private EditText endy;
    private EditText etdu;
    private EditText etfen;
    private EditText etmiao;
    private EditText distance;
    private Toolbar toolbar;
    private Button btn;
    private RadioGroup radioGroup;
    private RadioButton rbfront;
    private RadioButton rbback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        beginx = (EditText) findViewById(R.id.et_coordinate_benginx);
        beginy = (EditText) findViewById(R.id.et_coordinate_beginy);
        endx = (EditText) findViewById(R.id.et_coordinate_endx);
        endy = (EditText) findViewById(R.id.et_coordinate_endy);
        etdu = (EditText) findViewById(R.id.et_coordinate_du);
        etfen = (EditText) findViewById(R.id.et_coordinate_fen);
        etmiao = (EditText) findViewById(R.id.et_et_coordinate_miao);
        distance = (EditText) findViewById(R.id.et_coordinate_distance);
        btn = (Button) findViewById(R.id.btn_coordinate_computer);
        btn.setOnClickListener(new ButtonOnclick());
        rbfront = (RadioButton) findViewById(R.id.rb_coordinate_front);
        rbback = (RadioButton) findViewById(R.id.rb_coordinate_back);
        radioGroup = (RadioGroup) findViewById(R.id.rg_coordinate);
        radioGroup.setOnCheckedChangeListener(new RadioChange());
    }

    private class RadioChange implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_coordinate_front:
                    endx.setFocusable(false);
                    endy.setFocusable(false);
                    distance.setFocusableInTouchMode(true);
                    etdu.setFocusableInTouchMode(true);
                    etfen.setFocusableInTouchMode(true);
                    etmiao.setFocusableInTouchMode(true);
                    break;
                case R.id.rb_coordinate_back:
                    endx.setFocusableInTouchMode(true);
                    endy.setFocusableInTouchMode(true);
                    distance.setFocusable(false);
                    etdu.setFocusable(false);
                    etfen.setFocusable(false);
                    etmiao.setFocusable(false);
                    break;
            }

        }
    }

    private class ButtonOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (rbfront.isChecked()) {
                if (!isFrontnull()) {
                    Toast.makeText(CoordinateActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    double Ax = Double.parseDouble(beginx.getText().toString());
                    double Ay = Double.parseDouble(beginy.getText().toString());
                    double dis = Double.parseDouble(distance.getText().toString());
                    double du = Double.parseDouble(etdu.getText().toString());
                    double fen = Double.parseDouble(etfen.getText().toString());
                    double miao = Double.parseDouble(etmiao.getText().toString());
                    double andgle = ConvertLatlng.convertToDecimal(du, fen, miao);
                    double[] c = Computer.coordinateFrontComputer(Ax, Ay, andgle, dis);
                    double Bx = c[0];
                    double By = c[1];
                    DecimalFormat df = new DecimalFormat("0.0000");
                    endx.setText(String.valueOf(df.format(Bx)));
                    endy.setText(String.valueOf(df.format(By)));
                }

            } else {
                if (!isBacknull()) {
                    Toast.makeText(CoordinateActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();

                } else {
                    double Ax = Double.parseDouble(beginx.getText().toString());
                    double Ay = Double.parseDouble(beginy.getText().toString());
                    double Bx = Double.parseDouble(endx.getText().toString());
                    double By = Double.parseDouble(endy.getText().toString());
                    double[] c = Computer.coordinateBackComputer(Ax, Ay, Bx, By);
                    double dis = c[0];
                    DecimalFormat df = new DecimalFormat("0.0000");
                    double angel = c[1];
                    distance.setText(String.valueOf(df.format(dis)));
                    ConvertLatlng.convertToSexagesimal(angel);
                    etdu.setText(String.valueOf(ConvertLatlng.sdu));
                    etfen.setText(String.valueOf(ConvertLatlng.sfen));
                    DecimalFormat df1 = new DecimalFormat("0.00");
                        etmiao.setText(String.valueOf(df1.format(ConvertLatlng.smiao)));
                }
            }

        }
    }

    public boolean isFrontnull() {
        boolean tag = true;
        if (TextUtils.isEmpty(beginx.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(beginy.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(distance.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(etdu.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(etfen.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(etmiao.getText().toString())) {
            tag = false;
        }
        return tag;

    }

    public boolean isBacknull() {
        boolean tag = true;
        if (TextUtils.isEmpty(beginx.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(beginy.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(endx.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(endy.getText().toString())) {
            tag = false;
        }
        return tag;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
