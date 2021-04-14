package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.global.ATDataDispatcher;
import com.aliyun.ayland.global.ATDataDispatcher.ServerMessageType;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.utils.ATScreenUtils;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.anthouse.wyzhuoyue.R;

import static com.aliyun.ayland.global.ATDataDispatcher.ServerMessageType.TYPE_COMMUNITY_SERVICES;
import static com.aliyun.ayland.global.ATDataDispatcher.ServerMessageType.TYPE_PROPERTY_INFORMATION;


public class ATWelcomeActivity extends ATBaseActivity {
    private String TAG = "ATWelcomeActivity";
    private ATWelcomeActivity mContext;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_welcome;
    }

    @Override
    protected void findView() {
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        getWindow().getDecorView().setFitsSystemWindows(true);
        ATScreenUtils.getScreenData(this);
        Log.e("weiinit: ", getIntent().getIntExtra("id", 0) + "----"+getIntent().getIntExtra("type", 0));
        if (!LoginBusiness.isLogin() || TextUtils.isEmpty(ATGlobalApplication.getLoginBeanStr())) {
//            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            LoginBusiness.login(new ILoginCallback() {
                @Override
                public void onLoginSuccess() {
                    Log.i(TAG, "登录成功");
                }

                @Override
                public void onLoginFailed(int code, String error) {
                    Log.i(TAG, "登录失败");
                }
            });
        } else {
            if (ATGlobalApplication.isIsWuye())
                getIntent().setClass(ATWelcomeActivity.this, com.aliyun.wuye.ui.activity.ATMainActivity.class);
            else
                getIntent().setClass(ATWelcomeActivity.this, com.aliyun.ayland.ui.activity.ATMainActivity.class);
            if (getIntent().hasExtra("type")) {
                switch (getIntent().getIntExtra("type", 0)) {
                    case TYPE_COMMUNITY_SERVICES:
                    case TYPE_PROPERTY_INFORMATION:
                        switch (getIntent().getIntExtra("subType", 0)){
                            case ServerMessageType.SUBTYPE_VISITE_ACCESE:
                                //访客通行通知
                                if (ATGlobalApplication.isIsWuye())
                                    getIntent().setClass(ATWelcomeActivity.this, com.aliyun.wuye.ui.activity.ATVisitorAppointResultActivity.class);
                                else
                                    getIntent().setClass(ATWelcomeActivity.this, com.aliyun.ayland.ui.activity.ATVisitorAppointResultActivity.class);
                            case ServerMessageType.SUBTYPE_VISITE_APPOINT:
                                //访客预约通知
                                break;
                            case ServerMessageType.SUBTYPE_SPACE_APPOINT:
                                //共享空间预约通知
                                break;
                            case ServerMessageType.SUBTYPE_SPACE_PAY:
                                //共享空间支付通知
                                break;
                            case ServerMessageType.SUBTYPE_SPACE_ACCESE:
                                //共享空间通行通知
                                break;
                            default:
                                return;
                        }
                        break;
                }
            }
            startActivity(getIntent());
        }
        finish();
    }
}
