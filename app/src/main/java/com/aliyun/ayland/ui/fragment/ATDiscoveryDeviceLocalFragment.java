package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.IDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATLocalDevice;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATDiscoveryDeviceLocalRvAdapter;
import com.aliyun.ayland.utils.ATLocalDeviceBusiness;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATDiscoveryDeviceLocalFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainContract.Presenter mPresenter;
    private ATLocalDeviceBusiness localDeviceBusiness;
    private ATDiscoveryDeviceLocalRvAdapter mDiscoveryDeviceLocalRvAdapter;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_discovery_device_local;
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDiscoveryDeviceLocalRvAdapter = new ATDiscoveryDeviceLocalRvAdapter();
        recyclerView.setAdapter(mDiscoveryDeviceLocalRvAdapter);

        localDeviceBusiness = new ATLocalDeviceBusiness((localDevices) -> {
            for (ATLocalDevice localDevice : localDevices) {
                mDiscoveryDeviceLocalRvAdapter.addLocalDevice(localDevice);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        localDeviceBusiness.reset();
        mDiscoveryDeviceLocalRvAdapter.clearLocalDevices();
        LocalDeviceMgr.getInstance().startDiscovery(getActivity(), new IDiscoveryListener() {
            @Override
            public void onLocalDeviceFound(DeviceInfo deviceInfo) {
                Log.e("weadd: ", deviceInfo.deviceName+"---1");
                ATLocalDevice localDevice = new ATLocalDevice();
                localDevice.deviceStatus = ATLocalDevice.NEED_BIND;
                localDevice.productKey = deviceInfo.productKey;
                localDevice.deviceName = deviceInfo.deviceName;
                localDevice.token = deviceInfo.token;
                localDevice.addDeviceFrom = deviceInfo.addDeviceFrom;

                List<ATLocalDevice> localDevices = new ArrayList<>();
                localDevices.add(localDevice);
                localDeviceBusiness.add(localDevices);
            }

            @Override
            public void onEnrolleeDeviceFound(List<DeviceInfo> list) {
                //要配网
                for (DeviceInfo deviceInfo : list) {
                    ATLocalDevice localDevice = new ATLocalDevice();
                    localDevice.deviceStatus = ATLocalDevice.NEED_BIND;
                    localDevice.productKey = deviceInfo.productKey;
                    localDevice.deviceName = deviceInfo.deviceName;
                    localDevice.addDeviceFrom = deviceInfo.addDeviceFrom;

                    Log.e("weadd: ", deviceInfo.deviceName+"---2");
                    List<ATLocalDevice> localDevices = new ArrayList<>();
                    localDevices.add(localDevice);
                    localDeviceBusiness.add(localDevices);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalDeviceMgr.getInstance().stopDiscovery();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CATEGORY:

                        break;
                    case ATConstants.Config.SERVER_URL_PRODUCTLIST:

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