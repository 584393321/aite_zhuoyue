package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCaringRecordBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATFamilyMemberCareBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATCaringRecordRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATOldYoungCareFindLocationActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean mATHouseBean;
    private ATCaringRecordRVAdapter mATCaringRecordRVAdapter;
    private List<ATFamilyMemberCareBean> mATFamilyMemberCareBeanList = new ArrayList<>();
    private List<ATFamilyMemberCareBean> mATFamilyMemberCareBeanReallyList = new ArrayList<>();
    private ATMyTitleBar titlebar;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private int startNum = 0;
    private List<ATCaringRecordBean> mATCaringRecordBeanList= new ArrayList<>();
    private LinearLayout llEmpty;
    private TextView tvNoData;
    private Button btnOpenFunction;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_old_young_care_find_location;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        llEmpty = findViewById(R.id.ll_empty);
        btnOpenFunction = findViewById(R.id.btn_open_function);
        tvNoData = findViewById(R.id.tv_no_data);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findOutAloneRecord();
    }

    private void findOutAloneRecord() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("villageId", mATHouseBean.getVillageId());
        jsonObject.put("startNum", startNum);
        jsonObject.put("pageSize", 20);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDOUTALONERECORD, jsonObject);
    }

    private void init() {
        mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        titlebar.setSendText(getString(R.string.at_add));
        titlebar.setRightClickListener(() -> startActivity(new Intent(this, ATCareFunctionSetActivity.class)));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mATCaringRecordRVAdapter = new ATCaringRecordRVAdapter(this);
        recyclerView.setAdapter(mATCaringRecordRVAdapter);
        mATCaringRecordRVAdapter.setOnItemClickListener(position -> {
            startActivity(new Intent(this, ATOldYoungCareFindLocationDetailsActivity.class)
                    .putExtra("type", 1)
                    .putExtra("ATCaringRecordBean", mATCaringRecordBeanList.get(position)));
        });
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                startNum += 20;
                findOutAloneRecord();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                startNum = 0;
                findOutAloneRecord();
            }
        });
        btnOpenFunction.setOnClickListener(view -> startActivity(new Intent(this, ATCareFunctionSetActivity.class)));
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDOUTALONERECORD:
                        Log.e("modelrequestResult: ", result);
                        List<ATCaringRecordBean> aTCaringRecordBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATCaringRecordBean>>() {
                        }.getType());
                        if(startNum == 0)
                            mATCaringRecordBeanList.clear();
                        mATCaringRecordBeanList.addAll(aTCaringRecordBeanList);
                        mATCaringRecordRVAdapter.setLists(mATCaringRecordBeanList);
                        if(mATCaringRecordBeanList.size() <= 0){
                            llEmpty.setVisibility(View.VISIBLE);
                            if(0 == jsonResult.getInt("careType")){
                                tvNoData.setText(getString(R.string.at_no_record2));
                                btnOpenFunction.setVisibility(View.VISIBLE);
                            }else {
                                tvNoData.setText(getString(R.string.at_no_record1));
                                btnOpenFunction.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}