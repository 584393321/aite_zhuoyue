package com.aliyun.ayland.data;

import java.util.List;

public class ATFamilySecurityBean {
    /**
     * attributes : [{"attribute":"AlarmFrequencyLevel","value":"1"},{"attribute":"AlarmMode","value":"1"},{"attribute":"AlarmNotifyPlan","value":"[{\"DayOfWeek\":0,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":1,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":2,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":3,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":4,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":5,\"EndTime\":86399,\"BeginTime\":0},{\"DayOfWeek\":6,\"EndTime\":86399,\"BeginTime\":0}]"},{"attribute":"AlarmPromptSwitch","value":"1"},{"attribute":"AlarmSwitch","value":"1"},{"attribute":"CountDown","value":"{}"},{"attribute":"CountDownList","value":"{}"},{"attribute":"DayNightMode","value":"1"},{"attribute":"DeviceOwner","value":""},{"attribute":"DeviceTime","value":"{\"TZ\":\"GMT+08:00\",\"Time\":1568596844}"},{"attribute":"ImageFlipState","value":"0"},{"attribute":"IpcVersion","value":"MZ0100_X200_CN_SA00_20190611"},{"attribute":"LocalTimer","value":"[]"},{"attribute":"MicSwitch","value":"1"},{"attribute":"MotionDetectSensitivity","value":"5"},{"attribute":"NetworkInfo","value":"{\"IP\":\"\",\"MAC\":\"\"}"},{"attribute":"PushSwitch","value":"1"},{"attribute":"StorageRecordMode","value":"2"},{"attribute":"StorageRecordQuality","value":"0"},{"attribute":"StorageRemainCapacity","value":"0"},{"attribute":"StorageStatus","value":"0"},{"attribute":"StorageTotalCapacity","value":"0"},{"attribute":"StreamVideoQuality","value":"2"},{"attribute":"SubStreamVideoQuality","value":"1"},{"attribute":"WiFI_RSSI","value":"-127"},{"attribute":"_sys_device_mid","value":"example.demo.module-id"},{"attribute":"_sys_device_pid","value":"example.demo.partner-id"}]
     * categoryKey : Camera
     * deviceName : BxfDHTPOC1VfeBIffx0g
     * iotId : BxfDHTPOC1VfeBIffx0g000100
     * iotSpaceId : bd6c3e7e7c384d4cafcfad2fba450a95
     * nickName : 摄像头
     * productImage : http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1547618924375.png
     * productKey : a1Q8ZmSKd4f
     * productName : HI3518EV200_T1_200W_PTZ
     * status : 1
     */

    private String categoryKey;
    private String deviceName;
    private String iotId;
    private String iotSpaceId;
    private String nickName;
    private String productImage;
    private String productKey;
    private String productName;
    private int status;
    private List<AttributesBean> attributes;

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getIotSpaceId() {
        return iotSpaceId;
    }

    public void setIotSpaceId(String iotSpaceId) {
        this.iotSpaceId = iotSpaceId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    public static class AttributesBean {
        /**
         * attribute : AlarmFrequencyLevel
         * value : 1
         */

        private String attribute;
        private String value;

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}