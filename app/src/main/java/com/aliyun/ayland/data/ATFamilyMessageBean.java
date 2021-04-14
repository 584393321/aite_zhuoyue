package com.aliyun.ayland.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ATFamilyMessageBean implements Parcelable {
    private String mobile;
    private String personCode;
    private String personName;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mobile);
        out.writeString(personCode);
        out.writeString(personName);
    }

    public static final Creator<ATFamilyMessageBean> CREATOR = new Creator<ATFamilyMessageBean>() {
        @Override
        public ATFamilyMessageBean[] newArray(int size) {
            return new ATFamilyMessageBean[size];
        }

        @Override
        public ATFamilyMessageBean createFromParcel(Parcel in) {
            return new ATFamilyMessageBean(in);
        }
    };

    private ATFamilyMessageBean(Parcel in) {
        mobile = in.readString();
        personCode = in.readString();
        personName = in.readString();
    }
}