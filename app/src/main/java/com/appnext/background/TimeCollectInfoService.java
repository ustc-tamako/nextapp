package com.appnext.background;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.appnext.ItemDetailHostActivity;

import org.litepal.LitePal;

//定时收集app使用消息
public class TimeCollectInfoService extends Service {
    private UseTimeDataManager mUseTimeDataManager;
    private UsageInfoTask usageInfoTask;
    private static final String TAG = "TimeCollectInfoService";
    public TimeCollectInfoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
//        mUseTimeDataManager = (UseTimeDataManager) intent.getSerializableExtra("mUseTimeDataManager");
//        boolean x = mUseTimeDataManager == null;
//        Log.d(TAG, "onStartCommand: "+x);
        LitePal.getDatabase();
        usageInfoTask = new UsageInfoTask();
        usageInfoTask.execute(TimeCollectInfoService.this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}