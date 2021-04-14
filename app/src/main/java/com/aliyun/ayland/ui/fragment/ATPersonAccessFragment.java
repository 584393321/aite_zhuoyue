package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATAccessRecordHumanBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATAccessRecordHumanRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ATPersonAccessFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private int pageNum = 0;
    private String endTime, startTime;
    private ATAccessRecordHumanRVAdapter mRecordHumanRVAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private TextView tvNoData;
    private LinearLayout llContent;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_recyclerview_sml;
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        tvNoData = view.findViewById(R.id.tv_no_data);
        llContent = view.findViewById(R.id.ll_content);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
        showBaseProgressDlg();
        findHumnRecord();
    }

    public void findHumnRecord(String accessType, String startTime, String endTime) {
        this.endTime = endTime;
        this.startTime = startTime;
        pageNum = 0;
        if ("out".equals(accessType)) {
            List<ATAccessRecordHumanBean> accessRecordHumanBeanList = new ArrayList<>();
            llContent.setVisibility(View.VISIBLE);
            smartRefreshLayout.setNoMoreData(true);
            mRecordHumanRVAdapter.setLists(accessRecordHumanBeanList, pageNum);
        } else {
            findHumnRecord();
        }
    }

    private void findHumnRecord() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", pageNum);
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        if (!TextUtils.isEmpty(startTime) && !getString(R.string.at_please_choose).equals(startTime))
            jsonObject.put("startTime", startTime);
        if (!TextUtils.isEmpty(endTime) && !getString(R.string.at_please_choose).equals(endTime))
            jsonObject.put("endTime", endTime);
        mPresenter.request(ATConstants.Config.SERVER_URL_QUERYVISITORRECORD, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        mRecordHumanRVAdapter = new ATAccessRecordHumanRVAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mRecordHumanRVAdapter);

        tvNoData.setText(getString(R.string.at_have_none_pass_record));

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNum += 7;
                findHumnRecord();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum = 0;
                smartRefreshLayout.setNoMoreData(false);
                findHumnRecord();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_QUERYVISITORRECORD:
                        List<ATAccessRecordHumanBean> accessRecordHumanBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATAccessRecordHumanBean>>() {
                        }.getType());
                        if (accessRecordHumanBeanList != null) {
                            if (pageNum == 0 && accessRecordHumanBeanList.size() == 0) {
                                llContent.setVisibility(View.VISIBLE);
                            } else {
                                llContent.setVisibility(View.GONE);
                            }
                            mRecordHumanRVAdapter.setLists(accessRecordHumanBeanList, pageNum);
                            if (accessRecordHumanBeanList.size() < 7) {
                                smartRefreshLayout.setNoMoreData(true);
                            }
                        } else {
//                            tvNoData.setText(jsonResult.getString("message"));
                            llContent.setVisibility(View.VISIBLE);
                        }
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