package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCategory;
import com.aliyun.ayland.data.ATCategoryBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATDiscoveryDeviceControlRVAdapter;
import com.aliyun.ayland.ui.adapter.ATDiscoveryDeviceRightRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATDiscoveryDeviceCloudFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainContract.Presenter mPresenter;
    private ATDiscoveryDeviceControlRVAdapter mDiscoveryDeviceControlRVAdapter;
    private ATDiscoveryDeviceRightRVAdapter mDiscoveryDeviceRightRVAdapter;
    private int pageNo = 1;
    private String categoryKey, productName;
    private List<ATCategoryBean> mATCategoryBeanList;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView rvControl, rvDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_discovery_device_cloud;
    }

    @Override
    protected void findView(View view) {
        rvDevice = view.findViewById(R.id.rv_device);
        rvControl = view.findViewById(R.id.rv_control);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
        category();
//        getProductList("", 1, 20);
    }

    private void category() {
        JSONObject jsonObject = new JSONObject();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_CATEGORY, jsonObject);
    }

    private void getProductList() {
        JSONObject productQuery = new JSONObject();
        productQuery.put("categoryKey", categoryKey);
        productQuery.put("productName", productName);
        productQuery.put("pageNo", pageNo);

        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("productQuery", productQuery);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_PRODUCTLIST, jsonObject);
    }

    private void init() {
        mDiscoveryDeviceControlRVAdapter = new ATDiscoveryDeviceControlRVAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvControl.setLayoutManager(layoutManager);
        rvControl.setHasFixedSize(true);
        rvControl.setAdapter(mDiscoveryDeviceControlRVAdapter);
        mDiscoveryDeviceControlRVAdapter.setOnItemClickListener(position -> {
            categoryKey = mATCategoryBeanList.get(position).getCategoryKey();
            productName = "";
            pageNo = 1;
            mDiscoveryDeviceRightRVAdapter.setLists(new ArrayList<>(), 1);
            getProductList();
        });

        mDiscoveryDeviceRightRVAdapter = new ATDiscoveryDeviceRightRVAdapter(getActivity());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        rvDevice.setLayoutManager(layoutManager1);
        rvDevice.setHasFixedSize(true);
        rvDevice.setAdapter(mDiscoveryDeviceRightRVAdapter);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNo++;
                getProductList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                smartRefreshLayout.setNoMoreData(false);
                pageNo = 1;
                getProductList();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CATEGORY:
                        mATCategoryBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATCategoryBean>>() {
                        }.getType());
                        if (mATCategoryBeanList.size() > 0) {
                            categoryKey = mATCategoryBeanList.get(0).getCategoryKey();
                            productName = "";
                            pageNo = 1;
                            getProductList();
                        }
                        mDiscoveryDeviceControlRVAdapter.setLists(mATCategoryBeanList);
                        break;
                    case ATConstants.Config.SERVER_URL_PRODUCTLIST:
                        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResult.getString("data"));
                        List<ATCategory> categoryList = gson.fromJson(jsonObject.getString("data"), new TypeToken<List<ATCategory>>() {
                        }.getType());
                        mDiscoveryDeviceRightRVAdapter.setLists(categoryList, pageNo);
                        if (categoryList.size() == 0) {
                            smartRefreshLayout.setNoMoreData(true);
                        }
                        smartRefreshLayout.finishRefresh();
                        smartRefreshLayout.finishLoadMore();
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}