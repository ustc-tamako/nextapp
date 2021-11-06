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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.appnext.ml.Model;
import com.appnext.KNeighborsClassifier;
import com.appnext.tooluntils.*;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;
import java.util.Calendar;


public class WidgetService extends Service {

    private static final String REFRESH_BUTTON_CLICK = "com.nextapp.widget.refresh.CLICK";
    private static final String APP1_BUTTON_CLICK = "com.nextapp.widget.app1.CLICK";
    private static final String APP2_BUTTON_CLICK = "com.nextapp.widget.app2.CLICK";

    public static Bundle pkgInfo;

    public static float[] appSeq;
    public static byte[] appSeqByte;
    public static double[][] timeSeq;

    public static KNeighborsClassifier clf;


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
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.refresh_button, pendingIntent);

            Intent clickInt1 = new Intent();
            clickInt1.setClass(this, WidgetProvider.class);
            clickInt1.setAction(APP1_BUTTON_CLICK);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, clickInt1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.app1_icon, pendingIntent1);

            Intent clickInt2 = new Intent();
            clickInt2.setClass(this, WidgetProvider.class);
            clickInt2.setAction(APP2_BUTTON_CLICK);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, clickInt2, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.app2_icon, pendingIntent2);

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
                String className1 = doStartApplicationWithPackageName(context, pkgInfo.getString("pkgName1"));
                Intent clickInt = new Intent();
                clickInt.setClassName(pkgInfo.getString("pkgName1"), className1);
                clickInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(clickInt);
            } else if (action.equals(APP2_BUTTON_CLICK)) {
                String className2 = doStartApplicationWithPackageName(context, pkgInfo.getString("pkgName2"));
                Intent clickInt = new Intent();
                clickInt.setClassName(pkgInfo.getString("pkgName2"), className2);
                clickInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(clickInt);
            }
        }


        public static int getIndexOfLargest(float[] array )
        {
            if ( array == null || array.length == 0 ) return -1; // null or empty

            int largest = 0;
            for ( int i = 1; i < array.length; i++ )
            {
                if ( array[i] > array[largest] ) largest = i;
            }
            return largest; // position of the first largest found
        }

        public static Drawable getIconFromPackageName(String packageName, Context context)
        {
            PackageManager pm = context.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            {
                try
                {
                    PackageInfo pi = pm.getPackageInfo(packageName, 0);
                    Context otherAppCtx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);

                    int displayMetrics[] = {DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_TV};

                    for (int displayMetric : displayMetrics)
                    {
                        try
                        {
                            Drawable d = otherAppCtx.getResources().getDrawableForDensity(pi.applicationInfo.icon, displayMetric);
                            if (d != null)
                            {
                                return d;
                            }
                        }
                        catch (Resources.NotFoundException e)
                        {
//                      Log.d(TAG, "NameNotFound for" + packageName + " @ density: " + displayMetric);
                            continue;
                        }
                    }

                }
                catch (Exception e)
                {
                    // Handle Error here
                }
            }

            ApplicationInfo appInfo = null;
            try
            {
                appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                return null;
            }

            return appInfo.loadIcon(pm);
        }

        public static Bitmap drawableToBitmap (Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        private String doStartApplicationWithPackageName(Context context, String packagename) {

            // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
            PackageInfo packageinfo = null;
            try {
                packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageinfo == null) {
                return null;
            }

            // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageinfo.packageName);

            // 通过getPackageManager()的queryIntentActivities方法遍历
            List<ResolveInfo> resolveinfoList = context.getPackageManager()
                    .queryIntentActivities(resolveIntent, 0);
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null) {
                // packagename = 参数packname
                String packageName = resolveinfo.activityInfo.packageName;
                // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
                String className = resolveinfo.activityInfo.name;
                return className;
            }
            return null;
        }


        public static void refresh(Context context) {

            int len = GetStartTime.StartTimeList.size();

            appSeq = new float[10];
            for (int i = 0; i < 10; ++i) {
                appSeq[i] = GetStartTime.appIdList.get(len-10+i).floatValue();
            }

            if (timeSeq == null) {
                timeSeq = new double[len][1];
                for (int i = 0; i < len; ++i) {
                    timeSeq[i][0] = Double.valueOf(GetStartTime.StartTimeList.get(i));
                }
                appSeqByte = new byte[len];
                for (int i = 0; i < len; ++i) {
                    appSeqByte[i] = GetStartTime.appIdList.get(i).byteValue();
                }
                clf = new KNeighborsClassifier(10, 36, 2, timeSeq, appSeqByte);
            }

            pkgInfo = new Bundle();

            // predict app1
            Date date = Calendar.getInstance().getTime();

            double[] curTime = {date.getHours() * 60 + date.getMinutes()};
            // Prediction:
            int app1_id = clf.predict(curTime);

            pkgInfo.putString("pkgName1", ApknameMap.NumberToApkname.get(app1_id));

            int app2_id;
            // predict app2
            try {

                appSeq = new float[]{0, 1, 2, 1, 1, 2, 1, 0, 3, 2};

                Model model = Model.newInstance(context);

                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 * 10 * 1 * 4);
                byteBuffer.order(ByteOrder.nativeOrder());
                for (int i = 0; i < 10; i++) {
                    byteBuffer.putFloat(appSeq[i]);
                }
                byteBuffer.rewind();

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 10, 1}, DataType.FLOAT32);
                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                float[] probability = outputFeature0.getFloatArray();
                app2_id = getIndexOfLargest(probability);

                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                Log.e("tflite error", e.toString());
                app2_id = 0;
            }

            pkgInfo.putString("pkgName2", ApknameMap.NumberToApkname.get(app2_id));

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            PackageManager pm = context.getPackageManager();

            Drawable appIconDrawable1 = getIconFromPackageName(pkgInfo.getString("pkgName1"), context);
            Bitmap appIconBitMap1 = drawableToBitmap(appIconDrawable1);
            remoteView.setImageViewBitmap(R.id.app1_icon, appIconBitMap1);
            remoteView.setTextViewText(R.id.app1_name, ApknameMap.NumberToApkname.get(app1_id));
            remoteView.setTextViewText(R.id.app1_desc, String.format("常在 %d:%d 打开", date.getHours(), date.getMinutes()));

            Drawable appIconDrawable2 = getIconFromPackageName(pkgInfo.getString("pkgName2"), context);
            Bitmap appIconBitMap2 = drawableToBitmap(appIconDrawable2);
            remoteView.setImageViewBitmap(R.id.app2_icon, appIconBitMap2);
            remoteView.setTextViewText(R.id.app2_name, ApknameMap.NumberToApkname.get(app2_id));
            int lastAppId = GetStartTime.appIdList.get(len-1);
            remoteView.setTextViewText(R.id.app2_desc, String.format("常在使用%s后打开", ApknameMap.NumberToApkname.get(lastAppId)));

            manager.updateAppWidget(new ComponentName(context, WidgetProvider.class), remoteView);

        }
    }
}