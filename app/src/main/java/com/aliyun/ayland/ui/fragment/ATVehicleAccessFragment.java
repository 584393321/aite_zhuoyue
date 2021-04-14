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
import com.aliyun.ayland.data.ATAccessRecordBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATAccessRecordRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;


public class ATVehicleAccessFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATAccessRecordRVAdapter mAccessRecordRVAdapter;
    private int pageNum = 0;
    private String approach, endTime, startTime;
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
        findCarParkRecord();
    }

    public void findCarParkRecord(String approach, String startTime, String endTime) {
        this.approach = approach;
        this.endTime = endTime;
        this.startTime = startTime;
        pageNum = 0;
        findCarParkRecord();
    }

    private void findCarParkRecord() {
//        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("pageNum", pageNum);
        if (!TextUtils.isEmpty(approach))
            jsonObject.put("approach", approach);
        if (!TextUtils.isEmpty(startTime) && !getString(R.string.at_please_choose).equals(startTime))
            jsonObject.put("startTime", startTime);
        if (!TextUtils.isEmpty(endTime) && !getString(R.string.at_please_choose).equals(endTime))
            jsonObject.put("endTime", endTime);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDCARPARKRECORD, jsonObject);
    }

    private void init() {
        mAccessRecordRVAdapter = new ATAccessRecordRVAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAccessRecordRVAdapter);

        tvNoData.setText(getString(R.string.at_have_none_access_record));

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNum += 7;
                findCarParkRecord();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNum = 0;
                smartRefreshLayout.setNoMoreData(false);
                findCarParkRecord();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDCARPARKRECORD:
                        List<ATAccessRecordBean> accessRecordBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATAccessRecordBean>>() {
                        }.getType());
                        if (accessRecordBeanList != null) {
                            if (pageNum == 0 && accessRecordBeanList.size() == 0) {
                                llContent.setVisibility(View.VISIBLE);
                            } else {
                                llContent.setVisibility(View.GONE);
                            }
                            mAccessRecordRVAdapter.setLists(accessRecordBeanList, pageNum);
                            if (accessRecordBeanList.size() < 7) {
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