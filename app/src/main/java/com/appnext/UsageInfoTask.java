package com.appnext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UsageInfoTask extends AsyncTask<Context,java.lang.Void,java.lang.Void> {
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
            String startTime = appUsageInfo.getStartTime();
            String endTime = appUsageInfo.getEndTime();
            long usedTime = appUsageInfo.getUsedTime();
            LitePal.deleteAll(AppUsageInfo.class,"appName = ? and startTime = ? and endTime = ? and usedTime = ?",appName,startTime,endTime, String.valueOf(usedTime));
            addDataToAppUsageInfo(appUsageInfo.getAppName(),appUsageInfo.getStartTime(),appUsageInfo.getEndTime(),appUsageInfo.getUsedTime());
        }
        Log.d(TAG, "Step 1 over ");


//        向AppUsageInfoByAppName表格中添加数据
        mAppUsageInfoList = mUseTimeDataManager.getAppUsageInfoList();
        mPackageInfoList = mUseTimeDataManager.getPackageInfoList();
        for (int i = 0;i < mPackageInfoList.size();++i) {
            PackageInfo packageInfo = mPackageInfoList.get(i);
            String mAppName = packageInfo.getmAppName();
            int mAllUsedTime = (int) packageInfo.getmUsedTime();
            int mAllOpenTime = packageInfo.getmUsedCount();
            List<Integer> mUsedByHour = packageInfo.getmOpenTime();
            LitePal.deleteAll(AppUsageInfoByAppName.class,"appName = ?"
                            ,mAppName);
            if (mAllOpenTime != 0 || mAllOpenTime != 0) {
                addDataToAppUsageInfoByAppName(mAppName,mAllUsedTime,mAllOpenTime,mUsedByHour);
            }
        }
        return null;
    }

    //    AppUsageInfo表格添加数据
    private void addDataToAppUsageInfo(String appName,String startTime,String endTime,long usedTime) {
        AppUsageInfo appUsageInfo = new AppUsageInfo();
        appUsageInfo.setAppName(appName);
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
    private void addDataToAppUsageInfoByAppName(String appName,int allUsedTime,int allOpenTime,List<Integer> usedTimeByHour) {
        AppUsageInfoByAppName appUsageInfoByAppName = new AppUsageInfoByAppName();
        appUsageInfoByAppName.setAppName(appName);
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
        appUsageInfoByAppName.save();
//        Log.d(TAG,
//                "name:"+appUsageInfoByAppName.getmAppName()+
//                        " all_used_time:"+appUsageInfoByAppName.getmAllUsedTime()+
//                        " all_open_time:"+appUsageInfoByAppName.getmAllUsedTime());
    }
}
