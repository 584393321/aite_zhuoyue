package com.aliyun.ayland.data;

public class ATVisitorReservateBean {
    /**
     * reservationStartTime : 2019-08-22 09:40
     * visitorName : 陈
     * actualStartTime :
     * createTime : 2019-08-22 09:40
     * actualEndTime :
     * reservationEndTime : 2019-08-23 09:40
     * visitorTel : 13333333333
     * id : 83
     * visitorStatus : -1
     * hasCar : -1
     * intermediary : -1
     */

    private String address;
    private String reservationStartTime;
    private String visitorName;
    private String actualStartTime;
    private String createTime;
    private String actualEndTime;
    private String reservationEndTime;
    private String visitorTel;
    private String visitorHouse;
    private String id;
    private String carNumber;
    private String createPerson;
    private String inviterName;
    private String inviterPhone;
    private String qrcodeUrl;
    private String ownerName;
    private int visitorStatus;
    private int hasCar;
    private int intermediary;
    private int inviterCode;
    private boolean ifSuccess;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVisitorHouse() {
        return visitorHouse;
    }

    public void setVisitorHouse(String visitorHouse) {
        this.visitorHouse = visitorHouse;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviterPhone() {
        return inviterPhone;
    }

    public void setInviterPhone(String inviterPhone) {
        this.inviterPhone = inviterPhone;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public int getInviterCode() {
        return inviterCode;
    }

    public void setInviterCode(int inviterCode) {
        this.inviterCode = inviterCode;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getReservationStartTime() {
        return reservationStartTime;
    }

    public void setReservationStartTime(String reservationStartTime) {
        this.reservationStartTime = reservationStartTime;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(String actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(String reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    public String getVisitorTel() {
        return visitorTel;
    }

    public void setVisitorTel(String visitorTel) {
        this.visitorTel = visitorTel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVisitorStatus() {
        return visitorStatus;
    }

    public void setVisitorStatus(int visitorStatus) {
        this.visitorStatus = visitorStatus;
    }

    public int getHasCar() {
        return hasCar;
    }

    public void setHasCar(int hasCar) {
        this.hasCar = hasCar;
    }

    public int getIntermediary() {
        return intermediary;
    }

    public void setIntermediary(int intermediary) {
        this.intermediary = intermediary;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isIfSuccess() {
        return ifSuccess;
    }

    public void setIfSuccess(boolean ifSuccess) {
        this.ifSuccess = ifSuccess;
    }
}