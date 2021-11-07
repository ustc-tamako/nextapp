package com.appnext.background;

import static java.lang.Long.max;
import static java.lang.Long.min;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.appnext.database.AppUsageInfo;
import com.appnext.tooluntils.ApknameMap;
import com.appnext.tooluntils.DateTransUtils;
import com.appnext.tooluntils.EventUtils;
import com.appnext.tooluntils.IconUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 主要的数据操作的类
 * <p>
 *
 */
public class UseTimeDataManager {
    public static final String TAG = "Carden";

    private static UseTimeDataManager mUseTimeDataManager;

    private Context mContext;

    private int mDayNum;
    private HashMap<String,PackageInfo> pkgNameToPackageInfo = new HashMap<>();
    private ArrayList<Integer> mZeroArrayList = new ArrayList<>();
    private long ZeroLong = 0;

    //记录从系统中读取的数据
    private ArrayList<UsageEvents.Event> mEventList;
    private ArrayList<UsageEvents.Event> mEventListChecked;
    private ArrayList<UsageStats> mStatsList;

    //记录打开一次应用，使用的activity详情
    private ArrayList<OneTimeDetails> mOneTimeDetailList = new ArrayList<>();

    //记录某一次打开应用的使用情况（查询某一次使用情况的时候，用于界面显示）
    private OneTimeDetails mOneTimeDetails;

    //主界面数据
    private ArrayList<PackageInfo> mPackageInfoList = new ArrayList<>();

    //采集数据
    private  ArrayList<AppUsageInfo> mAppUsageInfoList = new ArrayList<>();

    public UseTimeDataManager(Context context) {
        this.mContext = context;
    }



    public static UseTimeDataManager getInstance(Context context) {
        if (mUseTimeDataManager == null) {
            mUseTimeDataManager = new UseTimeDataManager(context);
        }

        return mUseTimeDataManager;
    }

    /**
     * 主要的数据获取函数
     *
     * @param dayNumber 查询若干天前的数据
     * @return int        0 : event usage 均查询到了
     * 1 : event 未查询到 usage 查询到了
     * 2 : event usage 均未查询到
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int refreshData(int dayNumber) {
        mDayNum = dayNumber;
        mEventList = getEventList(dayNumber);
        mStatsList = getUsageList(dayNumber);

        if (mEventList == null || mEventList.size() == 0) {
//            Log.i(TAG, " UseTimeDataManager-refreshData()   未查到events");

            if (mStatsList == null || mStatsList.size() == 0) {
//                Log.i(TAG, " UseTimeDataManager-refreshData()   未查到stats");
                return 2;
            }
            return 1;
        }

        //获取数据之后，进行数据的处理
        mEventListChecked = getEventListChecked();
        refreshOneTimeDetailList(0);
        refreshPackageInfoList();
        ApknameMap.createMap();
//        test
//        Set<Map.Entry<String, Integer>> entries = ApknameMap.ApknameToNumber.entrySet();
//        for (Map.Entry<String, Integer> entry : entries) {
//            Log.d("ApknameToNumber", entry.getKey()+"==="+entry.getValue());
//        }
//
//        Set<Map.Entry<Integer, String>> entries1 = ApknameMap.NumberToApkname.entrySet();
//        for (Map.Entry<Integer, String> entry : entries1) {
//            Log.d("NumberToApkname", entry.getKey()+"==="+entry.getValue());
//        }
        refreshAppUsageInfoList();
//        sendEventBus();
        return 0;
    }

    //分类完成，初始化主界面所用到的数据
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshPackageInfoList() {
        mPackageInfoList.clear();
        pkgNameToPackageInfo.clear();

        for (int i = 0;i < 24;++i) {
            mZeroArrayList.add(0);
        }
        for (int i = 0; i < mStatsList.size(); i++) {
            //屏蔽系统应用
            if (!isSystemApp(mContext,mStatsList.get(i).getPackageName())) {
                ArrayList<OneTimeDetails> mTimeDetailsList = new ArrayList<>();
                for (int j = 0;j < mOneTimeDetailList.size();++j) {
                    if (mOneTimeDetailList.get(j).getPkgName().equals(mStatsList.get(i).getPackageName())) {
                        mTimeDetailsList.add(mOneTimeDetailList.get(j));
                    }
                }
                PackageInfo info = new PackageInfo(0, 0, mStatsList.get(i).getPackageName(), getApplicationNameByPackageName(mContext, mStatsList.get(i).getPackageName()),getApplicationStartTime(mStatsList.get(i)),getApplicationEndTime(mStatsList.get(i)),mZeroArrayList,mTimeDetailsList);
                pkgNameToPackageInfo.put(mStatsList.get(i).getPackageName(),info);
                mPackageInfoList.add(info);
            }
        }
//        Log.d(TAG, "mPackageInfoList.size:"+mPackageInfoList.size());
        for (int i = 0;i < mPackageInfoList.size();++i) {
            PackageInfo packageInfo = mPackageInfoList.get(i);
            packageInfo.setmOpenTime(calculateUseTimeByHour(packageInfo.getmPackageName()));
            packageInfo.setmUsedTime(calculateUseTimeToday(packageInfo.getmPackageName()));
            packageInfo.setDrawable(getDrawableIconByPackageName(mContext,packageInfo.getmPackageName()));

        }
//        pkgNameToPackageInfo.get("com.example.appinfocollect").setmOpenTime(calculateUseTimeByHour(pkgNameToPackageInfo.get("com.example.appinfocollect").getmPackageName()));
        long startTime = DateTransUtils.getTodayStartStamp(0,0,0);
        long endTime = DateTransUtils.getTodayStartStamp(0,0,0)+DateTransUtils.DAY_IN_MILLIS;
        for (int n = 0; n < mPackageInfoList.size(); n++) {
            String pkg = mPackageInfoList.get(n).getmPackageName();
            for (int m = 0; m < mOneTimeDetailList.size(); m++) {
                if (pkg.equals(mOneTimeDetailList.get(m).getPkgName())) {
                    if (mOneTimeDetailList.get(m).getStartTime_long() >= startTime && mOneTimeDetailList.get(m).getStopTime_long() <= endTime) {
                        mPackageInfoList.get(n).addCount();
                    }
                }
            }
        }

    }

    public void refreshAppUsageInfoList() {
//        for (Map.Entry<String, PackageInfo> entry : pkgNameToPackageInfo.entrySet()) {
//            Log.d(TAG, "key:"+entry.getKey()+" value:"+entry.getValue());
//        }
        mAppUsageInfoList.clear();
        for (int i = 0;i < mOneTimeDetailList.size();++i) {
            OneTimeDetails oneTimeDetails = mOneTimeDetailList.get(i);
            if (isSystemApp(mContext,oneTimeDetails.getPkgName())) {
                continue;
            }
            String pkgName = oneTimeDetails.getPkgName();
            PackageInfo info = pkgNameToPackageInfo.get(pkgName);
//            Log.d(TAG, "pkgName: "+pkgName);
//            Log.d(TAG, "info: "+info);
            try {
                AppUsageInfo appUsageInfo = new AppUsageInfo();
                appUsageInfo.setAppName(getApplicationNameByPackageName(mContext,pkgName));
                appUsageInfo.setStartTime(oneTimeDetails.getStartTime());
                appUsageInfo.setEndTime(oneTimeDetails.getStopTime());
                appUsageInfo.setUsedTime(oneTimeDetails.getUseTime()/1000);
                appUsageInfo.setPkgName(oneTimeDetails.getPkgName());
                mAppUsageInfoList.add(appUsageInfo);
            }catch (NullPointerException e){
            }
        }
        return;
    }

    //按照使用时间的长短进行排序，获取应用使用情况列表
    public ArrayList<PackageInfo> getmPackageInfoListOrderByTime() {
//        Log.i(TAG, " UseTimeDataManager-getmPackageInfoListOrderByTime()   排序前：mPackageInfoList.size()" + mPackageInfoList.size());

        for (int n = 0; n < mPackageInfoList.size(); n++) {
            for (int m = n + 1; m < mPackageInfoList.size(); m++) {
                if (mPackageInfoList.get(n).getmUsedTime() < mPackageInfoList.get(m).getmUsedTime()) {
                    PackageInfo temp = mPackageInfoList.get(n);
                    mPackageInfoList.set(n, mPackageInfoList.get(m));
                    mPackageInfoList.set(m, temp);
                }
            }
        }

//        Log.i(TAG, " UseTimeDataManager-getmPackageInfoListOrderByTime()   排序后：mPackageInfoList.size()" + mPackageInfoList.size());
        return mPackageInfoList;
    }

    //按照使用次数的多少进行排序，获取应用使用情况列表
    public ArrayList<PackageInfo> getmPackageInfoListOrderByCount() {
//        Log.i(TAG, " UseTimeDataManager-getmPackageInfoListOrderByCount()   排序前：mPackageInfoList.size()" + mPackageInfoList.size());

        for (int n = 0; n < mPackageInfoList.size(); n++) {
            for (int m = n + 1; m < mPackageInfoList.size(); m++) {
                if (mPackageInfoList.get(n).getmUsedCount() < mPackageInfoList.get(m).getmUsedCount()) {
                    PackageInfo temp = mPackageInfoList.get(n);
                    mPackageInfoList.set(n, mPackageInfoList.get(m));
                    mPackageInfoList.set(m, temp);
                }
            }
        }

//        Log.i(TAG, " UseTimeDataManager-getmPackageInfoListOrderByCount()   排序后：mPackageInfoList.size()" + mPackageInfoList.size());
        return mPackageInfoList;
    }

    public ArrayList<AppUsageInfo> getAppUsageInfoList() {
        return mAppUsageInfoList;
    }

    public ArrayList<PackageInfo> getPackageInfoList() {
        return mPackageInfoList;
    }

    /**
     * @TargetApi(Build.VERSION_CODES.LOLLIPOP) private void sendEventBus(){
     * TimeEvent event = new TimeEvent(0,0);
     * if(mEventListChecked != null && mEventListChecked.size() > 0){
     * event.setmStartTime(mEventListChecked.get(0).getTimeStamp());
     * event.setmEndTime(mEventListChecked.get(mEventListChecked.size()-1).getTimeStamp());
     * }
     * MsgEventBus.getInstance().post(event);
     * }
     **/

    //从系统中获取event数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<UsageEvents.Event> getEventList(int dayNumber) {
        ArrayList<UsageEvents.Event> mEventList = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//        calendar.add(Calendar.YEAR, -1);
//        //long startTime = calendar.getTimeInMillis()- 3 * DateTransUtils.DAY_IN_MILLIS;
//        long startTime = calendar.getTimeInMillis();

        long endTime = 0, startTime = 0,lastTime = 0;
        if (dayNumber == 0) {
            endTime = System.currentTimeMillis();
            startTime = DateTransUtils.getZeroClockTimestamp(endTime);
        } else {
            SimpleDateFormat dateFormat  = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//            String t=format.format(startTime);
            endTime = System.currentTimeMillis();
//            几天前的一天结束时间
            lastTime = DateTransUtils.getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1;
//            endTime = DateTransUtils.getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1;
            startTime = lastTime - DateTransUtils.DAY_IN_MILLIS + 1;
//            Log.d(TAG, "getEventList startTime:"+dateFormat.format(startTime)+" endTime"+dateFormat.format(endTime)+" lastTime:"+dateFormat.format(lastTime));
        }
        return EventUtils.getEventList(mContext, startTime, endTime);
    }

    //从系统中获取Usage数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<UsageStats> getUsageList(int dayNumber) {
        long endTime = 0, startTime = 0,lastTime = 0;
        if (dayNumber == 0) {
            endTime = System.currentTimeMillis();
            startTime = DateTransUtils.getZeroClockTimestamp(endTime);
        } else {
            SimpleDateFormat dateFormat  = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//            String t=format.format(startTime);
            endTime = System.currentTimeMillis();
//            几天前的一天结束时间
            lastTime = DateTransUtils.getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1;
//            endTime = DateTransUtils.getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1;
            startTime = lastTime - DateTransUtils.DAY_IN_MILLIS + 1;
//            Log.d(TAG, "getUsageList startTime:"+dateFormat.format(startTime)+" endTime"+dateFormat.format(endTime)+" lastTime:"+dateFormat.format(lastTime));
        }

        return EventUtils.getUsageList(mContext, startTime, endTime);
    }

    //仅保留 event 中 type 为 1或者2 的
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<UsageEvents.Event> getEventListChecked() {
        ArrayList<UsageEvents.Event> mList = new ArrayList<>();
        for (int i = 0; i < mEventList.size(); i++) {
            if (mEventList.get(i).getEventType() == 1 || mEventList.get(i).getEventType() == 2) {
                mList.add(mEventList.get(i));
            }
        }
        return mList;
    }

//    待补充
    public String getApplicationStartTime(UsageStats stats) {
        return "";
    }
//得到应用最后一次使用时间
    public String getApplicationEndTime(UsageStats stats) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String t=format.format(new Date(stats.getLastTimeUsed()));
        return t;
    }

//    通过包名获取应用名和图标
    public  String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }


    public byte[] getDrawableIconByPackageName(Context context,String packageName) {
        Drawable icon = IconUtils.getAppIcon(context,packageName);
        Bitmap bitmap = IconUtils.drawableToBitmap(icon);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<UsageEvents.Event> getEventListCheckWithoutErrorData() {
        ArrayList<UsageEvents.Event> mList = new ArrayList<>();
        for (int i = 0; i < mEventList.size(); i++) {
            if (mEventList.get(i).getEventType() == 1 || mEventList.get(i).getEventType() == 2) {
                mList.add(mEventList.get(i));
            }
        }
        return mList;
    }

    //从 startIndex 开始分类event  直至将event分完
    //每次从0开始，将原本的 mOneTimeDetailList 清除一次,然后开始分类
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void refreshOneTimeDetailList(int startIndex) {
//        Log.i(TAG, "  refreshOneTimeDetailList()     startIndex : " + startIndex);

        if (startIndex == 0) {
//            Log.i(TAG, "  refreshOneTimeDetailList()     每次从0开始，将原本的 mOneTimeDetailList 清除一次,然后开始分类 ");
            if (mOneTimeDetailList != null) {
                mOneTimeDetailList.clear();
            }
        }

        long totalTime = 0;
        int usedIndex = 0;
        String pkg = null;
        ArrayList<UsageEvents.Event> list = new ArrayList();
        for (int i = startIndex; i < mEventListChecked.size(); i++) {
            if (i == startIndex) {
                if (mEventListChecked.get(i).getEventType() == 2) {
//                    Log.i(TAG, "  refreshOneTimeDetailList()     warning : 每次打开一个app  第一个activity的类型是 2     ");
                }
                pkg = mEventListChecked.get(i).getPackageName();
                list.add(mEventListChecked.get(i));
            } else {
                if (pkg != null) {
                    if (pkg.equals(mEventListChecked.get(i).getPackageName())) {
                        list.add(mEventListChecked.get(i));
                        if (i == mEventListChecked.size() - 1) {
                            usedIndex = i;
                        }
                    } else {
                        usedIndex = i;
                        break;
                    }
                }
            }
        }

//        Log.i(TAG, "   mEventListChecked 分类:   before  check :   list.size() = " + list.size());
        checkEventList(list);
//        Log.i(TAG, "   mEventListChecked 分类:   after  check :   list.size() = " + list.size());
//        startTime = list.get(0).getTimeStamp();
//        endTime   = list.get( list.size() - 1 ).getTimeStamp();
//        Log.i(TAG, "   mEventListChecked 分类:  本次启动的包名：" + list.get(0).getPackageName() + "   时间：" + DateUtils.formatSameDayTime(list.get(0).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));
        for (int i = 1; i < list.size(); i += 2) {
            if (list.get(i).getEventType() == 2 && list.get(i - 1).getEventType() == 1) {
                totalTime += (list.get(i).getTimeStamp() - list.get(i - 1).getTimeStamp());
            }
        }
        OneTimeDetails oneTimeDetails = new OneTimeDetails(pkg, totalTime, list);
        mOneTimeDetailList.add(oneTimeDetails);

        if (usedIndex < mEventListChecked.size() - 1) {
            refreshOneTimeDetailList(usedIndex);
        } else {
//            Log.i(TAG, "  refreshOneTimeDetailList()     已经将  mEventListChecked 分类完毕   ");
        }

    }

    public ArrayList<OneTimeDetails> getPkgOneTimeDetailList(String pkg) {

        if ("all".equals(pkg)) {
            return mOneTimeDetailList;
        }

        ArrayList<OneTimeDetails> list = new ArrayList<>();
        if (mOneTimeDetailList != null && mOneTimeDetailList.size() > 0) {
            for (int i = 0; i < mOneTimeDetailList.size(); i++) {
                if (mOneTimeDetailList.get(i).getPkgName().equals(pkg)) {
                    list.add(mOneTimeDetailList.get(i));
                }
            }
        }
        return list;
    }

    // 采用回溯的思想：
    // 从头遍历EventList，如果发现异常数据，则删除该异常数据，并从头开始再次进行遍历，直至无异常数据
    // （异常数据是指：event 均为 type=1 和type=2 ，成对出现，一旦发现未成对出现的数据，即视为异常数据）
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkEventList(ArrayList<UsageEvents.Event> list) {
        boolean isCheckAgain = false;
        for (int i = 0; i < list.size() - 1; i += 2) {
            if (list.get(i).getClassName().equals(list.get(i + 1).getClassName())) {
                if (list.get(i).getEventType() != 1) {
//                    Log.i(UseTimeDataManager.TAG, "   EventList 出错  ： " + list.get(i).getPackageName() + "  " + DateUtils.formatSameDayTime(list.get(i).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString());
                    list.remove(i);
                    isCheckAgain = true;
                    break;
                }
                if (list.get(i + 1).getEventType() != 2) {
//                    Log.i(UseTimeDataManager.TAG, "   EventList 出错 ： " + list.get(i + 1).getPackageName() + "  " + DateUtils.formatSameDayTime(list.get(i + 1).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString());
                    list.remove(i);
                    isCheckAgain = true;
                    break;
                }
            } else {
                //i和i+1的className对不上，则删除第i个数据，重新检查
                list.remove(i);
                isCheckAgain = true;
                break;
            }
        }
        if (isCheckAgain) {
            checkEventList(list);
        }
    }

    // =======================================
    // service use
    // =======================================

    public ArrayList<PackageInfo> getPkgInfoListFromEventList() {
        return mPackageInfoList;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ArrayList<PackageInfo> getPkgInfoListFromUsageList() throws IllegalAccessException {
//        ArrayList<PackageInfo> result = new ArrayList<>();
//
//        if (mStatsList != null && mStatsList.size() > 0) {
//            for (int i = 0; i < mStatsList.size(); i++) {
//
//                result.add(new PackageInfo(getLaunchCount(mStatsList.get(i)), mStatsList.get(i).getTotalTimeInForeground(), mStatsList.get(i).getPackageName(), getApplicationNameByPackageName(mContext, mStatsList.get(i).getPackageName()),getApplicationStartTime(mStatsList.get(i)),getApplicationEndTime(mStatsList.get(i))),calculateUseTimeByHour());
//
//            }
//        }
//        return result;
//    }
    //判断app是否为系统qpp
    public static boolean isSystemApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)){
            return  false;
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 利用反射，获取UsageStats中统计的应用使用次数
    private int getLaunchCount(UsageStats usageStats) throws IllegalAccessException {
        Field field = null;
        try {
            field = usageStats.getClass().getDeclaredField("mLaunchCount");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return (int) field.get(usageStats);
    }

    //根据event计算使用时间
    public long calculateUseTime(String pkg) {
        long useTime = 0;
        for (int i = 0; i < mOneTimeDetailList.size(); i++) {
            if (mOneTimeDetailList.get(i).getPkgName().equals(pkg)) {
                useTime += mOneTimeDetailList.get(i).getUseTime();
            }
        }
//        Log.i(TAG, "  calculateUseTime : " + useTime);
        return useTime;
    }
//计算当天使用时间,该函数可以和calculateUseTimeByHour函数合并，加快运行速度
    @RequiresApi(api = Build.VERSION_CODES.N)
    public long calculateUseTimeToday(String pkg) {
        boolean flag = false;
        if (pkg.equals("com.miui.calculator")) {
            flag = true;
        }
        long useTime = 0;
        long startTime = DateTransUtils.getTodayStartStamp(0,0,0);
        long endTime = DateTransUtils.getTodayStartStamp(0,0,0)+DateTransUtils.DAY_IN_MILLIS;
//        if (flag)
//            Log.d("calculateUseTimeToday", "pkgName:"+pkg+" startTime:"+DateTransUtils.stampToDate(startTime)+" endTime:"+ DateTransUtils.stampToDate(endTime));
        PackageInfo packageInfo = pkgNameToPackageInfo.get(pkg);
        ArrayList<OneTimeDetails> mTimeDetailsList = packageInfo.getmTimeDetailsList();
        for (int i = 0;i < mTimeDetailsList.size();++i) {
            OneTimeDetails oneTimeDetails = mTimeDetailsList.get(i);
//            if (flag)
//                    Log.d("calculateUseTimeToday", "start_time:"+oneTimeDetails.getStartTime()+" end_time:"+oneTimeDetails.getStopTime());
            if (oneTimeDetails.getStartTime_long() >= endTime || oneTimeDetails.getStopTime_long() <= startTime) {
                continue;
            }
            else {
                long interval = DateTransUtils.getTimeDiffBetweenDays(DateTransUtils.stampToDate(max(startTime,oneTimeDetails.getStartTime_long())), DateTransUtils.stampToDate(min(endTime,oneTimeDetails.getStopTime_long())));

                useTime += interval;
//                if (flag) {
//                    Log.d("calculateUseTimeToday", "enter and interval is:"+interval);
//                    Log.d(TAG, "useTime:"+useTime);
//                }
            }
        }
        return useTime/1000;
    }

//    根据event以及时间段计算使用时间
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Integer> calculateUseTimeByHour(String pkg) {
        boolean flag = false;
        if (pkg.equals("com.miui.calculator")) {
            flag = true;
        }
        List<Integer> UsedTimeByHour = new LinkedList<>();
        PackageInfo packageInfo = pkgNameToPackageInfo.get(pkg);
        ArrayList<OneTimeDetails> mTimeDetailsList = packageInfo.getmTimeDetailsList();
//        if (flag == true)
//            Log.d(TAG, "pkg: "+pkg);
        for (int i = 0;i < 24;++i) {
            long allTime = 0;
            long startTime = DateTransUtils.getTodayStartStamp(i,0,0);
            long endTime = DateTransUtils.getTodayStartStamp(i+1,0,0);
//            if (flag)
//                Log.d("calculateUseTimeByHour", "pkgName:"+pkg+" startTime:"+DateTransUtils.stampToDate(startTime)+" endTime:"+ DateTransUtils.stampToDate(endTime));
            for (int j = 0; j < mTimeDetailsList.size(); ++j) {
                OneTimeDetails oneTimeDetails = mTimeDetailsList.get(j);
//                if (flag)
//                    Log.d("calculateUseTimeByHour", "start_time:"+oneTimeDetails.getStartTime()+" end_time:"+oneTimeDetails.getStopTime());
                    if (oneTimeDetails.getStartTime_long() >= endTime || oneTimeDetails.getStopTime_long() <= startTime) {
//                        Log.d("calculateUseTimeByHour", "break 1"+
//                                "start_long:"+oneTimeDetails.getStartTime_long()+
//                                " end_time:"+endTime+
//                                " stop_long:"+oneTimeDetails.getStopTime_long()+
//                                " start_time:"+startTime);
                        continue;
                    }
                    else {
                        long interval = DateTransUtils.getTimeDiffBetweenDays(DateTransUtils.stampToDate(max(startTime,oneTimeDetails.getStartTime_long())), DateTransUtils.stampToDate(min(endTime,oneTimeDetails.getStopTime_long())));
//                        if (flag)
//                            Log.d("calculateUseTimeByHour", "enter and interval is:"+interval);
                        allTime += interval;
                    }
                }
            UsedTimeByHour.add((int) allTime/1000);
        }
//        if (flag)
//        Log.d("UsedTimeByHour", "size:"+UsedTimeByHour.size());
//        for (int i = 0;i < UsedTimeByHour.size();++i) {
//           f if (flag)
//            Log.d("UsedTimeByHour", "i:"+i+" calculateUseTimeByHour: "+UsedTimeByHour.get(i));
//        }
        return UsedTimeByHour;
    }
    // =======================================
    // getter and setter
    // =======================================


    public int getmDayNum() {
        return mDayNum;
    }

    public void setmDayNum(int mDayNum) {
        this.mDayNum = mDayNum;
    }

    public ArrayList<OneTimeDetails> getmOneTimeDetailList() {
        return mOneTimeDetailList;
    }

    public OneTimeDetails getmOneTimeDetails() {
        return mOneTimeDetails;
    }

    public void setmOneTimeDetails(OneTimeDetails mOneTimeDetails) {
        this.mOneTimeDetails = mOneTimeDetails;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UsageStats getUsageStats(String pkg) {
        for (int i = 0; i < mStatsList.size(); i++) {
            if (mStatsList.get(i).getPackageName().equals(pkg)) {
                return mStatsList.get(i);
            }
        }
        return null;
    }
}