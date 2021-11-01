package com.appnext;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;


public class WidgetProvider extends AppWidgetProvider {

    private static final String REFRESH_BUTTON_CLICK = "com.nextapp.widget.refresh.CLICK";
    private static final String REFRESH_DONE = "com.nextapp.widget.refresh.DONE";
    private static final String APP1_BUTTON_CLICK = "com.nextapp.widget.app1.CLICK";

    private Bundle pkgInfo;

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (action.equals(REFRESH_BUTTON_CLICK)) {
            Intent refreshIntent = new Intent();
            refreshIntent.setClass(context, WidgetService.WidgetReceiver.class);
            refreshIntent.setAction(REFRESH_BUTTON_CLICK);
            context.sendBroadcast(refreshIntent);
        } else if (action.equals(REFRESH_DONE)) {
            pkgInfo = intent.getExtras();
        } else if (action.equals(APP1_BUTTON_CLICK)) {
            Intent click1Intent = new Intent();
            click1Intent.setClass(context, WidgetService.WidgetReceiver.class);
            click1Intent.setAction(APP1_BUTTON_CLICK);
            context.sendBroadcast(click1Intent);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

}