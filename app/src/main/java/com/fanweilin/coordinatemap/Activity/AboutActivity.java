package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;

public class AboutActivity extends AppCompatActivity {
    private String [] name={"检查更新","意见反馈"};
    private ListView listView;
    private Toolbar toolbar;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initview();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initview(){
        toolbar = findViewById(R.id.toolbar);
        listView= findViewById(R.id.list_about);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        ArrayAdapter<String > arrayAdapter=new ArrayAdapter<String>(AboutActivity.this,android.R.layout.simple_list_item_1,name);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        update();
                        break;
                    case 1:
                        break;
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    private void update() {
        dialog.show();
    }

}

