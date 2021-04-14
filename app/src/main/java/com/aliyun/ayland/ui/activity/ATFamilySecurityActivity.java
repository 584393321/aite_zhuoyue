package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATPublicSecurityCameraBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATFamilySecurityRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATFamilySecurityActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATFamilySecurityRVAdapter mFamilySecurityRVAdapter;
    private String mIotId, productKey;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_security2;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getPublicCamera();
    }

    private void getPublicCamera() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETPUBLICCAMERA, jsonObject);
    }

    private void queryLiveStreaming() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotId", mIotId);
        mPresenter.request(ATConstants.Config.SERVER_URL_QUERYLIVESTREAMING, jsonObject);
    }

    private void init() {
        titleBar.setTitle(getString(R.string.at_public_monitor));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mFamilySecurityRVAdapter = new ATFamilySecurityRVAdapter(this);
        recyclerView.setAdapter(mFamilySecurityRVAdapter);
        mFamilySecurityRVAdapter.setOnItemClickListener((view, o, position) -> {
            mIotId = ((ATPublicSecurityCameraBean) o).getIotId();
            productKey = ((ATPublicSecurityCameraBean) o).getProductKey();
            showBaseProgressDlg();
            queryLiveStreaming();
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            getPublicCamera();
        });
        smartRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETPUBLICCAMERA:
                        List<ATPublicSecurityCameraBean> publicSecurityCameraBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATPublicSecurityCameraBean>>() {
                        }.getType());
                        mFamilySecurityRVAdapter.setLists(publicSecurityCameraBeanList);
                        break;
                    case ATConstants.Config.SERVER_URL_QUERYLIVESTREAMING:
                        startActivity(new Intent(this, ATIntelligentMonitorActivity.class)
                                .putExtra("productKey", productKey)
                                .putExtra("path", jsonResult.getJSONObject("data").getString("path"))
                                .putExtra("iotId", mIotId));
                        break;
                }
            } else {
                if (jsonResult.getString("errorMessage").equals("device offline")) {
                    showToast("设备已离线");
                } else {
                    showToast(jsonResult.getString("message"));
                }
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}