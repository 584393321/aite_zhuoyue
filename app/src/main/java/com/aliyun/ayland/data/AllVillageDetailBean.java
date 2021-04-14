package com.aliyun.ayland.data;

import java.util.List;

public class AllVillageDetailBean {
    private String code;
    private String name;
    private String string;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "AllVillageDetailBean{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", string='" + string + '\'' +
                '}';
    }
}