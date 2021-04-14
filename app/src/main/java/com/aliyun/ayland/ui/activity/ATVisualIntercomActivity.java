package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATSipListBean;
import com.aliyun.ayland.data.ATVisualIntercomRecordBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATVisualIntercomRVAdapter;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.aliyun.ayland.widget.voip.VoipManager;
import com.anthouse.wyzhuoyue.R;
import com.evideo.voip.sdk.EVVoipException;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATVisualIntercomActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean mATHouseBean;
    private int startNum = 0, type = 0;
    private ATVisualIntercomRVAdapter mVisualIntercomRVAdapter;
    private ATSwitchButton switchview, switchviewTransfer;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private ATSipListBean mAtSipListBean;
    private TextView tvNone;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_visual_intercom;
    }

    @Override
    protected void findView() {
        switchview = findViewById(R.id.switchview);
        switchviewTransfer = findViewById(R.id.switchview_transfer);
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        tvNone = findViewById(R.id.tv_none);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        list();
    }

    private void close() {
        if (mATHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_MANAGER_CLOSE, jsonObject);
    }

    private void transfer() {
        if (mATHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_MANAGER_TRANSFER, jsonObject);
    }

    private void list() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 0);
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        jsonObject.put("targetId", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATGlobalApplication.isIsWuye() ? ATConstants.Config.SERVER_URL_SIPUSER : ATConstants.Config.SERVER_URL_LIST, jsonObject);
    }

    private void setOpenOrClose() {
        if (mATHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        String url = switchview.isChecked() ? ATConstants.Config.SERVER_URL_CLOSE : ATConstants.Config.SERVER_URL_OPEN;
        mPresenter.request(url, jsonObject);
    }

    private void log() {
        if (mATHouseBean == null)
            return;
        if (mAtSipListBean == null) {
            list();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        jsonObject.put("sipNumber", mAtSipListBean.getSipNumber());
        mPresenter.request(ATConstants.Config.SERVER_URL_LOG, jsonObject);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVisualIntercomRVAdapter = new ATVisualIntercomRVAdapter(this);
        recyclerView.setAdapter(mVisualIntercomRVAdapter);
        switchview.setOnTouchListener((view, motionEvent) -> {
            setOpenOrClose();
            return false;
        });
        switchviewTransfer.setOnTouchListener((view, motionEvent) -> {
            if (switchviewTransfer.isChecked())
                close();
            else
                transfer();
            return false;
        });
        smartRefreshLayout.setNoMoreData(true);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            log();
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_LIST:
                    case ATConstants.Config.SERVER_URL_SIPUSER:
                        List<ATSipListBean> atSipListBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATSipListBean>>() {
                        }.getType());
                        if (atSipListBean.size() > 0) {
                            mAtSipListBean = atSipListBean.get(0);
                            log();
                            switchview.setChecked(mAtSipListBean.getStatus() == 1);
                            switchviewTransfer.setChecked(mAtSipListBean.getOpenManager() == 1);
                            if (mAtSipListBean.getStatus() == 1) {
                                try {
                                    VoipManager.getInstance().login(mAtSipListBean.getSipNumber(), mAtSipListBean.getSipPassword(),
                                            mAtSipListBean.getSipHost(), mAtSipListBean.getSipPort());
                                } catch (EVVoipException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_OPEN:
                        switchview.setChecked(true);
                        if (mAtSipListBean != null)
                            try {
                                VoipManager.getInstance().login(mAtSipListBean.getSipNumber(), mAtSipListBean.getSipPassword(), mAtSipListBean.getSipHost(), mAtSipListBean.getSipPort());
                            } catch (EVVoipException e) {
                                e.printStackTrace();
                            }
                        break;
                    case ATConstants.Config.SERVER_URL_CLOSE:
                        switchview.setChecked(false);
                        VoipManager.getInstance().logout();
                        break;
                    case ATConstants.Config.SERVER_URL_MANAGER_CLOSE:
                        switchviewTransfer.setChecked(false);
                        break;
                    case ATConstants.Config.SERVER_URL_MANAGER_TRANSFER:
                        switchviewTransfer.setChecked(true);
                        break;
                    case ATConstants.Config.SERVER_URL_LOG:
                        List<ATVisualIntercomRecordBean> ATVisualIntercomRecordBeans = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATVisualIntercomRecordBean>>() {
                        }.getType());
                        if (ATVisualIntercomRecordBeans.size() == 0) {
                            smartRefreshLayout.setNoMoreData(true);
                        } else {
                            smartRefreshLayout.setNoMoreData(false);
                        }
                        mVisualIntercomRVAdapter.setList(ATVisualIntercomRecordBeans);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}