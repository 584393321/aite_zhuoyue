package com.aliyun.ayland.data;


/**
 * @author guikong on 18/4/12.
 */
public class ATAppointmentOpenBean {
    /**
     * appFaceCheck : true
     * appointment : false
     * bluetooth : false
     * card : true
     * createTime : 1617092789000
     * customerCode : 10008
     * face : true
     * id : 4
     * ifDelete : false
     * qrcode : true
     * qrcodeTime : 720
     * talk : false
     * talkConfigure : 101
     * updateTime : 1617092789000
     * villageCode : 10197
     */

    private boolean appFaceCheck;
    private boolean appointment;
    private boolean bluetooth;
    private boolean card;
    private long createTime;
    private int customerCode;
    private boolean face;
    private int id;
    private boolean ifDelete;
    private boolean qrcode;
    private int qrcodeTime;
    private boolean talk;
    private int talkConfigure;
    private long updateTime;
    private int villageCode;

    public boolean isAppFaceCheck() {
        return appFaceCheck;
    }

    public void setAppFaceCheck(boolean appFaceCheck) {
        this.appFaceCheck = appFaceCheck;
    }

    public boolean isAppointment() {
        return appointment;
    }

    public void setAppointment(boolean appointment) {
        this.appointment = appointment;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public boolean isCard() {
        return card;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(int customerCode) {
        this.customerCode = customerCode;
    }

    public boolean isFace() {
        return face;
    }

    public void setFace(boolean face) {
        this.face = face;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIfDelete() {
        return ifDelete;
    }

    public void setIfDelete(boolean ifDelete) {
        this.ifDelete = ifDelete;
    }

    public boolean isQrcode() {
        return qrcode;
    }

    public void setQrcode(boolean qrcode) {
        this.qrcode = qrcode;
    }

    public int getQrcodeTime() {
        return qrcodeTime;
    }

    public void setQrcodeTime(int qrcodeTime) {
        this.qrcodeTime = qrcodeTime;
    }

    public boolean isTalk() {
        return talk;
    }

    public void setTalk(boolean talk) {
        this.talk = talk;
    }

    public int getTalkConfigure() {
        return talkConfigure;
    }

    public void setTalkConfigure(int talkConfigure) {
        this.talkConfigure = talkConfigure;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(int villageCode) {
        this.villageCode = villageCode;
    }
}