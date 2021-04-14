package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATEventInteger2;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATLoginBean;
import com.aliyun.ayland.data.ATRoomBean1;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATEquipmentControlRVAdapter;
import com.aliyun.ayland.ui.adapter.ATEquipmentRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ATEquipmentActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATEquipmentControlRVAdapter mEquipmentControlRVAdapter;
    private ATEquipmentRVAdapter mEquipmentRVAdapter;
    private List<List<ATDeviceBean>> mDeviceBeanList = new ArrayList<>();
    private List<ATRoomBean1> mRoomNameList = new ArrayList<>();
    private boolean room, device;
    private ATHouseBean mHouseBean;
    private String mAllDeviceData;
    private int current = 0, specs = 0, current_position = 0, select_position = 0;
    private ATDeviceBean current_device;
    private JSONObject mAllDevice;
    private LinearLayout llEmpty;
    private RelativeLayout rlRight;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView rvControl, rvEquipment;
    private ATMyTitleBar titleBar;
    private TextView tvAddDevice;
    private Dialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_equipment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger2 eventInteger2) {
        if ("EquipmentActivity".equals(eventInteger2.getClazz())) {
            current_position = eventInteger2.getPosition();
            current_device = mDeviceBeanList.get(current).get(current_position);
            specs = eventInteger2.getSpecs();
            showBaseProgressDlg();
            control();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger eventInteger) {
        if ("EquipmentActivity".equals(eventInteger.getClazz())) {
            current = eventInteger.getPosition();
            mEquipmentRVAdapter.setLists(mDeviceBeanList.get(current), current);
            if (current == 0 && mDeviceBeanList.get(eventInteger.getPosition()).size() == 0) {
                llEmpty.setVisibility(View.VISIBLE);
            } else {
                llEmpty.setVisibility(View.GONE);
            }
        } else if ("EquipmentActivity1".equals(eventInteger.getClazz())) {
            startActivity(new Intent(this, ATManageRoomActivity.class).putExtra("allDevice", mAllDeviceData)
                    .putExtra("mRoomNameList", (Serializable) mRoomNameList).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvAddDevice = findViewById(R.id.tv_add_device);
        llEmpty = findViewById(R.id.ll_empty);
        rvControl = findViewById(R.id.rv_control);
        rvEquipment = findViewById(R.id.rv_equipment);
        rlRight = findViewById(R.id.rl_right);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void request() {
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        if (mHouseBean == null)
            return;
        findOrderRoom();
        getHouseDevice();
    }

    private void resetDevice() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("iotId", mDeviceBeanList.get(current).get(select_position).getIotId());
        mPresenter.request(ATConstants.Config.SERVER_URL_RESETDEVICE, jsonObject);
    }

    private void control() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONArray commands = new JSONArray();
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(current_device.getAttributes().get(0).getAttribute(), specs);
        command.put("data", data);
        command.put("type", "SET_PROPERTIES");
        commands.add(command);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("targetId", current_device.getIotId());
        jsonObject.put("operator", operator);
        jsonObject.put("commands", commands);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_CONTROL, jsonObject);
    }

    private void findOrderRoom() {
        room = false;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spaceId", mHouseBean.getIotSpaceId());
        jsonObject.put("villageId", mHouseBean.getVillageId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        ATLoginBean loginBean = ATGlobalApplication.getATLoginBean();
        jsonObject.put("personCode", loginBean.getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDORDERROOM, jsonObject);
    }

    private void getHouseDevice() {
        device = false;
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("iotSpaceId", mHouseBean.getIotSpaceId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("username", ATGlobalApplication.getAccount());
        mPresenter.request(ATConstants.Config.SERVER_URL_HOUSEDEVICE, jsonObject);
    }


    private void init() {
        mEquipmentControlRVAdapter = new ATEquipmentControlRVAdapter(this);
        rvControl.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rvControl.setAdapter(mEquipmentControlRVAdapter);
        mEquipmentRVAdapter = new ATEquipmentRVAdapter(this);
        rvEquipment.setLayoutManager(new GridLayoutManager(this, 2));
        rvEquipment.setAdapter(mEquipmentRVAdapter);
        mEquipmentRVAdapter.setOnRVClickListener((view, position) -> {
            if (mAllDevice == null) {
                request();
                return;
            }
            Intent intent = new Intent();
            if (mAllDevice.containsKey(mRoomNameList.get(current).getIotSpaceId())) {
                Gson gson = new Gson();
                List<ATDeviceBean> deviceBeanList = gson.fromJson(mAllDevice.getString(mRoomNameList.get(current).getIotSpaceId()), new TypeToken<List<ATDeviceBean>>() {
                }.getType());
                intent.putExtra("deviceBeanList", (Serializable) deviceBeanList);
            }
            intent.putExtra("allDeviceData", mAllDeviceData);
            intent.putExtra("roombean", mRoomNameList.get(current));
            intent.setClass(ATEquipmentActivity.this, ATEditRoomActivity.class);
            startActivity(intent);
        });
        mEquipmentRVAdapter.addItemLongClickListener(position -> {
            select_position = position;
            dialog.show();
        });

        rlRight.setOnClickListener(view ->
                startActivity(new Intent(this, ATManageRoomActivity.class)
                        .putExtra("allDevice", mAllDeviceData)
                        .putExtra("mRoomNameList", (Serializable) mRoomNameList)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            request();
        });
//        titlebar.setTitleBarClickBackListener(new TitleBarClickBackListener() {
//            @Override
//            public void clickBack() {
//                LoginBusiness.logout(new ILogoutCallback() {
//                    @Override
//                    public void onLogoutSuccess() {
//                        EMClient.getInstance().logout(false, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                ATApplication.setAllDeviceData("");
//                                stopService(new Intent(EquipmentActivity.this, SocketServer.class));
//                                LoginBusiness.login(new ILoginCallback() {
//                                    @Override
//                                    public void onLoginSuccess() {
//                                    }
//
//                                    @Override
//                                    public void onLoginFailed(int code, String error) {
//                                    }
//                                });
//                                finish();
//                            }
//
//                            @Override
//                            public void onProgress(int progress, String status) {
//
//                            }
//
//                            @Override
//                            public void onError(int code, String error) {
//
//                            }
//                        });
//                    }
//                    @Override
//                    public void onLogoutFailed(int code, String error) {
//                    }
//                });
//            }
//        });
        titleBar.setRightBtTextImage(R.drawable.zhihui_ico_tianjiakuaijie);
        titleBar.setRightClickListener(() -> {
//            mHomePopup.showPopupWindow(titlebar.getRightButton())
            startActivity(new Intent(this, ATDiscoveryDeviceActivity.class));
        });
        tvAddDevice.setOnClickListener(view -> startActivity(new Intent(this, ATDiscoveryDeviceActivity.class)));

        initDialog();
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_normal, null, false);
        ((TextView) view.findViewById(R.id.tv_title)).setText(R.string.at_whether_to_reset_device);
        view.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.tv_sure).setOnClickListener(v -> resetDevice());
        dialog.setContentView(view);
    }

    private void requestComplete() {
        if (room && device) {
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
            mDeviceBeanList.clear();
            List<ATDeviceBean> deviceBeanList;
            ATGlobalApplication.setAllDeviceData(mAllDeviceData);
            mAllDevice = JSON.parseObject(mAllDeviceData);
            for (int i = 0; i < mRoomNameList.size(); i++) {
                if (mAllDevice.containsKey(mRoomNameList.get(i).getIotSpaceId())) {
                    deviceBeanList = gson.fromJson(mAllDevice.getString(mRoomNameList.get(i).getIotSpaceId()), new TypeToken<List<ATDeviceBean>>() {
                    }.getType());
                } else {
                    deviceBeanList = new ArrayList<>();
                }
                mDeviceBeanList.add(deviceBeanList);
            }
            if (mDeviceBeanList.size() <= current)
                current = 0;
            mEquipmentRVAdapter.setLists(mDeviceBeanList.get(current), current);
            if (current == 0 && mDeviceBeanList.get(current).size() == 0) {
                llEmpty.setVisibility(View.VISIBLE);
            } else {
                llEmpty.setVisibility(View.GONE);
            }
            mEquipmentControlRVAdapter.setSelectItem(current);
            mEquipmentControlRVAdapter.setLists(mRoomNameList);
            room = false;
            device = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBaseProgressDlg();
        request();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CONTROL:
                        closeBaseProgressDlg();
                        if (result.contains("success")) {
                            showToast(getString(R.string.at_operate_success));
                            mDeviceBeanList.get(current).get(current_position).getAttributes().get(0).setValue(String.valueOf(specs));
                            mEquipmentRVAdapter.setCheck(mDeviceBeanList.get(current), current_position);
                        } else {
                            request();
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_RESETDEVICE:
                        request();
                        dialog.dismiss();
                        showToast("重置成功");
                        break;
                    case ATConstants.Config.SERVER_URL_HOUSEDEVICE:
                        mAllDeviceData = jsonResult.getString("data");
                        device = true;
                        requestComplete();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDORDERROOM:
                        mRoomNameList.clear();
                        ATRoomBean1 roomBean = new ATRoomBean1();
                        roomBean.setType("all");
                        roomBean.setIotSpaceId("all");
                        roomBean.setName("全部设备");
                        mRoomNameList.add(roomBean);
                        List<ATRoomBean1> roomNameList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATRoomBean1>>() {
                        }.getType());
                        if (roomNameList.size() > 0)
                            mRoomNameList.addAll(roomNameList);
                        room = true;
                        requestComplete();
                        break;
                }
            } else {
                closeBaseProgressDlg();
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}