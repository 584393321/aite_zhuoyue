package com.aliyun.ayland.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCategory;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATDiscoveryDeviceSearchRVAdapter;
import com.aliyun.ayland.utils.ATAddDeviceScanHelper;
import com.aliyun.ayland.widget.ATClearEditText;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.List;

public class ATDiscoveryDeviceSearchActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATDiscoveryDeviceSearchRVAdapter mDiscoveryDeviceSearchRVAdapter;
    private int pageNo = 1;
    private String productName = "";
    private RecyclerView recyclerView;
    private ATClearEditText etSearch;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_discovery_device_search;
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.et_search);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getProductList();
    }

    private void getProductList() {
        JSONObject productQuery = new JSONObject();
        productQuery.put("categoryKey", "");
        productQuery.put("productName", productName);
        productQuery.put("pageNo", pageNo);
        productQuery.put("pageSize", 100);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDiscoveryDeviceSearchRVAdapter = new ATDiscoveryDeviceSearchRVAdapter(this);
        recyclerView.setAdapter(mDiscoveryDeviceSearchRVAdapter);
        etSearch.setFocusable(true);
        etSearch.setFocusableInTouchMode(true);
        etSearch.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            showBaseProgressDlg();
            productName = etSearch.getText().toString();
            pageNo = 1;
            getProductList();
            return false;
        });

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
                pageNo = 1;
                getProductList();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ATAddDeviceScanHelper.REQUEST_CODE_CONFIG_WIFI:
                String productKey = data.getStringExtra("productKey");
                String deviceName = data.getStringExtra("deviceName");
                String token = data.getStringExtra("token");
                String iotId = data.getStringExtra("iotId");
                if (null == productKey) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("productKey", productKey);
                bundle.putString("deviceName", deviceName);
                bundle.putString("token", token);
                bundle.putString("iotId", iotId);
                startActivityForResult(new Intent(ATDiscoveryDeviceSearchActivity.this, ATDeviceBindActivity.class)
                        .putExtras(bundle), ATDeviceBindActivity.REQUEST_CODE);
                break;
            case ATDeviceBindActivity.REQUEST_CODE:
            case ATProductListActivity.REQUEST_CODE:
                String pk = data.getStringExtra("productKey");
                String iotId1 = data.getStringExtra("iotId");
                Intent bundle1 = new Intent();
                bundle1.putExtra("iotId", iotId1);
                bundle1.putExtra("productKey", pk);
                setResult(Activity.RESULT_OK, bundle1);
                finish();
            case ATAddDeviceScanHelper.REQUEST_CODE_SCAN:
                ATAddDeviceScanHelper.wiFiConfig(this, data);
                break;
            default:
                break;

        }
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_PRODUCTLIST:
                        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResult.getString("data"));
                        List<ATCategory> categoryList = gson.fromJson(jsonObject.getString("data"), new TypeToken<List<ATCategory>>() {
                        }.getType());
                        mDiscoveryDeviceSearchRVAdapter.setLists(categoryList, pageNo);
                        if (categoryList.size() == 0) {
                            smartRefreshLayout.setNoMoreData(true);
                        }
                        etSearch.setFocusable(true);
                        etSearch.setFocusableInTouchMode(true);
                        etSearch.requestFocus();
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