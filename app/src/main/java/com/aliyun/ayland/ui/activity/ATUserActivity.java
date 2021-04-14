package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;

public class ATUserActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ImageView imgBack, imgUser;
    private TextView tvName;
    private RelativeLayout rlSetting, rlAbout, rlSwitchRoom, rlManageFace, rlMessageCenter;
    private LinearLayout llUser, llDeviceManage, llRoomManage, llFamilyManage;
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ico_touxiang_mr)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_user;
    }

    @Override
    protected void findView() {
        imgBack = findViewById(R.id.img_back);
        llFamilyManage = findViewById(R.id.ll_family_manage);
        llRoomManage = findViewById(R.id.ll_room_manage);
        llDeviceManage = findViewById(R.id.ll_device_manage);
        llUser = findViewById(R.id.ll_user);
        imgUser = findViewById(R.id.img_user);
        tvName = findViewById(R.id.tv_name);
        rlSetting = findViewById(R.id.rl_setting);
        rlAbout = findViewById(R.id.rl_about);
        rlSwitchRoom = findViewById(R.id.rl_switch_room);
        rlManageFace = findViewById(R.id.rl_manage_face);
        rlMessageCenter = findViewById(R.id.rl_message_center);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        tvName.setText(ATGlobalApplication.getAccount());
        imgBack.setOnClickListener(v -> finish());
        llUser.setOnClickListener(v -> startActivity(new Intent(this, ATUserCenterActivity.class)));
        llDeviceManage.setOnClickListener(v -> startActivity(new Intent(this, ATDeviceManageActivity.class)));
        llRoomManage.setOnClickListener(v -> startActivity(new Intent(this, ATManageRoomActivity.class)));
        llFamilyManage.setOnClickListener(v -> startActivity(new Intent(this, ATFamilyManageActivity.class)));
        rlSetting.setOnClickListener(v -> startActivity(new Intent(this, ATSettingActivity.class)));
        rlAbout.setOnClickListener(v -> startActivity(new Intent(this, ATAboutActivity.class)));
        rlSwitchRoom.setOnClickListener(v -> startActivity(new Intent(this, ATChangeHouseActivity.class)));
        rlManageFace.setOnClickListener(v -> startActivity(new Intent(this, ATUserFaceActivity.class)));
        rlMessageCenter.setOnClickListener(v -> startActivity(new Intent(this, ATMessageCenterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(ATGlobalApplication.getATLoginBean().getAvatarUrl()).apply(options).into(imgUser);
        tvName.setText(ATGlobalApplication.getATLoginBean().getNickName());
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_HOUSEDEVICE:

                        break;
                }
            } else {
                closeBaseProgressDlg();
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}