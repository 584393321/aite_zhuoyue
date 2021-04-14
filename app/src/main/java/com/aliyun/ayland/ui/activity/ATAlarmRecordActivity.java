package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATAlarmRecordBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATAlarmRecordRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;

public class ATAlarmRecordActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATAlarmRecordRVAdapter mAlarmRecordRVAdapter;
    private ATMyTitleBar titlebar;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private int startNum = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_security1;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findAlarmRecord();
    }

    private void findAlarmRecord() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("startNum", startNum);
        jsonObject.put("status", "0");
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDALARMRECORD, jsonObject);
    }

    private void init() {
        titlebar.setTitle(getString(R.string.at_alarm_record));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlarmRecordRVAdapter = new ATAlarmRecordRVAdapter(this);
        recyclerView.setAdapter(mAlarmRecordRVAdapter);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                startNum += 10;
                findAlarmRecord();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                startNum = 0;
                findAlarmRecord();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDALARMRECORD:
                        List<ATAlarmRecordBean> accessRecordBeans = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATAlarmRecordBean>>() {
                        }.getType());
                        if (accessRecordBeans.size() == 0) {
                            smartRefreshLayout.setNoMoreData(true);
                        } else {
                            smartRefreshLayout.setNoMoreData(false);
                        }
                        mAlarmRecordRVAdapter.setLists(accessRecordBeans, startNum);
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