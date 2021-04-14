package com.aliyun.ayland.ui.activity;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATSceneActionBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.interfaces.ATOnEPLItemClickListener;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATRecommendSceneEPLAdapter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATRecommendSceneDetailActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean mHouseBean;
    private String recommendSceneId, recommendSceneType;
    private List<ATSceneActionBean> mSceneActionBeanList;
    private ATRecommendSceneEPLAdapter mRecommendSceneEPLAdapter;
    private Button button;
    private ExpandableListView expandableListView;
    private SmartRefreshLayout smartRefreshLayout;
    private TextView tvType, tvStatus;
    private int current_groupPosition = 0, current_childPosition = 0, deviceStatus = 0, recommendSceneStatus = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recommend_scene_detail;
    }

    @Override
    protected void findView() {
        tvType = findViewById(R.id.tv_type);
        tvStatus = findViewById(R.id.tv_status);
        button = findViewById(R.id.button);
        expandableListView = findViewById(R.id.expandableListView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getEnvironmentRecommendSceneInfo();
    }

    private void updateEnvironmentRecommendSceneInfo() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recommendSceneStatus", recommendSceneStatus == 0 ? 1 : 0);
        jsonObject.put("id", recommendSceneId);
        mPresenter.request(ATConstants.Config.SERVER_URL_UPDATEENVIRONMENTRECOMMENDSCENEINFO, jsonObject);
    }

    private void updateEnvironmentRecommendSceneDeviceInfo() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceStatus", mSceneActionBeanList.get(current_groupPosition).getDeviceList().get(current_childPosition).getDeviceStatus() == 1 ? 0 : 1);
        jsonObject.put("id", mSceneActionBeanList.get(current_groupPosition).getDeviceList().get(current_childPosition).getId());
        mPresenter.request(ATConstants.Config.SERVER_URL_UPDATEENVIRONMENTRECOMMENDSCENEDEVICEINFO, jsonObject);
    }

    private void getEnvironmentRecommendSceneInfo() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("villageId", mHouseBean.getVillageId());
        jsonObject.put("recommendSceneId", recommendSceneId);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETENVIRONMENTRECOMMENDSCENEINFO, jsonObject);
    }

    private void init() {
        recommendSceneId = getIntent().getStringExtra("recommendSceneId");
        recommendSceneType = getIntent().getStringExtra("recommendSceneType");
        recommendSceneStatus = getIntent().getIntExtra("recommendSceneStatus", 0);
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        tvType.setText(ATResourceUtils.getResIdByName(String.format(getString(R.string.at_weather_), recommendSceneType)
                , ATResourceUtils.ResourceType.STRING));
        tvStatus.setText(ATResourceUtils.getResIdByName(String.format(getString(R.string.at_weather_status_), recommendSceneType)
                , ATResourceUtils.ResourceType.STRING));

        button.setBackground(recommendSceneStatus == 0 ? getResources().getDrawable(R.drawable.shape_66px_e85d3e_to_ef6b49) :
                getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
        button.setText(recommendSceneStatus == 0 ? getString(R.string.at_stop_scene) : getString(R.string.at_start_scene));

        mRecommendSceneEPLAdapter = new ATRecommendSceneEPLAdapter(this);
        mRecommendSceneEPLAdapter.setOnItemClickListener(new ATOnEPLItemClickListener() {
            @Override
            public void onItemClick(int groupPosition) {

            }

            @Override
            public void onItemClick(int groupPosition, int childPosition) {

            }

            @Override
            public void onItemClick(int groupPosition, int childPosition, int flag) {
                current_groupPosition = groupPosition;
                current_childPosition = childPosition;
                deviceStatus = flag;
                updateEnvironmentRecommendSceneDeviceInfo();
            }
        });
        expandableListView.setAdapter(mRecommendSceneEPLAdapter);
        expandableListView.setOnGroupClickListener((expandableListView, v, i, l) -> true);
        button.setOnClickListener(view -> updateEnvironmentRecommendSceneInfo());
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_UPDATEENVIRONMENTRECOMMENDSCENEINFO:
                        recommendSceneStatus = recommendSceneStatus == 0 ? 1 : 0;
                        button.setBackground(recommendSceneStatus == 0 ? getResources().getDrawable(R.drawable.shape_66px_e85d3e_to_ef6b49) :
                                getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
                        button.setText(recommendSceneStatus == 0 ? getString(R.string.at_stop_scene) : getString(R.string.at_start_scene));
                        showToast(getString(R.string.at_operate_success));
                        break;
                    case ATConstants.Config.SERVER_URL_UPDATEENVIRONMENTRECOMMENDSCENEDEVICEINFO:
                        mSceneActionBeanList.get(current_groupPosition).getDeviceList().get(current_childPosition).setDeviceStatus(deviceStatus);
                        mRecommendSceneEPLAdapter.setList(mSceneActionBeanList);
                        showToast(getString(R.string.at_operate_success));
                        break;
                    case ATConstants.Config.SERVER_URL_GETENVIRONMENTRECOMMENDSCENEINFO:
                        mSceneActionBeanList = gson.fromJson(jsonResult.getJSONObject("data").getString("sceneActions"), new TypeToken<List<ATSceneActionBean>>() {
                        }.getType());
                        mRecommendSceneEPLAdapter.setList(mSceneActionBeanList);
                        for (int i = 0; i < mSceneActionBeanList.size(); i++) {
                            expandableListView.expandGroup(i);
                        }
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