package com.aliyun.wuye.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATSceneContract;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATEventString;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATShareAppointRecordBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATScenePresenter;
import com.aliyun.wuye.ui.adapter.ATShareHandleAppointRecordRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATAppointRecordHandleFragment extends ATBaseFragment implements ATSceneContract.View {
    private ATScenePresenter mPresenter;
    private ATShareHandleAppointRecordRVAdapter mATWYShareAppointRecordRVAdapter;
    private ATHouseBean houseBean;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private int pageNo = 1;
    private List<ATShareAppointRecordBean> mAtShareAppointBeanList = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventClazz eventClazz) {
        if ("ATAppointRecordHandleFragment".equals(eventClazz.getClazz())) {
            smartRefreshLayout.autoRefresh();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_recyclerview_sml;
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATScenePresenter(this);
        mPresenter.install(getActivity());
        findAppointmentStatusWuye();
    }

    private void findAppointmentStatusWuye() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", houseBean.getVillageId());
        jsonObject.put("pageNo", pageNo);
        jsonObject.put("pageSize", 10);
        jsonObject.put("handle", 1);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDAPPOINTMENTSTATUSWUYE, jsonObject, 1);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mATWYShareAppointRecordRVAdapter = new ATShareHandleAppointRecordRVAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mATWYShareAppointRecordRVAdapter);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo++;
                findAppointmentStatusWuye();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo = 1;
                findAppointmentStatusWuye();
                smartRefreshLayout.setNoMoreData(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void requestResult(String result, String url, Object o) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                if (ATConstants.Config.SERVER_URL_FINDAPPOINTMENTSTATUSWUYE.equals(url) && (Integer) o == 1) {
                    List<ATShareAppointRecordBean> atShareAppointBeanList = gson.fromJson(jsonResult.getString("result"), new TypeToken<List<ATShareAppointRecordBean>>() {
                    }.getType());
                    if (pageNo == 1)
                        mAtShareAppointBeanList.clear();
                    if (atShareAppointBeanList.size() == 0) {
                        if (pageNo != 1)
                            pageNo--;
                        smartRefreshLayout.setNoMoreData(true);
                        return;
                    }
                    mAtShareAppointBeanList.addAll(atShareAppointBeanList);
                    mATWYShareAppointRecordRVAdapter.setLists(mAtShareAppointBeanList);
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