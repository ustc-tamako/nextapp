package com.appnext.background;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.appnext.ItemDetailHostActivity;
import com.appnext.ItemListFragment;
import com.appnext.database.AppUsageInfo;
import com.appnext.database.AppUsageInfoByAppName;
import com.appnext.placeholder.PlaceholderContent;
import com.appnext.tooluntils.DatabaseUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UsageInfoTask extends AsyncTask<Context, Void, Void> {
    UseTimeDataManager mUseTimeDataManager;
    private Context context;
    ArrayList<AppUsageInfo> mAppUsageInfoList;
    ArrayList<PackageInfo> mPackageInfoList;
    private static final String TAG = "UsageInfoTask";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.d(TAG, "onPostExecute: 执行结束");
        Toast.makeText(this.context,"执行结束",Toast.LENGTH_SHORT).show();
        super.onPostExecute(unused);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Void doInBackground(Context... contexts) {
        LitePal.getDatabase();
        this.context = contexts[0];
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
            int category = packageInfo.getCategory();
            String lastUsedTime = packageInfo.getmEndTime();
            LitePal.deleteAll(AppUsageInfoByAppName.class,"appName = ?"
                            ,appName);
            if (allOpenTime != 0 || allOpenTime != 0) {
                DatabaseUtils.addDataToAppUsageInfoByAppName(appName,pkgName,allUsedTime,allOpenTime,usedByHour,icon,category,lastUsedTime);
            }
        }




        return null;

    }


}
