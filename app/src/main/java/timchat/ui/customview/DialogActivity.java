package timchat.ui.customview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.qcloud.presentation.business.LoginBusiness;

import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import timchat.model.FriendshipInfo;
import timchat.model.GroupInfo;
import timchat.model.UserInfo;
import com.fanweilin.coordinatemap.Activity.SplashActivity;

public class DialogActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        setFinishOnTouchOutside(false);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(DialogActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                        logout();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(DialogActivity.this, getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                        String deviceMan = android.os.Build.MANUFACTURER;
                        finish();
                    }
                });
                break;
            case R.id.btnCancel:
                logout();
                break;
        }
    }

    private void logout(){
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(this,SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
