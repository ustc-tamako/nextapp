package com.appnext.tooluntils;

import android.content.Context;
import android.util.Log;

import com.appnext.ItemDetailHostActivity;
import com.appnext.background.PackageInfo;
import com.appnext.background.UseTimeDataManager;
import com.appnext.database.AppUsageInfo;
import com.appnext.database.AppUsageInfoByAppName;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class UpdateDatabase {
    private static final String TAG = "UpdateDatabase";
    UseTimeDataManager mUseTimeDataManager;
    private Context context;
    ArrayList<AppUsageInfo> mAppUsageInfoList;
    ArrayList<PackageInfo> mPackageInfoList;

    public UpdateDatabase(Context context) {
        this.context = context;
    }

    public void update() {
        LitePal.getDatabase();
        mUseTimeDataManager = UseTimeDataManager.getInstance(context);
        mUseTimeDataManager.refreshData(7);
        mAppUsageInfoList = mUseTimeDataManager.getAppUsageInfoList();
        Log.d(TAG, "event个数:"+mAppUsageInfoList.size());
        LitePal.saveAll(mAppUsageInfoList);
//        实现去重
//        向AppUsageInfo表格中添加数据
        for (int i = 0;i < mAppUsageInfoList.size();++i) {
            AppUsageInfo appUsageInfo = mAppUsageInfoList.get(i);
            String appName = appUsageInfo.getAppName();
            String pkgName = appUsageInfo.getPkgName();
            String startTime = appUsageInfo.getStartTime();
            String endTime = appUsageInfo.getEndTime();
            long usedTime = appUsageInfo.getUsedTime();
            LitePal.deleteAll(AppUsageInfo.class,"appName = ? and startTime = ? and endTime = ? and usedTime = ?",appName,startTime,endTime, String.valueOf(usedTime));
            DatabaseUtils.addDataToAppUsageInfo(appName,pkgName,startTime,endTime,usedTime);
        }
        Log.d(TAG, "Step 1 over ");


//        向AppUsageInfoByAppName表格中添加数据
        mAppUsageInfoList = mUseTimeDataManager.getAppUsageInfoList();
        mPackageInfoList = mUseTimeDataManager.getPackageInfoList();
        for (int i = 0;i < mPackageInfoList.size();++i) {
            PackageInfo packageInfo = mPackageInfoList.get(i);
            String appName = packageInfo.getmAppName();
            String pkgName = packageInfo.getmPackageName();
            int allUsedTime = (int) packageInfo.getmUsedTime();
            int allOpenTime = packageInfo.getmUsedCount();
            List<Integer> usedByHour = packageInfo.getmOpenTime();
            byte[] icon = packageInfo.getDrawable();
            LitePal.deleteAll(AppUsageInfoByAppName.class,"appName = ?"
                    ,appName);
            if (allOpenTime != 0 || allOpenTime != 0) {
                DatabaseUtils.addDataToAppUsageInfoByAppName(appName,pkgName,allUsedTime,allOpenTime,usedByHour,icon);
            }
        }
    }
}
