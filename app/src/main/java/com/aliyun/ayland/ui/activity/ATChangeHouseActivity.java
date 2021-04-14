package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATSipListBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATChangeHouseRVAdapter;
import com.aliyun.ayland.widget.voip.VoipManager;
import com.anthouse.wyzhuoyue.R;
import com.evideo.voip.sdk.EVVoipException;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATChangeHouseActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean mHouseBean;
    private ATChangeHouseRVAdapter mATChangeHouseRVAdapter;
    private List<ATHouseBean> mHouseList = new ArrayList<>();
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout llContent;
    private TextView tvNoData;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_change_house;
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        llContent = findViewById(R.id.ll_content);
        tvNoData = findViewById(R.id.tv_no_data);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        smartRefreshLayout.autoRefresh();
    }

    private void list() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 0);
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        jsonObject.put("targetId", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATGlobalApplication.isIsWuye() ? ATConstants.Config.SERVER_URL_SIPUSER : ATConstants.Config.SERVER_URL_LIST, jsonObject);
    }

    private void setPresent() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", ATGlobalApplication.getAccount());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_SETPRESENT, jsonObject);
    }

    private void findUserHouse() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", ATGlobalApplication.getAccount());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDUSERHOUSE, jsonObject);
    }

    private void init() {
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        tvNoData.setText("还没有房屋哦～");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mATChangeHouseRVAdapter = new ATChangeHouseRVAdapter(this);
        recyclerView.setAdapter(mATChangeHouseRVAdapter);
        mATChangeHouseRVAdapter.setOnItemClickListener((view, position) -> {
            mHouseBean = mHouseList.get(position);
            list();
            setPresent();
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> findUserHouse());
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_SETPRESENT:
                        showToast(getString(R.string.at_change_house_success));

                        ATGlobalApplication.setHouse(mHouseBean.toString());
                        ATGlobalApplication.setHouseState(mHouseBean.getHouseState());
                        if (mHouseList.size() == 0) {
                            llContent.setVisibility(View.VISIBLE);
                        } else {
                            llContent.setVisibility(View.GONE);
                        }
                        mATChangeHouseRVAdapter.setList(mHouseList, mHouseBean);
                        break;
                    case ATConstants.Config.SERVER_URL_LIST:
                    case ATConstants.Config.SERVER_URL_SIPUSER:
                        VoipManager.getInstance().logout();
                        List<ATSipListBean> atSipListBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATSipListBean>>() {
                        }.getType());
                        if (atSipListBean.size() > 0) {
                            ATSipListBean sipListBean = atSipListBean.get(0);
                            if (sipListBean.getStatus() == 1)
                                try {
                                    VoipManager.getInstance().login(sipListBean.getSipNumber(), sipListBean.getSipPassword()
                                            , sipListBean.getSipHost(), sipListBean.getSipPort());
                                } catch (EVVoipException e) {
                                    e.printStackTrace();
                                }
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_FINDUSERHOUSE:
                        mHouseList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATHouseBean>>() {
                        }.getType());
                        if (mHouseList.size() == 0) {
                            llContent.setVisibility(View.VISIBLE);
                        } else {
                            llContent.setVisibility(View.GONE);
                        }
                        mATChangeHouseRVAdapter.setList(mHouseList, mHouseBean);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishLoadMore();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
            closeBaseProgressDlg();
            smartRefreshLayout.finishLoadMore();
            smartRefreshLayout.finishRefresh();
        }
    }
}