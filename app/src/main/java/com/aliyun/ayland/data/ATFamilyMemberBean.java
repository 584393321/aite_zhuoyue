package com.aliyun.ayland.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ATFamilyMemberBean implements Parcelable{
    /**
     * birthDate : 1990-11-22
     * householdtype : 101
     * idNumber :
     * ifAdmin : 0
     * inDate : 2021-03-10 08:00:00
     * nickname : 高瑶
     * personCode : 1005441
     * phone : 17688539501
     */

    private String birthDate;
    private String householdtype;
    private String idNumber;
    private int ifAdmin;
    private String inDate;
    private String nickname;
    private String personCode;
    private String phone;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(birthDate);
        out.writeString(householdtype);
        out.writeString(idNumber);
        out.writeInt(ifAdmin);
        out.writeString(inDate);
        out.writeString(nickname);
        out.writeString(personCode);
        out.writeString(phone);
    }

    public static final Creator<ATFamilyMemberBean> CREATOR = new Creator<ATFamilyMemberBean>() {
        @Override
        public ATFamilyMemberBean[] newArray(int size) {
            return new ATFamilyMemberBean[size];
        }

        @Override
        public ATFamilyMemberBean createFromParcel(Parcel in) {
            return new ATFamilyMemberBean(in);
        }
    };

    private ATFamilyMemberBean(Parcel in) {
        birthDate = in.readString();
        householdtype = in.readString();
        idNumber = in.readString();
        ifAdmin = in.readInt();
        inDate = in.readString();
        nickname = in.readString();
        personCode = in.readString();
        phone = in.readString();
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getHouseholdtype() {
        return householdtype;
    }

    public void setHouseholdtype(String householdtype) {
        this.householdtype = householdtype;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getIfAdmin() {
        return ifAdmin;
    }

    public void setIfAdmin(int ifAdmin) {
        this.ifAdmin = ifAdmin;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
