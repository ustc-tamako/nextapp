package com.appnext.tooluntils;

import com.appnext.database.AppUsageInfo;

import org.litepal.LitePal;

import java.util.LinkedList;
import java.util.List;

public class GetStartTime {
    public static List<Integer> StartTimeList = new LinkedList<>();

    public static final void getStartTimeByString() {
        List<AppUsageInfo> appUsageInfos = LitePal.findAll(AppUsageInfo.class);
        for (int i = 0;i < appUsageInfos.size();++i) {
            AppUsageInfo appUsageInfo = appUsageInfos.get(i);
            String hourMinute = DateTransUtils.getHourMinute(appUsageInfo.getStartTime());
            int hour = Integer.parseInt(hourMinute.substring(0,2));
            int minute = Integer.parseInt(hourMinute.substring(3,5));
            StartTimeList.add(hour*60+minute);
        }
    }
}
