package com.appnext;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;


public class WidgetService extends Service {

    private static final String REFRESH_BUTTON_CLICK = "com.nextapp.widget.refresh.CLICK";
    private static final String REFRESH_DONE = "com.nextapp.widget.refresh.DONE";
    private static final String APP1_BUTTON_CLICK = "com.nextapp.widget.app1.CLICK";

    static Bundle pkgInfo;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] idLs = manager.getAppWidgetIds(new ComponentName(getPackageName(), WidgetProvider.class.getName()));
        //用于遍历所有保存的widget的id
        for (int i : idLs) {
            int appID = i;
            //创建一个远程view，绑定我们要操控的widget布局文件
            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.app_widget);

            Intent refreshIntent = new Intent();
            refreshIntent.setClass(this, WidgetProvider.class);
            refreshIntent.setAction(REFRESH_BUTTON_CLICK);
            //为布局文件中的按钮设置点击监听
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.refresh_button, pendingIntent);

            Intent clickInt1 = new Intent();
            clickInt1.setClass(this, WidgetProvider.class);
            clickInt1.setAction(APP1_BUTTON_CLICK);
            //为布局文件中的按钮设置点击监听
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, clickInt1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.app1_icon, pendingIntent1);

//            Intent clickInt1 = new Intent();
//            clickInt1.setClassName(pkgInfo.getString("pkgName1"), pkgInfo.getString("className1"));
//            clickInt1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, clickInt1, 0);
//            remoteView.setOnClickPendingIntent(R.id.app1_icon, pendingIntent1);
//
//            Intent clickInt2 = new Intent();
//            clickInt2.setClassName(pkgInfo.getString("pkgName2"), pkgInfo.getString("className2"));
//            clickInt2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, clickInt2, 0);
//            remoteView.setOnClickPendingIntent(R.id.app2_icon, pendingIntent2);

            // 更新 widget
            manager.updateAppWidget(appID, remoteView);
        }

        return START_STICKY;
    }


    public static class WidgetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (pkgInfo == null) {
                pkgInfo = new Bundle();
                pkgInfo.putString("pkgName1", "com.android.contacts");
                pkgInfo.putString("className1", "com.android.contacts.activities.PeopleActivity");
                pkgInfo.putString("pkgName2", "com.android.settings");
                pkgInfo.putString("className2", "com.android.settings.Settings");
            }

            String action = intent.getAction();

            if (action.equals(REFRESH_BUTTON_CLICK)) {
                refresh(context);
            } else if (action.equals(APP1_BUTTON_CLICK)) {
                Intent clickInt1 = new Intent();
                clickInt1.setClassName(pkgInfo.getString("pkgName1"), pkgInfo.getString("className1"));
                clickInt1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(clickInt1);
            }
        }

        public void refresh(Context context) {

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            PackageManager pm = context.getPackageManager();

            pkgInfo = new Bundle();
            pkgInfo.putString("pkgName1", "com.android.vending");
            pkgInfo.putString("className1", "com.android.vending.AssetBrowserActivity");

            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(pkgInfo.getString("pkgName1"), PackageManager.GET_META_DATA);
                Resources resources = pm.getResourcesForApplication(appInfo);
                int appIconResId = appInfo.icon;
                Bitmap appIconBitMap = BitmapFactory.decodeResource(resources, appIconResId);
                remoteView.setImageViewBitmap(R.id.app1_icon, appIconBitMap);
                manager.updateAppWidget(new ComponentName(context, WidgetProvider.class), remoteView);

                Intent doneIntent = new Intent();
                doneIntent.setClass(context, WidgetProvider.class);
                doneIntent.setAction(REFRESH_DONE);
                doneIntent.putExtras(pkgInfo);
                context.sendBroadcast(doneIntent);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}