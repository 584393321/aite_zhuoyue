package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFindSafetyApplicationBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATWisdomSecurityRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.List;

public class ATWisdomSecurityActivity1 extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_empty1;
    }

    @Override
    protected void findView() {
        findViewById(R.id.ll_zhaf).setOnClickListener(v -> {

        });
        findViewById(R.id.ll_jtbf);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findSafetyApplication() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDSAFETYAPPLICATION, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
//        titleBar.setTitle(getString(R.string.at_wisdom_security));
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mWisdomSecurityRVAdapter = new ATWisdomSecurityRVAdapter(this);
//        recyclerView.setAdapter(mWisdomSecurityRVAdapter);
//        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
//            refreshLayout.finishRefresh(2000);
//            findSafetyApplication();
//        });
//
//        mWisdomSecurityRVAdapter.setOnItemClickListener((view, o, position) -> {
//            switch (((ATFindSafetyApplicationBean) o).getApplicationIdentification()) {
//                case "app_elderly_care_subscribe":
//                    //独居老人关怀订阅
//                    startActivity(new Intent(this, ATWisdomSecurityLiveAloneActivity.class).putExtra("status", ((ATFindSafetyApplicationBean) o).getStatus()));
//                    break;
//                case "app_go_out_abnormal_subscribe":
//                    //出门习惯异常关怀订阅
//                    startActivity(new Intent(this, ATWisdomSecurityOutAbnormalActivity.class).putExtra("status", ((ATFindSafetyApplicationBean) o).getStatus())
//                            .putExtra("appId", ((ATFindSafetyApplicationBean) o).getApplicationId()));
//                    break;
//                case "app_independent_care_subscribe":
//                    //独出社区关怀订阅
//                    startActivity(new Intent(this, ATWisdomSecurityOutAloneActivity.class).putExtra("status", ((ATFindSafetyApplicationBean) o).getStatus())
//                            .putExtra("appId", ((ATFindSafetyApplicationBean) o).getApplicationId()));
//                    break;
//            }
//        });
    }

    @Override
    public void requestResult(String result, String url) {
//        try {
//            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
//            if ("200".equals(jsonResult.getString("code"))) {
//                switch (url) {
//                    case ATConstants.Config.SERVER_URL_FINDSAFETYAPPLICATION:
//                        List<ATFindSafetyApplicationBean> applicationList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATFindSafetyApplicationBean>>() {
//                        }.getType());
//                        mWisdomSecurityRVAdapter.setLists(applicationList);
//                        break;
//                }
//            } else {
//                showToast(jsonResult.getString("message"));
//            }
//            smartRefreshLayout.finishRefresh();
//            closeBaseProgressDlg();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}