package com.appnext.background;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.os.Build;

import com.appnext.tooluntils.DateTransUtils;

import java.util.ArrayList;

/**
 *
 *
 *
 */

public class OneTimeDetails {
    private String pkgName;
    private long useTime;
    private ArrayList<UsageEvents.Event> OneTimeDetailEventList;

    public OneTimeDetails(String pkg, long useTime, ArrayList<UsageEvents.Event> oneTimeDetailList) {
        this.pkgName = pkg;
        this.useTime = useTime;
        OneTimeDetailEventList = oneTimeDetailList;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public ArrayList<UsageEvents.Event> getOneTimeDetailEventList() {
        return OneTimeDetailEventList;
    }

    public void setOneTimeDetailEventList(ArrayList<UsageEvents.Event> oneTimeDetailEventList) {
        OneTimeDetailEventList = oneTimeDetailEventList;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getStartTime(){
        String startTime = null;
        if(OneTimeDetailEventList.size() > 0){
            //startTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(0).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
            startTime = DateTransUtils.stampToDate(OneTimeDetailEventList.get(0).getTimeStamp());
        }
        return startTime;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getStopTime(){
        String stopTime = null;
        if(OneTimeDetailEventList.size() > 0){
            //stopTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(OneTimeDetailEventList.size()-1).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
            stopTime = DateTransUtils.stampToDate(OneTimeDetailEventList.get(OneTimeDetailEventList.size()-1).getTimeStamp());
        }
        return stopTime;
    }

    public long getStartTime_long() {
        long startTime = 0;
        if (OneTimeDetailEventList.size() > 0) {
            startTime = OneTimeDetailEventList.get(0).getTimeStamp();
        }
        return startTime;
    }

    public long getStopTime_long() {
        long stopTime = 0;
        if (OneTimeDetailEventList.size() > 0) {
            stopTime = OneTimeDetailEventList.get(OneTimeDetailEventList.size()-1).getTimeStamp();
        }
        return stopTime;
    }
}