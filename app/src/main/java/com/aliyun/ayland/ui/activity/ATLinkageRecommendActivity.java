package com.aliyun.ayland.ui.activity;

import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATBrightnessLightBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageMorningRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATLinkageRecommendActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATLinkageMorningRVAdapter mLinkageMorningRVAdapter;
    private String cron_week, week_text;
    private int hour, min, total_min, total_second;
    private List<ATBrightnessLightBean> mBrightnessLightList = new ArrayList<>();
    private ATHouseBean mHouseBean;
    private ATMyTitleBar titleBar;
    private ImageView imgScene;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_recommend;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        imgScene = findViewById(R.id.img_scene);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
//        getBrightnessLight();
    }

    private void addMorningGetUpPattern() {
        if (mHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONArray targetIdList = new JSONArray();
        for (Integer integer : mLinkageMorningRVAdapter.getSet()) {
            targetIdList.add(mBrightnessLightList.get(integer).getIotId());
        }
        JSONArray triggers = new JSONArray();
        JSONObject object = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("cron", min + " " + hour + " * * " + cron_week);
        params.put("cronType", "linux");
        object.put("params", params);
        object.put("uri", "trigger/timer");
        triggers.add(object);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("targetIdList", targetIdList);
        jsonObject.put("totalTime", total_min * 60 + total_second);
        jsonObject.put("triggers", triggers);
        jsonObject.put("villageId", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDMORNINGGETUPPATTERN, jsonObject);
    }

    private void getBrightnessLight() {
        if (mHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("villageId", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETBRIGHTNESSLIGHT, jsonObject);
    }

    private void init() {
        int position = getIntent().getIntExtra("position", 0);
        switch (position) {
            case 0:
                imgScene.setImageResource(R.drawable.szwj_ld_bg_a);
                titleBar.setTitle(getString(R.string.at_home_model));
                break;
            case 1:
                imgScene.setImageResource(R.drawable.szwj_ld_bg_b);
                titleBar.setTitle(getString(R.string.at_out_model));
                break;
            case 2:
                imgScene.setImageResource(R.drawable.szwj_ld_bg_c);
                titleBar.setTitle(getString(R.string.at_security_model));
                break;
        }
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        cron_week = "*";
        week_text = "每天";
        hour = 7;
        min = 0;
        total_min = 5;
        total_second = 0;

//        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        });
//        recyclerView.setNestedScrollingEnabled(false);
//        mLinkageMorningRVAdapter = new LinkageMorningRVAdapter(this);
//        recyclerView.setAdapter(mLinkageMorningRVAdapter);

        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
//            getBrightnessLight();
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ADDMORNINGGETUPPATTERN:
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_morning_model_success));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_GETBRIGHTNESSLIGHT:
                        mBrightnessLightList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATBrightnessLightBean>>() {
                        }.getType());
                        mLinkageMorningRVAdapter.setLists(mBrightnessLightList);
                        break;
                }
            } else {
                closeBaseProgressDlg();
                if (jsonResult.has("message")) {
                    showToast(jsonResult.getString("message"));
                } else if (jsonResult.has("localizedMsg")) {
                    showToast(jsonResult.getString("localizedMsg"));
                } else {
                    showToast(getString(R.string.at_morning_model_failed));
                }
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}