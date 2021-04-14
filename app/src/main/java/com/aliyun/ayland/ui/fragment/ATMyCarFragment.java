package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCarListBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATMyCarRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.List;

public class ATMyCarFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATMyCarRVAdapter mATMyCarRVAdapter;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout llEmpty;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_my_car;
    }

    @Override
    protected void findView(View view) {
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        llEmpty = view.findViewById(R.id.ll_empty);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void list() {
        ATHouseBean mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        if (mATHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        jsonObject.put("status", 1);
        mPresenter.request(ATConstants.Config.SERVER_URL_CARPASSLIST, jsonObject);
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mATMyCarRVAdapter = new ATMyCarRVAdapter(getActivity());
        recyclerView.setAdapter(mATMyCarRVAdapter);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            list();
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
                        if (carList.size() == 0) {
                            llEmpty.setVisibility(View.VISIBLE);
                        } else {
                            llEmpty.setVisibility(View.GONE);
                        }
                        mATMyCarRVAdapter.setLists(carList);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        list();
    }
}