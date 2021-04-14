package com.aliyun.ayland.data;

public class ATSceneManualAutoBean {
    public static final int NOTSHOW = 1;
    public static final int SHOWING = 2;
    public static final int SHOWSUCCESS = 3;
    private int isShowing = NOTSHOW;
    private String deployStatus;
    private String sceneIcon;
    private String sceneId;
    private String sceneName;
    private int sceneType;

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public int getIsShowing() {
        return isShowing;
    }

    public void setIsShowing(int isShowing) {
        this.isShowing = isShowing;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }

    public String getSceneIcon() {
        return sceneIcon;
    }

    public void setSceneIcon(String sceneIcon) {
        this.sceneIcon = sceneIcon;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    @Override
    public String toString() {
        return "SceneManualAutoBean{" +
                "deployStatus='" + deployStatus + '\'' +
                ", sceneIcon='" + sceneIcon + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", sceneName='" + sceneName + '\'' +
                '}';
    }
}
