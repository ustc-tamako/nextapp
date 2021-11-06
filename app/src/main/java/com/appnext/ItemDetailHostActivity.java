package com.appnext;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.appnext.background.TimeCollectInfoService;
import com.appnext.database.AppInfo;
import com.appnext.database.AppUsageInfo;
import com.appnext.database.AppUsageInfoByAppName;
import com.appnext.database.TimeSequence;
import com.appnext.databinding.ActivityItemDetailBinding;
import com.appnext.tooluntils.ApknameMap;
import com.appnext.tooluntils.DateTransUtils;
import com.appnext.tooluntils.GetAppInfo;
import com.appnext.tooluntils.GetStartTime;

import org.litepal.LitePal;

import java.util.List;

public class ItemDetailHostActivity extends AppCompatActivity{

    private static final String TAG = "ItemDetailHostActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        open permission windows
        if (isNoOption() == true) {
            if (isNoSwitch() == false) {
                Intent intent = new Intent(
                        Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
//        open service to collect data
//        Intent openIntent = new Intent(this, TimeCollectInfoService.class);
//        startService(openIntent);
//        stopService(openIntent

//          获取app使用序列对应的序号值
//        AppInfo appInfo = LitePal.findFirst(AppInfo.class);
//        boolean isNull = appInfo == null;
//        if (isNull == true) {
//            GetAppInfo.getAppInfoFromUsageInfo();
//        }
//        ApknameMap.createMap(); //创建双向map
//        List<AppUsageInfo> appUsageInfos = LitePal.findAll(AppUsageInfo.class);
//        for (int i = 0;i < appUsageInfos.size();++i) {
//            TimeSequence timeSequence = new TimeSequence();
//            timeSequence.setNumber(ApknameMap.ApknameToNumber.get(appUsageInfos.get(i).getPkgName()));
//            timeSequence.save();
//        }



        List<AppUsageInfoByAppName> appUsageInfoByAppNames = LitePal.findAll(AppUsageInfoByAppName.class);




        Log.d(TAG, "appUsageInfoByAppNames size:"+appUsageInfoByAppNames.size());
        for (int i = 0;i < appUsageInfoByAppNames.size();++i) {
            AppUsageInfoByAppName appUsageInfoByAppName = appUsageInfoByAppNames.get(i);
            Log.d(TAG, "appname:"+appUsageInfoByAppName.getAppName()+
                    " pkgname:"+appUsageInfoByAppName.getPkgName()+
                    " usedtime:"+appUsageInfoByAppName.getAllUsedTime()+
                    " name"+appUsageInfoByAppName.getName());
            if (appUsageInfoByAppName.getName() != null) {
                byte[] icon = appUsageInfoByAppName.getName().getBytes();
                Log.d(TAG, "icon:"+icon);
                Bitmap bmpout = BitmapFactory.decodeByteArray(icon, 0, icon.length);
                Drawable bd= new BitmapDrawable(getResources(), bmpout);
                Log.d(TAG, "Drawable:"+bd);
            }
        }



        ActivityItemDetailBinding binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_item_detail);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_item_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    //判断当前设备中是否有"有权查看使用情况的应用程序"这个选项
    private boolean isNoOption() {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }
}