package com.appnext.tooluntils;

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
import android.util.Log;

public class IconUtils {

    public static final Drawable getAppIcon(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return info.loadIcon(pm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Bitmap drawableToBitmap(Drawable drawable, String pkgName) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Log.i("Utilities",
                "drawableToBitmap drawable.getIntrinsicWidth()=" + drawable.getIntrinsicWidth()
                        + ",drawable.getIntrinsicHeight()="
                        + drawable.getIntrinsicHeight()
                        + " "+pkgName);
//        drawable.draw(canvas);
        return bitmap;
    }

    public static final Bitmap drawableToBitmap2(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return ((BitmapDrawable) drawable).getBitmap();
        }
    }
}
