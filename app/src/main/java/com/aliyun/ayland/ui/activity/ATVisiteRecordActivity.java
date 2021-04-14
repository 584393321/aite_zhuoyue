package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATVisiteRecordRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;

public class ATVisiteRecordActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private ATVisiteRecordRVAdapter mVisitedRecordRVAdapter;
    private LinearLayout llContent;
    private TextView tvNoData;
    private int pageNum = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recycleview_smr2;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
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
        findVisitedPage();
    }

    private void findVisitedPage() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNo", pageNum);
        jsonObject.put("pageSize", 10);
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDVISITEDPAGE, jsonObject);
    }

    private void init() {
        titleBar.setTitle(getString(R.string.at_visited_record));

        tvNoData.setText(getString(R.string.at_has_no_visited_record));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVisitedRecordRVAdapter = new ATVisiteRecordRVAdapter(this);
        recyclerView.setAdapter(mVisitedRecordRVAdapter);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum++;
                findVisitedPage();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum = 0;
                smartRefreshLayout.setNoMoreData(false);
                findVisitedPage();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDVISITEDPAGE:
                        List<ATVisitorReservateBean> visitorReservateList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATVisitorReservateBean>>() {
                        }.getType());
                        if (pageNum == 0 && visitorReservateList.size() == 0) {
                            llContent.setVisibility(View.VISIBLE);
                        } else {
                            llContent.setVisibility(View.GONE);
                        }
                        if (visitorReservateList.size() == 0)
                            smartRefreshLayout.setNoMoreData(true);
                        mVisitedRecordRVAdapter.setLists(visitorReservateList, pageNum);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}