package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCarListBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATCarApplyRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;

public class ATCarApplyActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATCarApplyRVAdapter mATCarApplyRVAdapter;
    private ATMyTitleBar titlebar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private int pageNo = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recycleview_sml;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        smartRefreshLayout.autoRefresh();
    }

    private void list() {
        ATHouseBean mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        if (mATHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
//        jsonObject.put("status", 1);
        jsonObject.put("pageNo", pageNo);
        jsonObject.put("pageSize", 20);
        mPresenter.request(ATConstants.Config.SERVER_URL_CARPASSLIST, jsonObject);
    }

    private void init() {
        titlebar.setTitle(getString(R.string.at_application_record));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mATCarApplyRVAdapter = new ATCarApplyRVAdapter(this);
        recyclerView.setAdapter(mATCarApplyRVAdapter);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo ++;
                list();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo = 1;
                list();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CARPASSLIST:
                        List<ATCarListBean> carList = gson.fromJson(jsonResult.getJSONObject("data").getString("content"), new TypeToken<List<ATCarListBean>>() {
                        }.getType());
                        mATCarApplyRVAdapter.setLists(carList, pageNo);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}