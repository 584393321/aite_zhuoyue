package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATFamilyMonitorRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATFamilyMonitorActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATFamilyMonitorRVAdapter mFamilyMonitorRVAdapter;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private TextView tvAdd;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llNoData;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_monitor;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        tvAdd = findViewById(R.id.tv_add);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        llNoData = findViewById(R.id.ll_no_data);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findUserCamera();
    }

    private void findUserCamera() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDUSERCAMERA, jsonObject);
    }

    private void init() {
        titleBar.setTitle(getString(R.string.at_family_monitor));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFamilyMonitorRVAdapter = new ATFamilyMonitorRVAdapter(this);
        recyclerView.setAdapter(mFamilyMonitorRVAdapter);
        tvAdd.setOnClickListener(view -> startActivity(new Intent(this, ATDiscoveryDeviceActivity.class)));
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findUserCamera();
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDUSERCAMERA:
                        List<ATDeviceBean> deviceBeanList = gson.fromJson(jsonResult.getString("deviceList"), new TypeToken<List<ATDeviceBean>>() {
                        }.getType());
                        if(deviceBeanList.size() == 0){
                            llNoData.setVisibility(View.VISIBLE);
                        }else {
                            llNoData.setVisibility(View.GONE);
                        }
                        mFamilyMonitorRVAdapter.setLists(deviceBeanList);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        closeBaseProgressDlg();
        smartRefreshLayout.finishRefresh();
    }
}