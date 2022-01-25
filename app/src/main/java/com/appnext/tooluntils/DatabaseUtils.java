package com.appnext.tooluntils;

import android.util.Log;

import com.appnext.database.AppUsageInfo;
import com.appnext.database.AppUsageInfoByAppName;

import java.util.List;

public class DatabaseUtils {

    //    AppUsageInfo表格添加数据
    public static final void addDataToAppUsageInfo(String appName,String pkgName,String startTime,String endTime,long usedTime) {
        AppUsageInfo appUsageInfo = new AppUsageInfo();
        appUsageInfo.setAppName(appName);
        appUsageInfo.setPkgName(pkgName);
        appUsageInfo.setStartTime(startTime);
        appUsageInfo.setEndTime(endTime);
        appUsageInfo.setUsedTime(usedTime);
        appUsageInfo.save();
//        Log.d(TAG,
//                "name:"+appUsageInfo.getmAppName()+
//                        " start_time:"+appUsageInfo.getmStartTime()+
//                        " end_time:"+appUsageInfo.getmEndTime()+
//                        " temp_used_time:"+appUsageInfo.getmUsedTime());
    }

    //    AppUsageInfoByAppName表格添加数据
    public static final void addDataToAppUsageInfoByAppName(String appName, String pkgName, int allUsedTime, int allOpenTime, List<Integer> usedTimeByHour, byte[] icon,int category,String lastUsedTime) {
        AppUsageInfoByAppName appUsageInfoByAppName = new AppUsageInfoByAppName();
        appUsageInfoByAppName.setAppName(appName);
        appUsageInfoByAppName.setPkgName(pkgName);
        appUsageInfoByAppName.setAllUsedTime(allUsedTime);
        appUsageInfoByAppName.setAllOpenTime(allOpenTime);
//        appUsageInfoByAppName.setmUsedTimeByHour(usedTimeByHour);
        appUsageInfoByAppName.setUsedTimeByHour0(usedTimeByHour.get(0));
        appUsageInfoByAppName.setUsedTimeByHour1(usedTimeByHour.get(1));
        appUsageInfoByAppName.setUsedTimeByHour2(usedTimeByHour.get(2));
        appUsageInfoByAppName.setUsedTimeByHour3(usedTimeByHour.get(3));
        appUsageInfoByAppName.setUsedTimeByHour4(usedTimeByHour.get(4));
        appUsageInfoByAppName.setUsedTimeByHour5(usedTimeByHour.get(5));
        appUsageInfoByAppName.setUsedTimeByHour6(usedTimeByHour.get(6));
        appUsageInfoByAppName.setUsedTimeByHour7(usedTimeByHour.get(7));
        appUsageInfoByAppName.setUsedTimeByHour8(usedTimeByHour.get(8));
        appUsageInfoByAppName.setUsedTimeByHour9(usedTimeByHour.get(9));
        appUsageInfoByAppName.setUsedTimeByHour10(usedTimeByHour.get(10));
        appUsageInfoByAppName.setUsedTimeByHour11(usedTimeByHour.get(11));
        appUsageInfoByAppName.setUsedTimeByHour12(usedTimeByHour.get(12));
        appUsageInfoByAppName.setUsedTimeByHour13(usedTimeByHour.get(13));
        appUsageInfoByAppName.setUsedTimeByHour14(usedTimeByHour.get(14));
        appUsageInfoByAppName.setUsedTimeByHour15(usedTimeByHour.get(15));
        appUsageInfoByAppName.setUsedTimeByHour16(usedTimeByHour.get(16));
        appUsageInfoByAppName.setUsedTimeByHour17(usedTimeByHour.get(17));
        appUsageInfoByAppName.setUsedTimeByHour18(usedTimeByHour.get(18));
        appUsageInfoByAppName.setUsedTimeByHour19(usedTimeByHour.get(19));
        appUsageInfoByAppName.setUsedTimeByHour20(usedTimeByHour.get(20));
        appUsageInfoByAppName.setUsedTimeByHour21(usedTimeByHour.get(21));
        appUsageInfoByAppName.setUsedTimeByHour22(usedTimeByHour.get(22));
        appUsageInfoByAppName.setUsedTimeByHour23(usedTimeByHour.get(23));
        appUsageInfoByAppName.setName(new String(icon));
        appUsageInfoByAppName.setLastUsedTime(lastUsedTime);
        appUsageInfoByAppName.setCategory(category);
        String str = "abcd";
        byte[] tmp = str.getBytes();
        appUsageInfoByAppName.setDrawable(icon);
        appUsageInfoByAppName.save();
//        Log.d("icon11", "icon:"+tmp);
//        Log.d(TAG,
//                "name:"+appUsageInfoByAppName.getmAppName()+
//                        " all_used_time:"+appUsageInfoByAppName.getmAllUsedTime()+
//                        " all_open_time:"+appUsageInfoByAppName.getmAllUsedTime());
    }
}
