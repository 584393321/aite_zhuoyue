package com.aliyun.ayland.data;

public class ATFindSafetyApplicationBean {
    /**
     * applicationIcon : http://120.78.151.92/pic/icon/ico_zhaf_djlr@2x.png
     * applicationId : 188
     * applicationIdentification : app_elderly_care_subscribe
     * applicationName : 独居老人关怀订阅
     * status : 0
     */

    private String applicationIcon;
    private int applicationId;
    private String applicationIdentification;
    private String applicationName;
    private int status;

    public String getApplicationIcon() {
        return applicationIcon;
    }

    public void setApplicationIcon(String applicationIcon) {
        this.applicationIcon = applicationIcon;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationIdentification() {
        return applicationIdentification;
    }

    public void setApplicationIdentification(String applicationIdentification) {
        this.applicationIdentification = applicationIdentification;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
