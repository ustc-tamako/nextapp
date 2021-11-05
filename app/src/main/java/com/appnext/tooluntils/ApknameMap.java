package com.appnext.tooluntils;

import com.appnext.background.PackageInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class ApknameMap {
//    ApknameToNumber是从包名到数字的映射，NumberToApkname是从数字到包名的映射
    public static HashMap<String,Integer> ApknameToNumber = new HashMap<>();
    public static HashMap<Integer,String> NumberToApkname = new HashMap<>();


    public static void createMap(ArrayList<PackageInfo> mPackageInfoList) {
        for (int i = 0;i < mPackageInfoList.size();++i) {
            ApknameToNumber.put(mPackageInfoList.get(i).getmPackageName(),i);
            NumberToApkname.put(i,mPackageInfoList.get(i).getmPackageName());
        }
    }
}
