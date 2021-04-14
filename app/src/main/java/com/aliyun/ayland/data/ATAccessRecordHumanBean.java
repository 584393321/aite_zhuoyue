package com.aliyun.ayland.data;

public class ATAccessRecordHumanBean {
    private String eventId;
    private String scopeId;
    private String face;
    private String userName;
    private String userId;
    private String deviceName;
    private int accessType;
    private int itemId;
    private String iotId;
    private String createTime;
    private String eventTime;
    private int id;
    private String userType;
    private String address;
    /**
     * modelId : iot_entrance_event_model
     * eventTime : 1588239413758
     * mediaType : QRCODE
     * personCode : 100002
     */

    private String modelId;
    private String mediaType;
    private int personCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "AccessRecordHumanBean{" +
                "eventId='" + eventId + '\'' +
                ", scopeId='" + scopeId + '\'' +
                ", face='" + face + '\'' +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", deviceName='" + deviceName + '\'' +
                ", accessType='" + accessType + '\'' +
                ", itemId='" + itemId + '\'' +
                ", iotId='" + iotId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", id='" + id + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getPersonCode() {
        return personCode;
    }

    public void setPersonCode(int personCode) {
        this.personCode = personCode;
    }
}

