package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceTca;
import com.aliyun.ayland.data.ATDeviceTslDataType;
import com.aliyun.ayland.data.ATDeviceTslEvents;
import com.aliyun.ayland.data.ATDeviceTslOutputDataType;
import com.aliyun.ayland.data.ATDeviceTslProperties;
import com.aliyun.ayland.data.ATDeviceTslServices;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.interfaces.ATOnRVItemClickListener;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageStatusRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.activity.ATLinkageAddActivity.REQUEST_CODE_ADD_CONDITION;

public class ATLinkageStatusActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private String productKey, tagKey, iotId, status_name;
    private boolean tca, tsl;
    private List<ATDeviceTslServices> mDeviceTslServices = new ArrayList<>();
    private List<ATDeviceTslProperties> mDeviceTslProperties = new ArrayList<>();
    private List<ATDeviceTslEvents> mDeviceTslEvents = new ArrayList<>();
    private List<ATDeviceTca> mDeviceTcaList = new ArrayList<>();
    private ATLinkageStatusRVAdapter mLinkageStatusRVAdapter;
    private RecyclerView rvTca;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_status;
    }

    @Override
    protected void findView() {
        rvTca = findViewById(R.id.rv_tca);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        showBaseProgressDlg();
        getTsl();
        getProductTcaGet();
    }

    private void getTsl() {
        tsl = false;
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("operator", operator);
        jsonObject.put("iotId", iotId);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETTSL, jsonObject);
    }

    private void getProductTcaGet() {
        tca = false;
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject tagInfo = new JSONObject();
        tagInfo.put("abilityType", "");
        tagInfo.put("tagValue", "ON");
        tagInfo.put("productKey", productKey);
        tagInfo.put("tagKey", tagKey);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("operator", operator);
        jsonObject.put("tagInfo", tagInfo);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETPRODUCTTCAGET, jsonObject);
    }

    private void init() {
        iotId = getIntent().getStringExtra("iotId");
        productKey = getIntent().getStringExtra("productKey");
        String[] tagKeyArr = getResources().getStringArray(R.array.tagKey);
        tagKey = tagKeyArr[getIntent().getIntExtra("flowType", 1) - 1];

        JSONObject params = new JSONObject();
        params.put("iotId", iotId);

        mLinkageStatusRVAdapter = new ATLinkageStatusRVAdapter(this);
        rvTca.setLayoutManager(new LinearLayoutManager(this));
        rvTca.setAdapter(mLinkageStatusRVAdapter);

        mLinkageStatusRVAdapter.setOnRVClickListener(new ATOnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (mDeviceTcaList.get(position).getAbilityType()) {
                    case "PROPERTY":
                        for (ATDeviceTslProperties deviceTslProperty : mDeviceTslProperties) {
                            if (mDeviceTcaList.get(position).getIdentifier().equals(deviceTslProperty.getIdentifier())) {
                                String uri = tagKey.toLowerCase();
                                switch (tagKey) {
                                    case "TRIGGER":
                                    case "CONDITION":
                                        params.put("propertyName", deviceTslProperty.getIdentifier());
                                        uri += "/device/property";
                                        break;
                                    case "ACTION":
                                        uri += "/device/setProperty";
                                        JSONObject propertyItems = new JSONObject();
                                        JSONObject propertyNamesItems = new JSONObject();
                                        JSONObject identifier = new JSONObject();
                                        identifier.put("abilityName", deviceTslProperty.getName());
                                        propertyNamesItems.put(deviceTslProperty.getIdentifier(), identifier);
                                        params.put("propertyItems", propertyItems);
                                        params.put("propertyNamesItems", propertyNamesItems);
                                        break;
                                }
                                startActivityForResult(getIntent()
                                        .putExtra("params", params.toJSONString())
                                        .putExtra("dataType", deviceTslProperty.getDataType().toJSONString())
                                        .putExtra("uri", uri)
                                        .putExtra("content", deviceTslProperty.getName())
                                        .setClass(ATLinkageStatusActivity.this, ATLinkageStatusChoiseActivity.class), REQUEST_CODE_ADD_CONDITION);
                                return;
                            }
                        }
                        break;
                    case "EVENT":
                        for (ATDeviceTslEvents deviceTslEvent : mDeviceTslEvents) {
                            if (mDeviceTcaList.get(position).getIdentifier().equals(deviceTslEvent.getIdentifier())) {
                                ArrayList<ATDeviceTslOutputDataType> deviceTslOutputDataType = gson.fromJson(deviceTslEvent.getOutputData().toJSONString(), new TypeToken<ArrayList<ATDeviceTslOutputDataType>>() {
                                }.getType());
                                String uri = tagKey.toLowerCase() + "/device/event";
                                params.put("eventCode", deviceTslEvent.getIdentifier());
                                startActivityForResult(getIntent()
                                        .putParcelableArrayListExtra("deviceTslOutputDataType", deviceTslOutputDataType)
                                        .putExtra("params", params.toJSONString())
                                        .putExtra("uri", uri)
                                        .putExtra("content", deviceTslEvent.getName())
                                        .setClass(ATLinkageStatusActivity.this, ATLinkageStatusToActivity.class), REQUEST_CODE_ADD_CONDITION);
                                return;
                            }
                        }

                        break;
                    case "SERVICE":
                        for (ATDeviceTslServices deviceTslService : mDeviceTslServices) {
                            if (mDeviceTcaList.get(position).getIdentifier().equals(deviceTslService.getIdentifier())) {
                                ATDeviceTslDataType deviceTslDataType = gson.fromJson(deviceTslService.getOutputData().toJSONString(), ATDeviceTslDataType.class);

                                return;
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETPRODUCTTCAGET:
                        mDeviceTcaList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATDeviceTca>>() {
                        }.getType());
                        tca = true;
                        requestComplete();
                        break;
                    case ATConstants.Config.SERVER_URL_GETTSL:
                        JSONObject mAllDevice = JSON.parseObject(jsonResult.getString("data"));
                        mDeviceTslServices = gson.fromJson(mAllDevice.getString("services"), new TypeToken<List<ATDeviceTslServices>>() {
                        }.getType());
                        mDeviceTslProperties = gson.fromJson(mAllDevice.getString("properties"), new TypeToken<List<ATDeviceTslProperties>>() {
                        }.getType());
                        mDeviceTslEvents = gson.fromJson(mAllDevice.getString("events"), new TypeToken<List<ATDeviceTslEvents>>() {
                        }.getType());
                        tsl = true;
                        requestComplete();
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestComplete() {
        if (tca && tsl) {
            closeBaseProgressDlg();
            mLinkageStatusRVAdapter.setLists(mDeviceTcaList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_CONDITION) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}