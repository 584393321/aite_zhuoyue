package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATShareAppointRecordBean;
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATVisitorAppointRecordRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATAppointRecordFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVisitorAppointRecordRVAdapter mVisitorAppointRecordRVAdapter;
    private ATHouseBean houseBean;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llContent;
    private int pageNo = 1, mPosition, appointmentStatus;
    private List<ATShareAppointRecordBean> mAtShareAppointBeanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_recyclerview_sml;
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        llContent = view.findViewById(R.id.ll_content);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void getAllVisitorReservationList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", ATGlobalApplication.getHid());
        jsonObject.put("pageNum", pageNo);
        jsonObject.put("pageSize", 10);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETALLVISITORRESERVATIONLIST, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mVisitorAppointRecordRVAdapter = new ATVisitorAppointRecordRVAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mVisitorAppointRecordRVAdapter);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo++;
                getAllVisitorReservationList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo = 1;
                getAllVisitorReservationList();
                smartRefreshLayout.setNoMoreData(false);
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                if (ATConstants.Config.SERVER_URL_GETALLVISITORRESERVATIONLIST.equals(url)) {
                    List<ATVisitorReservateBean> visitorReservateList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATVisitorReservateBean>>() {
                    }.getType());
                    if (pageNo == 1 && visitorReservateList.size() == 0) {
                        llContent.setVisibility(View.VISIBLE);
                    } else {
                        llContent.setVisibility(View.GONE);
                    }
                    if (visitorReservateList.size() == 0)
                        smartRefreshLayout.setNoMoreData(true);
                    mVisitorAppointRecordRVAdapter.setLists(visitorReservateList, pageNo);
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

    @Override
    public void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }
}