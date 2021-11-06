package com.appnext.tooluntils;

import com.appnext.background.PackageInfo;
import com.appnext.database.AppUsageInfoByAppName;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApknameMap {
//    ApknameToNumber是从包名到数字的映射，NumberToApkname是从数字到包名的映射
    public static HashMap<String,Integer> ApknameToNumber = new HashMap<>();
    public static HashMap<Integer,String> NumberToApkname = new HashMap<>();


    public static void createMap() {
        List<AppUsageInfoByAppName> appUsageInfoByAppNames = LitePal.findAll(AppUsageInfoByAppName.class);
        for (int i = 0;i < appUsageInfoByAppNames.size();++i) {
            AppUsageInfoByAppName appUsageInfoByAppName = appUsageInfoByAppNames.get(i);
            ApknameToNumber.put(appUsageInfoByAppName.getPkgName(),i);
            NumberToApkname.put(i,appUsageInfoByAppName.getPkgName());
        }
    }
}
