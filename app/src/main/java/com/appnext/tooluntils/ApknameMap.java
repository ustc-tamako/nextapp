package com.appnext.tooluntils;

import com.appnext.background.PackageInfo;
import com.appnext.database.AppInfo;
import com.appnext.database.AppUsageInfoByAppName;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApknameMap {
    //    ApknameToNumber是从包名到数字的映射，NumberToApkname是从数字到包名的映射
    public static HashMap<String,Integer> ApknameToNumber = new HashMap<>();
    public static HashMap<Integer,String> NumberToApkname = new HashMap<>();
    public static HashMap<String,String> PkgnameToAppname = new HashMap<>();
    public static HashMap<String,String> AppnameToPkgname = new HashMap<>();


    public static void createMap() {
        List<AppInfo> appInfos = LitePal.findAll(AppInfo.class);
        for (int i = 0;i < appInfos.size();++i) {
            AppInfo appInfo = appInfos.get(i);
            ApknameToNumber.put(appInfo.getPkgName(),appInfo.getNumber());
            NumberToApkname.put(appInfo.getNumber(),appInfo.getPkgName());
            PkgnameToAppname.put(appInfo.getPkgName(),appInfo.getAppName());
            AppnameToPkgname.put(appInfo.getAppName(),appInfo.getPkgName());
        }
    }
}