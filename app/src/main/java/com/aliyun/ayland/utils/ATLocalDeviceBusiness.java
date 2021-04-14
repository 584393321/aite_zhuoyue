package com.aliyun.ayland.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.aliyun.ayland.data.ATLocalDevice;
import com.aliyun.ayland.interfaces.ATOnDeviceFilterCompletedListener;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 本地设备过滤逻辑的封装
 *
 * @author guikong on 18/4/19.
 */
public class ATLocalDeviceBusiness {
    private static final String TAG = "LocalDeviceBusiness";

    private List<ATLocalDevice> discoveredDevices;
    private List<ATLocalDevice> filteredDevices;

    private ATOnDeviceFilterCompletedListener listener;

    public ATLocalDeviceBusiness(ATOnDeviceFilterCompletedListener listener) {
        this.listener = listener;
        discoveredDevices = new LinkedList<>();
        filteredDevices = new LinkedList<>();
    }

    @SuppressWarnings("unused")
    public List<ATLocalDevice> getFilteredDevices() {
        return new ArrayList<>(filteredDevices);
    }

    @SuppressWarnings("unused")
    public List<ATLocalDevice> getDiscoveredDevices() {
        return new ArrayList<>(discoveredDevices);
    }

    public void reset() {
        discoveredDevices.clear();
        filteredDevices.clear();
    }

    public void add(List<ATLocalDevice> localDevices) {
        if (null == localDevices || localDevices.isEmpty()) {
            return;
        }

        // remove duplicated data
        List<ATLocalDevice> availableDevice = new ArrayList<>(localDevices.size());

        for (ATLocalDevice localDevice : localDevices) {
            boolean invalid = false;

            for (ATLocalDevice discoveredDevice : discoveredDevices) {
                if (TextUtils.equals(localDevice.deviceName, discoveredDevice.deviceName)
                        && TextUtils.equals(localDevice.productKey, discoveredDevice.productKey)) {
                    invalid = true;
                    break;
                }
            }

            if (!invalid) {
                availableDevice.add(localDevice);
            }
        }

        if (availableDevice.isEmpty()) {
            return;
        }

        // cache devices
        discoveredDevices.addAll(availableDevice);

        // do filter
        filterDevice(availableDevice);
    }

    /**
     * 过滤本地设备
     *
     * @param localDevices 本地设备，未区分已配网/待配网
     */
    private void filterDevice(List<ATLocalDevice> localDevices) {
        discoveredDevices.addAll(localDevices);

        List<ATLocalDevice> localDevicesNeedBind = new ArrayList<>();
        List<ATLocalDevice> localDevicesNeedEnroll = new ArrayList<>();

        for (ATLocalDevice localDevice : localDevices) {
            if (ATLocalDevice.NEED_BIND.equalsIgnoreCase(localDevice.deviceStatus)) {
                localDevicesNeedBind.add(localDevice);
            } else if (ATLocalDevice.NEED_CONNECT.equalsIgnoreCase(localDevice.deviceStatus)) {
                localDevicesNeedEnroll.add(localDevice);
            }
        }

        if (!localDevicesNeedEnroll.isEmpty()) {
            filterLocalDeviceNeedEnroll(localDevicesNeedEnroll);
        }

        if (!localDevicesNeedBind.isEmpty()) {
            filterLocalDeviceNeedBind(localDevices);
        }
    }

    /**
     * 过滤待配网设备
     *
     * @param localDevices 未配网的设备
     */
    private void filterLocalDeviceNeedEnroll(List<ATLocalDevice> localDevices) {
        filteredDevices.addAll(localDevices);

        if (null != listener) {
            try {
                listener.onDeviceFilterCompleted(localDevices);
            } catch (Exception e) {
                ALog.e(TAG, "exception happens when call onDeviceFilterCompleted:");
                e.printStackTrace();
            }
        }
    }

    /**
     * 过滤已配网设备
     *
     * @param localDevices 已配网的设备
     */
    private void filterLocalDeviceNeedBind(final List<ATLocalDevice> localDevices) {
        List<Map<String, String>> devices = new ArrayList<>(localDevices.size());
        for (ATLocalDevice localDevice : localDevices) {
            Map<String, String> device = new HashMap<>(2);
            device.put("productKey", localDevice.productKey);
            device.put("deviceName", localDevice.deviceName);
            devices.add(device);
        }

        IoTRequest request = new IoTRequestBuilder()
                .setPath("/awss/enrollee/product/filter")
                .setApiVersion("1.0.2")
                .addParam("iotDevices", devices)
                .setAuthType("iotAuth")
                .build();

        new IoTAPIClientFactory().getClient().send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, final Exception e) {
                // nothing to do
            }

            @Override
            public void onResponse(final IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    return;
                }

                if (!(ioTResponse.getData() instanceof JSONArray)) {
                    return;
                }

                final List<ATLocalDevice> availableDevices = new ArrayList<>();
                List<ATLocalDevice> filteredLocalDevices = new ArrayList<>();
                JSONArray items = (JSONArray) ioTResponse.getData();

                if (null != items
                        && items.length() > 0) {
                    String jsonStr = items.toString();
                    filteredLocalDevices.addAll(JSON.parseArray(jsonStr, ATLocalDevice.class));
                }

                //append token & addDeviceFrom & deviceStatus
                for (ATLocalDevice localDevice : localDevices) {
                    boolean available = false;
                    for (ATLocalDevice filteredLocalDevice : filteredLocalDevices) {
                        if (TextUtils.equals(filteredLocalDevice.productKey, localDevice.productKey)
                                && TextUtils.equals(filteredLocalDevice.deviceName, localDevice.deviceName)) {
                            available = true;
                            localDevice.productName = filteredLocalDevice.productName;
                            break;
                        }
                    }
                    if (available) {
                        availableDevices.add(localDevice);
                    }
                }

                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        filteredDevices.addAll(availableDevices);
                        if (null != listener) {
                            try {
                                listener.onDeviceFilterCompleted(availableDevices);
                            } catch (Exception e) {
                                ALog.e(TAG, "exception happens when call onDeviceFilterCompleted:");
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}