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
    private static final String APP1_BUTTON_CLICK = "com.nextapp.widget.app1.CLICK";
    private static final String APP2_BUTTON_CLICK = "com.nextapp.widget.app2.CLICK";


    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (action.equals(REFRESH_BUTTON_CLICK)) {
            Intent refreshIntent = new Intent();
            refreshIntent.setClass(context, WidgetService.WidgetReceiver.class);
            refreshIntent.setAction(REFRESH_BUTTON_CLICK);
            context.sendBroadcast(refreshIntent);
        } else if (action.equals(APP1_BUTTON_CLICK)) {
            Intent clickInt = new Intent();
            clickInt.setClass(context, WidgetService.WidgetReceiver.class);
            clickInt.setAction(APP1_BUTTON_CLICK);
            context.sendBroadcast(clickInt);
        } else if (action.equals(APP2_BUTTON_CLICK)) {
            Intent clickInt = new Intent();
            clickInt.setClass(context, WidgetService.WidgetReceiver.class);
            clickInt.setAction(APP2_BUTTON_CLICK);
            context.sendBroadcast(clickInt);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);

        Intent refreshIntent = new Intent();
        refreshIntent.setClass(context, WidgetService.WidgetReceiver.class);
        refreshIntent.setAction(REFRESH_BUTTON_CLICK);
        context.sendBroadcast(refreshIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

}