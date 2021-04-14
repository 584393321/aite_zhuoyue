package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATCarBean;
import com.aliyun.ayland.data.ATCarListBean;
import com.aliyun.ayland.data.ATDeviceAccessBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATParkNumberBean;
import com.aliyun.ayland.data.db.ATParkNumberDao;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATParkCarRVAdapter;
import com.aliyun.ayland.ui.adapter.ATParkRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ATLinkageAccessActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int REQUEST_CODE_VEHICLE_ACCESS = 0x1001;
    private ATMainPresenter mPresenter;
    private List<ATDeviceAccessBean> mDeviceAccessBeans = new ArrayList<>();
    private ATParkCarRVAdapter mParkCarRVAdapter;
    private List<ATCarListBean> mCarList = new ArrayList<>();
    private int car_position = -1, device_position = 0;
    private ATParkNumberDao mATParkNumberDao;
    private List<ATParkNumberBean> mParkNumberList = new ArrayList<>();
    //    private BottomPlatePopup mBottomPlatePopup;
//    private SlideFromBottomInputPopup mSlideFromBottomInputPopup;
    private ATParkRVAdapter mParkRVAdapter;
    private String bizInfo, deviceId;
    private List<String> bizTypeList = new ArrayList<>();
    private boolean replace;
    private String dataType;
    private JSONObject params = new JSONObject();
    private LinearLayout llVehicle;
    private ATMyTitleBar titleBar;
    private RecyclerView rvVehicleName, rvPark;


    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_access;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        llVehicle = findViewById(R.id.ll_vehicle);
        rvVehicleName = findViewById(R.id.rv_vehicle_name);
        rvPark = findViewById(R.id.rv_park);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getDeviceList();
        if ("106".equals(dataType))
            list();
    }

    private void getDeviceList() {
        ATHouseBean houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        if (houseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceType", dataType);
        jsonObject.put("villageCode", houseBean.getVillageId());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETDEVICELIST, jsonObject);
    }

    private void list() {
        ATHouseBean mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        if (mATHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
//        jsonObject.put("status", 1);
        jsonObject.put("pageNo", 1);
        jsonObject.put("pageSize", 50);
        mPresenter.request(ATConstants.Config.SERVER_URL_CARPASSLIST, jsonObject);
    }

    private void init() {
        titleBar.setRightButtonText(getString(R.string.at_next));
        replace = getIntent().getBooleanExtra("replace", false);
        if (replace)
            params = JSONObject.parseObject(getIntent().getStringExtra("params"));
        dataType = getIntent().getStringExtra("dataType");
        switch (dataType) {
            case "106":
                llVehicle.setVisibility(View.VISIBLE);
                titleBar.setTitle(getString(R.string.at_vehicle_access));
                mATParkNumberDao = new ATParkNumberDao(this);
                mParkNumberList = mATParkNumberDao.getAll();
                if (mParkNumberList.size() == 0) {
                    initLitepal();
                }
//                mBottomPlatePopup = new BottomPlatePopup(this, "VisitorAppointActivity1");
//                mBottomPlatePopup.setOnItemClickListener(new BottomPlatePopup.OnPopupItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        String park_number = mParkNumberList.get(position).getPark_number();
//                        updateLitepal(park_number);
//                        tvPlateNumber.setText(park_number);
//                        tvPlateNumbers.setText(park_number);
//                        tvPlateNumbers.setTextColor(getResources().getColor(R.color._333333));
//                        observableScrollView.smoothScrollTo(0, 1000);
//                        mSlideFromBottomInputPopup.showPopupWindow();
//                    }
//                });
//                mSlideFromBottomInputPopup = new SlideFromBottomInputPopup(this, "VisitorAppointActivity");
//                mSlideFromBottomInputPopup.setOnItemClickListener(new SlideFromBottomInputPopup.OnPopupItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, String text) {
//                        String parkNumber = tvPlateNumbers.getText().toString();
//                        if (TextUtils.isEmpty(text)) {
//                            if (parkNumber.length() > 1) {
//                                tvPlateNumbers.setText(parkNumber.substring(0, parkNumber.length() - 1));
//                            } else {
//                                tvPlateNumbers.setText(getString(R.string.input_plate_number));
//                                tvPlateNumbers.setTextColor(getResources().getColor(R.color._666666));
//                                mSlideFromBottomInputPopup.dismiss();
//                                observableScrollView.smoothScrollTo(0, 1000);
//                                mBottomPlatePopup.showPopupWindow();
//                            }
//                        } else {
//                            if (parkNumber.length() == 1)
//                                parkNumber += "-";
//                            tvPlateNumbers.setText(parkNumber + text);
//                        }
//                    }
//                });

//                tvPlateNumbers.setOnClickListener(view -> {
//                    observableScrollView.smoothScrollTo(0, 1000);
//                    if (getString(R.string.input_plate_number).equals(tvPlateNumbers.getText().toString())) {
//                        mBottomPlatePopup.showPopupWindow();
//                    } else {
//                        mSlideFromBottomInputPopup.showPopupWindow();
//                    }
//                });
//                rlPlateNumbers.setOnClickListener(view -> mBottomPlatePopup.showPopupWindow());

                rvVehicleName.setLayoutManager(new LinearLayoutManager(this));
                mParkCarRVAdapter = new ATParkCarRVAdapter(this);
                rvVehicleName.setAdapter(mParkCarRVAdapter);
                mParkCarRVAdapter.setOnItemClickListener((view, position) -> {
                    car_position = position;
//                    checkBox.setChecked(false);
                });
                break;
            case "108":
                titleBar.setTitle(getString(R.string.at_person_access));
                break;
            case "109":
                break;
        }

        rvPark.setLayoutManager(new LinearLayoutManager(this));
        mParkRVAdapter = new ATParkRVAdapter(this);
        rvPark.setAdapter(mParkRVAdapter);
        mParkRVAdapter.setOnItemClickListener((view, position) -> device_position = position);
//        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b) {
//                mParkCarRVAdapter.setSelectPosition(-1);
//            }
//        });

        titleBar.setRightClickListener(() -> {
            if (mDeviceAccessBeans.size() == 0) {
                return;
            }
            deviceId = mDeviceAccessBeans.get(device_position).getIotId();
            bizInfo = mDeviceAccessBeans.get(device_position).getBizInfo();
            switch (dataType) {
                case "106":
//                    if (checkBox.isChecked()) {
//                        bizInfo = tvPlateNumbers.getText().toString();
//                    } else {
                    if (mCarList.size() > car_position) {
                        bizInfo = mCarList.get(car_position).getPlateNumber();
                    } else {
                        bizInfo = "";
                    }
//                    }
                    if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(bizInfo)) {
                        showToast(getString(R.string.at_choose_vehicle_park_device));
                        return;
                    }
                    break;
                case "108":

                    break;
            }
            params.put("deviceId", deviceId);
            params.put("bizInfo", bizInfo);
            params.put("hid", ATGlobalApplication.getHid());
            params.put("hidType", "OPEN");
            String content;
            if (replace) {
                content = bizInfo + " " + getIntent().getStringExtra("content").split(" ")[1];
            } else {
                content = bizInfo;
            }
            startActivityForResult(getIntent()
                    .putExtra("name", mDeviceAccessBeans.get(device_position).getName())
                    .putExtra("content", content)
                    .putExtra("bizType", mDeviceAccessBeans.get(device_position).getBizType())
                    .putExtra("params", params.toJSONString())
                    .setClass(this, ATLinkageAccessBizTypeActivity.class), REQUEST_CODE_VEHICLE_ACCESS);
        });
    }

    private void updateLitepal(String park_number) {
        ATParkNumberBean parkNumberBean = mATParkNumberDao.getByParkNumber(park_number);
        parkNumberBean.setCreate_time(new Date().getTime());
        mATParkNumberDao.update(parkNumberBean);
    }

    private void initLitepal() {
        List<String> s = new ArrayList<>();
        s.add("新");
        s.add("宁");
        s.add("青");
        s.add("甘");
        s.add("陕");
        s.add("藏");
        s.add("云");
        s.add("贵");
        s.add("川");
        s.add("渝");
        s.add("琼");
        s.add("桂");
        s.add("晋");
        s.add("蒙");
        s.add("鄂");
        s.add("豫");
        s.add("鲁");
        s.add("冀");
        s.add("闽");
        s.add("皖");
        s.add("浙");
        s.add("苏");
        s.add("沪");
        s.add("黑");
        s.add("吉");
        s.add("辽");
        s.add("津");
        s.add("京");
        s.add("湘");
        s.add("粤");
        s.add("赣");
        // 添加用户数据
        for (int i = 0; i < s.size(); i++) {
            ATParkNumberBean parkNumberBean = new ATParkNumberBean();
            parkNumberBean.setCreate_time(new Date().getTime() + i * 10);
            parkNumberBean.setPark_number(s.get(i));
            mATParkNumberDao.add(parkNumberBean);
        }
        mParkNumberList = mATParkNumberDao.getAll();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETDEVICELIST:
                        mDeviceAccessBeans = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATDeviceAccessBean>>() {
                        }.getType());
                        if (mDeviceAccessBeans.size() > 0) {
                            if (replace) {
                                String deviceId = params.getString("deviceId");
                                for (int i = 0; i < mDeviceAccessBeans.size(); i++) {
                                    if (deviceId.equals(mDeviceAccessBeans.get(i).getIotId())) {
                                        device_position = i;
                                        break;
                                    }
                                }
                            }
                            mParkRVAdapter.setList(mDeviceAccessBeans, device_position);
                            bizTypeList = mDeviceAccessBeans.get(device_position).getBizTypeList();
                            deviceId = mDeviceAccessBeans.get(device_position).getIotId();
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_CARPASSLIST:
                        mCarList = gson.fromJson(jsonResult.getJSONObject("data").getString("content"), new TypeToken<List<ATCarListBean>>() {
                        }.getType());
                        int licence_position = 0;
                        if (replace) {
                            bizInfo = getIntent().getStringExtra("content").split(" ")[0];
                            for (int i = 0; i < mCarList.size(); i++) {
                                if (bizInfo.equals(mCarList.get(i).getPlateNumber())) {
                                    licence_position = i;
                                    car_position = 0;
                                    break;
                                }
                            }
                        } else {
                            car_position = 0;
                            if (mCarList.size() > 0) {
                                bizInfo = mCarList.get(car_position).getPlateNumber();
                            }
                        }

                        mParkCarRVAdapter.setList(mCarList, licence_position);
                        break;
//                    case ATConstants.Config.SERVER_URL_GETUSERLICENCELIST:
//                        mCarList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATCarBean>>() {
//                        }.getType());
//                        int licence_position = 0;
//                        if (replace) {
//                            bizInfo = getIntent().getStringExtra("content").split(" ")[0];
//                            for (int i = 0; i < mCarList.size(); i++) {
//                                if (bizInfo.equals(mCarList.get(i).getLicence())) {
//                                    licence_position = i;
//                                    car_position = 0;
//                                    break;
//                                }
//                            }
//                        } else {
//                            car_position = 0;
//                            if (mCarList.size() > 0) {
//                                bizInfo = mCarList.get(car_position).getLicence();
//                            }
//                        }
//
//                        mParkCarRVAdapter.setList(mCarList, licence_position);
//                        break;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_VEHICLE_ACCESS) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}