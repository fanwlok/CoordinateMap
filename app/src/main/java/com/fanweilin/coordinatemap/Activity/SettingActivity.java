package com.fanweilin.coordinatemap.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;

import static com.fanweilin.coordinatemap.R.id.update;


public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            SettingFragment settingFragment = new SettingFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.placeholder,settingFragment)
                    .commit();
        }
    }
    private void initview(){
        toolbar = findViewById(R.id.toolbar);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    public static class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private ProgressDialog dialog;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.setting);
            initdata();
        }
        public void initdata(){
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            ListPreference listPreference= (ListPreference) findPreference("coordinatedisplayformat");
            ListPreference gpslistPreference= (ListPreference) findPreference("gps_rate");
            CheckBoxPreference checkBoxPreference= (CheckBoxPreference) findPreference("pre_isshow_name");
            prefs.registerOnSharedPreferenceChangeListener(this);
            String gps_rate=gpslistPreference.getValue();
            int gpsindex=gpslistPreference.findIndexOfValue(gps_rate);
            if (gpsindex>=0){
                gpslistPreference.setSummary(gpslistPreference.getEntries()[gpsindex]);
            }else {
                gpslistPreference.setSummary(gpslistPreference.getEntries()[0]);
            }

            String format=listPreference.getValue();
            int index = listPreference.findIndexOfValue(format);
            if(index >= 0) {
                listPreference.setSummary(listPreference.getEntries()[index]);

          } else {
            listPreference.setSummary(listPreference.getEntries()[0]);
         }
            checkBoxPreference.setChecked(prefs.getBoolean("pre_isshow_name",false));

        }
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference){

          if("update".equals(preference.getKey())){
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if ("gps_rate".equals(key)) {
                ListPreference listPreference = (ListPreference) findPreference("gps_rate");
                String format = listPreference.getValue();
                int index = listPreference.findIndexOfValue(format);
                if (index >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[index]);
                } else {
                    listPreference.setSummary(listPreference.getEntries()[0]);
                }
            }
            if ("coordinatedisplayformat".equals(key)) {
                ListPreference listPreference = (ListPreference) findPreference("coordinatedisplayformat");
                String format = listPreference.getValue();
                int index = listPreference.findIndexOfValue(format);
                if (index >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[index]);

                } else {
                    listPreference.setSummary(listPreference.getEntries()[0]);
                }
            }
            if("pre_isshow_name".equals(key)){
                CheckBoxPreference checkBoxPreference= (CheckBoxPreference) findPreference("pre_isshow_name");

            }
        }
        @Override
        public void onResume() {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }
        @Override
         public void onPause() {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }

}
