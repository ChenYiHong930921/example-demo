package com.chenyihong.plugin.entity;

public class FlavorParamsEntity {

    private final String flavorName;

    private final String applicationId;

    private final String versionCode;

    private final String versionName;

    private final String appName;

    private final String appIconPath;

    private final String openWebsite;

    public FlavorParamsEntity(String flavorName, String applicationId, String versionCode, String versionName, String appName, String appIconPath, String openWebsite) {
        this.flavorName = flavorName;
        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.appName = appName;
        this.appIconPath = appIconPath;
        this.openWebsite = openWebsite;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppIconPath() {
        return appIconPath;
    }

    public String getOpenWebsite() {
        return openWebsite;
    }

    @Override
    public String toString() {
        return "FlavorParamsEntity{" +
                "flavorName='" + flavorName + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", appName='" + appName + '\'' +
                ", appIconPath='" + appIconPath + '\'' +
                ", openWebsite='" + openWebsite + '\'' +
                '}';
    }
}
