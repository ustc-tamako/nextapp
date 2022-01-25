package com.appnext.database;

import org.litepal.crud.LitePalSupport;

//AppUsageInfo表格，记录每次app启动记录
public class AppUsageInfo extends LitePalSupport {
//mAppName表示app名称，mStartTime表示启动时间，mEndTime表示关闭时间，mTempAppRunningTime表示本次运行app时间，mAllAppRunningTime表示自从app开始运行之后的总运行时间（最后一个特征好像没啥用，删除吧）
    private String appName;
    private String pkgName;
    private String startTime;
    private String endTime;
    private long usedTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }
}
