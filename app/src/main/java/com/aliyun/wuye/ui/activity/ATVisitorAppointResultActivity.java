package com.aliyun.wuye.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATVisitorResultBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATQRCodeUtil;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ATVisitorAppointResultActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVisitorResultBean mVisitorReservateBean;
    private ATMyTitleBar titleBar;
    private TextView tvName, tvVisiteRoom, tvOwner, tvVisiteTime, tvLeaveTime, tvPlateNumber, tvPhone;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wy_visitor_appoint_result;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvOwner = findViewById(R.id.tv_owner);
        tvName = findViewById(R.id.tv_name);
        tvVisiteRoom = findViewById(R.id.tv_visite_room);
        tvVisiteTime = findViewById(R.id.tv_visite_time);
        tvLeaveTime = findViewById(R.id.tv_leave_time);
        tvPlateNumber = findViewById(R.id.tv_plate_number);
        tvPhone = findViewById(R.id.tv_phone);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getVisitorReservation();
    }

    private void getVisitorReservation() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", getIntent().getStringExtra("id"));
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITORRESERVATION, jsonObject);
    }

    private void init() {
        titleBar.setTitle(getString(R.string.at_visitor_regist_result));
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETVISITORRESERVATION:
                        mVisitorReservateBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<ATVisitorResultBean>() {
                        }.getType());
                        tvName.setText(mVisitorReservateBean.getVisitorName());
                        tvOwner.setText(String.format(getString(R.string.at_owner_), mVisitorReservateBean.getOwnerName()));
                        tvVisiteRoom.setText(mVisitorReservateBean.getVisitorHouse());
                        tvVisiteTime.setText(String.format(getString(R.string.at_visit_time_), mVisitorReservateBean.getReservationStartTime()));
                        tvLeaveTime.setText(String.format(getString(R.string.at_leave_time_), mVisitorReservateBean.getReservationEndTime()));
                        tvPhone.setText(String.format(getString(R.string.at_phone_),
                                TextUtils.isEmpty(mVisitorReservateBean.getVisitorTel()) ? "无" : mVisitorReservateBean.getVisitorTel()));
                        if (mVisitorReservateBean.getHasCar() != -1) {
                            tvPlateNumber.setVisibility(View.VISIBLE);
                            tvPlateNumber.setText(String.format(getString(R.string.at_plate_number_),
                                    TextUtils.isEmpty(mVisitorReservateBean.getCarNumber()) ? "无" :mVisitorReservateBean.getCarNumber()));
                        }
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}