package com.aliyun.wuye.ui.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
import com.aliyun.wuye.ui.adapter.ATVisitorAppointRecordRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;

public class ATVisitorRecordActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVisitorAppointRecordRVAdapter mVisitorAppointRecordRVAdapter;
    private int pageNum;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout mainContent;
    private TextView tvBack, tvTitle, tvNoData, tvVisitorAppoint;
    private RelativeLayout iv;
    private RecyclerView rvVisitorRecord;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llContent;
    private ATHouseBean mHouseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wy_visitor_record;
    }

    @Override
    protected void findView() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        mainContent = findViewById(R.id.main_content);
        tvBack = findViewById(R.id.tv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvVisitorAppoint = findViewById(R.id.tv_visitor_appoint);
        iv = findViewById(R.id.iv);
        rvVisitorRecord = findViewById(R.id.rv_visitor_record);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        llContent = findViewById(R.id.ll_content);
        tvNoData = findViewById(R.id.tv_no_data);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findVisitorPage() {
        if(mHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        jsonObject.put("pageNum", pageNum);
        jsonObject.put("pageSize", 10);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDVISITORPAGE, jsonObject);
    }

    private void init() {
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, false);
        ATAutoUtils.auto(collapsingToolbarLayout);
        ATAutoUtils.auto(appBarLayout);
        ATAutoUtils.auto(mainContent);
        tvBack.setOnClickListener(view -> finish());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            iv.getBackground().setAlpha(255 * verticalOffset / (-appBarLayout.getTotalScrollRange()));
            if (iv.getBackground().getAlpha() == 0) {
                ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, false);
                tvBack.setSelected(false);
                tvTitle.setTextColor(getResources().getColor(R.color.white));
            } else if (iv.getBackground().getAlpha() == 255) {
                ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, true);
                tvBack.setSelected(true);
                tvTitle.setTextColor(getResources().getColor(R.color._333333));
            }
        });
        tvVisitorAppoint.setOnClickListener(view -> startActivity(new Intent(this, ATVisiteAppointActivity.class)));

//        include.img.setImageResource(R.drawable.o_empty_invitevisitors_normal);
        tvNoData.setText(getString(R.string.at_have_no_visite_message));
//        include.tvButton.setText(getString(R.string.go_to_visite));
//        include.tvButton.setOnClickListener(view -> startActivity(new Intent(this, VisitorAppointActivity.class)));

        rvVisitorRecord.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvVisitorRecord.setNestedScrollingEnabled(false);
        mVisitorAppointRecordRVAdapter = new ATVisitorAppointRecordRVAdapter(this);
        rvVisitorRecord.setAdapter(mVisitorAppointRecordRVAdapter);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum++;
                findVisitorPage();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum = 1;
                smartRefreshLayout.setNoMoreData(false);
                findVisitorPage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBaseProgressDlg();
        pageNum = 1;
        smartRefreshLayout.setNoMoreData(false);
        findVisitorPage();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDVISITORPAGE:
                        List<ATVisitorReservateBean> visitorReservateList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATVisitorReservateBean>>() {
                        }.getType());
                        if (pageNum == 1 && visitorReservateList.size() == 0) {
                            llContent.setVisibility(View.VISIBLE);
                        } else {
                            llContent.setVisibility(View.GONE);
                        }
                        if (visitorReservateList.size() == 0)
                            smartRefreshLayout.setNoMoreData(true);
                        mVisitorAppointRecordRVAdapter.setLists(visitorReservateList, pageNum);
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