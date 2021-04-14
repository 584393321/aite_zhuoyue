package com.aliyun.ayland.data;

public class ATBannerBean {
    private String id;
    private String imgSort;
    private String imgUrl;
    private String jumpUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgSort() {
        return imgSort;
    }

    public void setImgSort(String imgSort) {
        this.imgSort = imgSort;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "id='" + id + '\'' +
                ", imgSort='" + imgSort + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                '}';
    }
}
