package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilySensorsBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATFamilySensorsRVAdapter;
import com.aliyun.ayland.widget.popup.ATFamilySecurityPopup;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

public class ATFamilySecurityActivity1 extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean mATHouseBean;
    private ATFamilySecurityPopup mATFamilySecurityPopup;
    private ATFamilySensorsBean mATFamilySensorsBean;
    private android.os.Handler mHandler = new android.os.Handler();
    private ATMyTitleBar titlebar;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvEquipmentNumber, tvOnlineNumber, tvMonitoringNumber, tvMonitoringPlace;
    private RelativeLayout rlFamilyMonitor, rlSensorSecurity, rlLockSecurity;
    private ATFamilySensorsRVAdapter mATFamilySensorsRVAdapter;
    private LinearLayout llZhaf, llJtbf;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_security1;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        rlFamilyMonitor = findViewById(R.id.rl_family_monitor);
        rlSensorSecurity = findViewById(R.id.rl_sensor_security);
        rlLockSecurity = findViewById(R.id.rl_lock_security);
        tvEquipmentNumber = findViewById(R.id.tv_equipment_number);
        tvOnlineNumber = findViewById(R.id.tv_online_number);
        tvMonitoringNumber = findViewById(R.id.tv_monitoring_number);
        tvMonitoringPlace = findViewById(R.id.tv_monitoring_place);
        llZhaf = findViewById(R.id.ll_zhaf);
        llJtbf = findViewById(R.id.ll_jtbf);
        recyclerView = findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void getFamilySecurity() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageId", mATHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETFAMILYSECURITY, jsonObject);
    }

    private void init() {
        mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mATFamilySecurityPopup = new ATFamilySecurityPopup(this);
        titlebar.setTitle(getString(R.string.at_family_security));

        llZhaf.setOnClickListener(view -> startActivity(new Intent(this, ATOldYoungCareFindLocationActivity.class)));
        llJtbf.setOnClickListener(view -> startActivity(new Intent(this, ATSensorSecurityActivity.class)));

        rlFamilyMonitor.setOnClickListener(view -> startActivity(new Intent(this, ATFamilyMonitorActivity.class)));

        rlSensorSecurity.setOnClickListener(view -> startActivity(new Intent(this, ATSensorSecurityActivity.class)));

        rlLockSecurity.setOnClickListener(view -> startActivity(new Intent(this, ATSmartLockSecurityActivity.class)));

        mATFamilySensorsRVAdapter = new ATFamilySensorsRVAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mATFamilySensorsRVAdapter);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            getFamilySecurity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFamilySecurity();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETFAMILYSECURITY:
                        mATFamilySensorsBean = gson.fromJson(result, ATFamilySensorsBean.class);
                        mATFamilySensorsRVAdapter.setLists(mATFamilySensorsBean.getSensors());
                        tvEquipmentNumber.setText(mATFamilySensorsBean.getTotalCameraCount());
                        tvOnlineNumber.setText(mATFamilySensorsBean.getOnlineCameraCount());
                        tvMonitoringNumber.setText(mATFamilySensorsBean.getTotalDeviceCount());
                        tvMonitoringPlace.setText(String.format(getString(R.string.at_divide), mATFamilySensorsBean.getHasTypeCount(), mATFamilySensorsBean.getTotalTypeCount()));
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}