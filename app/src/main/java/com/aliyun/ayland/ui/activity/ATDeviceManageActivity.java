package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

public class ATDeviceManageActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private RelativeLayout rlUnbind, rlMy, rlShared, rlAccepted;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_device_manage;
    }

    @Override
    protected void findView() {
        rlUnbind = findViewById(R.id.rl_unbind);
        rlMy = findViewById(R.id.rl_my);
        rlShared = findViewById(R.id.rl_shared);
        rlAccepted = findViewById(R.id.rl_accepted);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void getFace() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userType", "OPEN");
        jsonObject.put("userId", ATGlobalApplication.getHid());
        jsonObject.put("imageFormat", "URL");
        mPresenter.request(ATConstants.Config.SERVER_URL_GETFACE, jsonObject);
    }

    private void init() {
        rlUnbind.setOnClickListener(view -> startActivity(new Intent(this, ATDeviceManageToActivity.class).putExtra("type", 1)));
        rlMy.setOnClickListener(view -> startActivity(new Intent(this, ATDeviceManageToActivity.class).putExtra("type", 2)));
        rlShared.setOnClickListener(view -> startActivity(new Intent(this, ATDeviceManageToActivity.class).putExtra("type", 3)));
        rlAccepted.setOnClickListener(view -> startActivity(new Intent(this, ATDeviceManageToActivity.class).putExtra("type", 4)));
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        break;
                    case ATConstants.Config.SERVER_URL_GETFACE:
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}