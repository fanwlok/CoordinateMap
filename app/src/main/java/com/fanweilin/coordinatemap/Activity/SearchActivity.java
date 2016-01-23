package com.fanweilin.coordinatemap.Activity;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;

import com.fanweilin.coordinatemap.R;

public class SearchActivity extends Activity {
    public SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//默认输入法弹起
        searchView= (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}
