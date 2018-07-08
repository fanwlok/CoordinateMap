package timchat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fanweilin.coordinatemap.R;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;
import java.util.ArrayList;
import java.util.List;

import timchat.adapters.ProfileSummaryAdapter;
import timchat.model.FriendProfile;
import timchat.model.ProfileSummary;

public class BlackListActivity extends Activity {

    private final String TAG = "BlackListActivity";

    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        listView = findViewById(R.id.list);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.get(position).onClick(BlackListActivity.this);
            }
        });
        TIMFriendshipManagerExt.getInstance().getBlackList(new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "get black list error code : " + i);
            }

            @Override
            public void onSuccess(List<String> ids) {
                TIMFriendshipManagerExt.getInstance().getFriendsProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "get profile error code : " + i);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        for (TIMUserProfile item : timUserProfiles){
                            FriendProfile profile = new FriendProfile(item);
                            list.add(profile);
                        }
                        adapter.notifyDataSetChanged();

                    }
                });

            }
        });
    }
}
