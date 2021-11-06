package com.appnext.tooluntils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.appnext.background.UseTimeDataManager;
import com.appnext.database.AppInfo;
import com.appnext.database.AppUsageInfo;
import com.appnext.database.AppUsageInfoByAppName;

import org.checkerframework.checker.units.qual.A;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetAppInfo {

//    根据AppUsageInfoByAppName表格转化的序号
    public static final void getAppInfoFromUsageInfo() {
        String TAG = "GetAppInfo";
        List<AppUsageInfo> appUsageInfos = LitePal.findAll(AppUsageInfo.class);
        int sub = 0;
        Log.d(TAG, "appUsageInfo.size:"+appUsageInfos.size()+"\nenter: ");
        for (int i = 0;i < appUsageInfos.size();++i) {
            AppUsageInfo appUsageInfo  = appUsageInfos.get(i);
            AppInfo appInfo = new AppInfo();
            if (LitePal
                    .where("pkgName = ?",appUsageInfo.getPkgName()).
                            findFirst(AppInfo.class) == null) {
                Log.d(TAG, "pkgNmae:"+appUsageInfo.getPkgName());
                appInfo.setPkgName(appUsageInfo.getPkgName());
                appInfo.setAppName(appUsageInfo.getAppName());
                appInfo.setNumber(sub++);
                appInfo.save();
            }
        }
        Log.d(TAG, "break");
    }

    public static final void getAppInfoToDatabase(Context context) {
        List<ApplicationInfo> apps = queryFilterAppInfo(context);
        for (int i = 0; i < apps.size(); i++) {
            ApplicationInfo app = apps.get(i);
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(getApplicationNameByPackageName(context,app.packageName));
            appInfo.setNumber(i);
            appInfo.setPkgName(app.packageName);
//            appInfo.setTheme(app.theme);
            appInfo.save();
        }
    }

        private static final List<ApplicationInfo> queryFilterAppInfo(Context context) {
            PackageManager pm = context.getPackageManager();
            // 查询全部已经安装的应用程序
            List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES表明已删除，但还有安装目录的
            List<ApplicationInfo> applicationInfos = new ArrayList<>();

            // 建立一个类别为CATEGORY_LAUNCHER的该包名的Intent
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 经过getPackageManager()的queryIntentActivities方法遍历,获得全部能打开的app的packageName
            List<ResolveInfo> resolveinfoList = context.getPackageManager()
                    .queryIntentActivities(resolveIntent, 0);
            Set<String> allowPackages = new HashSet();
            for (ResolveInfo resolveInfo : resolveinfoList) {
                allowPackages.add(resolveInfo.activityInfo.packageName);
            }

            for (ApplicationInfo app : appInfos) {
                if (!UseTimeDataManager.isSystemApp(context,app.packageName)) {
                    applicationInfos.add(app);
                }
            }
            return applicationInfos;
        }

    public static final String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }
}
