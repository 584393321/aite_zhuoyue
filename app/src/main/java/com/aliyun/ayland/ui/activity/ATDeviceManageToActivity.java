package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceManageBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATDeviceManageRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATDeviceManageToActivity extends ATBaseActivity implements ATMainContract.View {
    public static final int REQUEST_CODE_CHANGE = 0x1001;
    private ATMainPresenter mPresenter;
    private ATDeviceManageRVAdapter mDeviceManageRVAdapter;
    private int type, position;
    private List<ATDeviceManageBean> mDeviceManageList;
    private Dialog dialog;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private ATHouseBean mATHouseBean;
    private TextView mTvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recycleview;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findDevices();
    }

    private void shareDevice() {
        if (mATHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getATLoginBean().getPersonCode());
        operator.put("hidType", "OPEN");
        JSONArray iotIdArr = new JSONArray();
        iotIdArr.add(mDeviceManageList.get(position).getIotId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spaceId", mATHouseBean.getIotSpaceId());
        jsonObject.put("rootSpaceId", mATHouseBean.getRootSpaceId());
        jsonObject.put("iotIdList", iotIdArr);
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_BINDDEVBUILDING, jsonObject);
    }

    private void unsharedDevice() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        if (mDeviceManageList.get(position).getSharedUsers() != null &&
                !ATGlobalApplication.getAccount().equals(mDeviceManageList.get(position).getSharedUsers().get(0).getUsername()))
            jsonObject.put("username", mDeviceManageList.get(position).getSharedUsers().get(0).getUsername());
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("iotId", mDeviceManageList.get(position).getIotId());
        mPresenter.request(ATConstants.Config.SERVER_URL_UNSHAREDDEVICE, jsonObject);
    }

    private void findDevices() {
        ATHouseBean houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        if (type == 2 && houseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("iotSpaceId", houseBean.getIotSpaceId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDDEVICES, jsonObject);
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_855px546px_sure_or_no, null, false);
        mTvTitle = view.findViewById(R.id.tv_title);
        TextView tvLeft = view.findViewById(R.id.tv_cancel);
        TextView tvRight = view.findViewById(R.id.tv_sure);
        tvLeft.setOnClickListener(v -> dialog.dismiss());
        tvRight.setOnClickListener(v -> {
            if (type == 1)
                shareDevice();
            else
                unsharedDevice();
        });
        dialog.setContentView(view);
    }

    private void init() {
        mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        type = getIntent().getIntExtra("type", 1);
        switch (type) {
            case 1:
                titleBar.setTitle(getString(R.string.at_unbind_device));
                break;
            case 2:
                titleBar.setTitle(getString(R.string.at_my_device));
                titleBar.setRightButtonText(getString(R.string.at_choose_share));
                titleBar.setRightClickListener(() -> {
                    if (getString(R.string.at_choose_share).equals(titleBar.getSendText())) {
                        mDeviceManageRVAdapter.setCheckable(true);
                        titleBar.setRightButtonText(getString(R.string.at_next));
                    } else {
                        if (mDeviceManageRVAdapter.getCheckSet().size() == 0) {
                            showToast(getString(R.string.at_please_choose_at_lease_one_device));
                        } else {
                            ArrayList<String> iotIdList = new ArrayList<>();
                            for (Integer integer : mDeviceManageRVAdapter.getCheckSet()) {
                                iotIdList.add(mDeviceManageList.get(integer).getIotId());
                            }
                            startActivityForResult(new Intent(this, ATDeviceManageMineToActivity.class)
                                    .putExtra("iotIdList", iotIdList), REQUEST_CODE_CHANGE);
                        }
                    }
                });
                break;
            case 3:
                titleBar.setTitle(getString(R.string.at_shared_device));
                break;
            case 4:
                titleBar.setTitle(getString(R.string.at_accepted_device));
                break;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceManageRVAdapter = new ATDeviceManageRVAdapter(this, type);
        recyclerView.setAdapter(mDeviceManageRVAdapter);
        mDeviceManageRVAdapter.setOnItemClickListener((view, position) -> {
            this.position = position;
            if (type == 1)
                mTvTitle.setText(getString(R.string.at_sure_unbind_));
            else
                mTvTitle.setText(getString(R.string.at_sure_to_unbind_));
            dialog.show();
        });
        initDialog();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_RESETDEVICE:
                        mDeviceManageList.remove(position);
                        mDeviceManageRVAdapter.remove(position);
                        mDeviceManageRVAdapter.notifyItemRemoved(position);
                        mDeviceManageRVAdapter.notifyItemRangeChanged(position, mDeviceManageList.size());
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_delete_success));
                        break;
                    case ATConstants.Config.SERVER_URL_BINDDEVBUILDING:
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_bind_success));
                        mDeviceManageList.remove(position);
                        mDeviceManageRVAdapter.remove(position);
                        mDeviceManageRVAdapter.notifyItemRemoved(position);
                        mDeviceManageRVAdapter.notifyItemRangeChanged(position, mDeviceManageList.size());
                        dialog.dismiss();
                        break;
                    case ATConstants.Config.SERVER_URL_UNSHAREDDEVICE:
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_unbind_success));
                        dialog.dismiss();
                        mDeviceManageList.remove(position);
                        mDeviceManageRVAdapter.remove(position);
                        break;
                    case ATConstants.Config.SERVER_URL_FINDDEVICES:
                        mDeviceManageList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATDeviceManageBean>>() {
                        }.getType());
                        mDeviceManageRVAdapter.setList(mDeviceManageList);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHANGE && resultCode == RESULT_OK)
            finish();
    }
}