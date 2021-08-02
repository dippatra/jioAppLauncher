package com.appinfomodule;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String AppName;
    private String packageName;
    private String versionName;
    private int versionCode;
    private String launchActivityName;
    private Drawable launchIcon;
    public AppInfo(String appName,String packageName,String versionName,int versionCode,String launchActivityName,Drawable launchIcon){
        this.AppName=appName;
        this.packageName=packageName;
        this.versionName=versionName;
        this.versionCode=versionCode;
        this.launchActivityName=launchActivityName;
        this.launchIcon=launchIcon;
    }

    public String getAppName() {
        return AppName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getLaunchActivityName() {
        return launchActivityName;
    }

    public Drawable getLaunchIcon() {
        return launchIcon;
    }
}
