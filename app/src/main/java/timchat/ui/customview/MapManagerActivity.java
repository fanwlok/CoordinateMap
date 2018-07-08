package timchat.ui.customview;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.fragment.OnlineMapFragment;
import com.fanweilin.coordinatemap.widget.MyViewPagerHome;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import java.util.ArrayList;
import java.util.List;

import timchat.model.FriendshipInfo;
import timchat.model.GroupInfo;
import timchat.model.UserInfo;
import timchat.ui.ContactFragment;
import timchat.ui.ConversationFragment;
import timchat.ui.SettingFragment;

public class MapManagerActivity extends AppCompatActivity  {
   private BottomNavigationBar bottomNavigationBar;
    private List<Fragment> fragments;
    private ConversationFragment conversationFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;
    OnlineMapFragment onlineMapFragment;
    private MyViewPagerHome mViewPager;
    TextBadgeItem numberBadgeItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);
        initdata();
        initview();

    }
    private void initview(){
        mViewPager=findViewById(R.id.frament_map);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(0);
        bottomNavigationBar =findViewById(R.id.bottom_navigation_bar);
        numberBadgeItem =  new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.white)
                .setTextColor(R.color.red_500)
                .setHideOnSelect(true);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar//设置选中的颜色
                .setInActiveColor(R.color.light_blue_500);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_map_grey600_48dp, "地图"))
                .addItem(new BottomNavigationItem(R.drawable.tab_conversation, "会话")
                .setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_contact, "好友"))
                .addItem(new BottomNavigationItem(R.drawable.tab_setting, "设置"))
                .setFirstSelectedPosition(0)
                .initialise(); //所有的设置需在调用该方法前完成
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                   switch (position){
                       case 0:
                           mViewPager.setCurrentItem(0);
                           break;
                       case 1:
                           mViewPager.setCurrentItem(1);
                           break;
                       case 2:
                           mViewPager.setCurrentItem(2);
                           break;
                       case 3:
                           mViewPager.setCurrentItem(3);
                           break;
                   }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }
    public void setMsgUnread(long num){
        if(num>0){
            numberBadgeItem.setHideOnSelect(true).setText(String.valueOf(num));
        }else {
            numberBadgeItem.setHideOnSelect(false).setText("");
        }

    }
    private void initdata(){
        fragments = new ArrayList<Fragment>();
        conversationFragment=new ConversationFragment();
        contactFragment=new ContactFragment();
        settingFragment =new SettingFragment();
        onlineMapFragment=new OnlineMapFragment();
        fragments.add(onlineMapFragment);
        fragments.add(conversationFragment);
        fragments.add(contactFragment);
        fragments.add(settingFragment);

    }
    public void logout(){
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(MapManagerActivity.this,MainMapsActivity.class);
        intent.putExtra("ACTIVITY",MainMapsActivity.MAPMANAGERACTIVITY);
        startActivity(intent);
        finish();

    }
    public class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
