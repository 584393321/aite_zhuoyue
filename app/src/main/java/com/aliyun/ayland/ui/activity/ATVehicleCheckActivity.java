package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATParkingBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATVehicleCheckRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATVehicleCheckActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVehicleCheckRVAdapter mVehicleCheckRVAdapter;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_my_car;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findParkingData();
    }

    private void findParkingData() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPARKINGDATA, jsonObject);
    }

    private void init() {
        titleBar.setTitle(getString(R.string.at_vehicle_check));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVehicleCheckRVAdapter = new ATVehicleCheckRVAdapter(this);
        recyclerView.setAdapter(mVehicleCheckRVAdapter);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findParkingData();
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDPARKINGDATA:
                        List<ATParkingBean> parkingBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATParkingBean>>() {
                        }.getType());
                        mVehicleCheckRVAdapter.setLists(parkingBeanList);
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
