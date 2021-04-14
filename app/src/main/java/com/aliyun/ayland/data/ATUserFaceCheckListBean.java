package com.aliyun.ayland.data;


import java.util.List;

public class ATUserFaceCheckListBean {

    /**
     * checkStatus : 1
     * deviceList : [{"faceStatus":0,"deviceAddress":"旭辉总部36号楼2期3号商铺","villageName":"旭辉总部36号楼","villageId":100003,"deviceName":"江阴旭辉公元东门门口机","deviceId":"DlBTAoMYd2jqkqWxUxs1000100"},{"deviceType":"AIBOX","iotId":"mJP3SVxTJFIkYKphNv1D000100","faceStatus":2,"createTime":1575528158000,"groupId":"100003","id":2,"deviceName":"ai服务器网关","deviceId":"mJP3SVxTJFIkYKphNv1D000100","villageId":100003}]
     * villageName : 旭辉总部36号楼
     */

    private int checkStatus;
    private String villageName;
    private boolean fromFamily;
    private List<ATUserFaceCheckBean> deviceList;

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public List<ATUserFaceCheckBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<ATUserFaceCheckBean> deviceList) {
        this.deviceList = deviceList;
    }

    public boolean isFromFamily() {
        return fromFamily;
    }

    public void setFromFamily(boolean fromFamily) {
        this.fromFamily = fromFamily;
    }
}