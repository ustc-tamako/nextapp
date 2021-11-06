package com.appnext.database;

import org.litepal.crud.LitePalSupport;

public class AppInfo extends LitePalSupport {
    private String pkgName;
    private int number;
    private String appName;
//    private int theme;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

//    public int getTheme() {
//        return theme;
//    }
//
//    public void setTheme(int theme) {
//        this.theme = theme;
//    }
}
