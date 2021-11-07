package com.appnext.background;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计数据---记录每个应用的包名，使用时长和使用次数
 * <p>
 *
 */

public class PackageInfo {

    private int mUsedCount;
    private long mUsedTime; //保存今天app总使用时间，不要初始化，以秒为单位
    private String mPackageName;
    private String mAppName;
    private String mStartTime;
    private String mEndTime;
    private List<Integer> mOpenTime; //保存每个时间段app使用时间,不要初始化，以秒为单位
    private ArrayList<OneTimeDetails> mTimeDetailsList; //保存该app启动的所有事件
    private byte[] drawable; //保存app对应的图标
    private int category;

    public PackageInfo(int mUsedCount, long mUsedTime, String mPackageName,String appName,String mStartTime,String mEndTime,List<Integer> mOpenTime,ArrayList<OneTimeDetails> mTimeDetailsList) {
        this.mUsedCount = mUsedCount;
        this.mUsedTime = mUsedTime;
        this.mPackageName = mPackageName;
        this.mAppName = appName;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mOpenTime = mOpenTime;
        this.mTimeDetailsList = mTimeDetailsList;
    }

    public void addCount() {
        mUsedCount++;
    }

    public int getmUsedCount() {
        return mUsedCount;
    }

    public void setmUsedCount(int mUsedCount) {
        this.mUsedCount = mUsedCount;
    }

    public String getmStartTime() {
        return mStartTime;
    }

    public List<Integer> getmOpenTime() {
        return mOpenTime;
    }

    public void setmOpenTime(List<Integer> mOpenTime) {
        this.mOpenTime = mOpenTime;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public long getmUsedTime() {
        return mUsedTime;
    }

    public void setmUsedTime(long mUsedTime) {
        this.mUsedTime = mUsedTime;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public ArrayList<OneTimeDetails> getmTimeDetailsList() {
        return mTimeDetailsList;
    }

    public void setmTimeDetailsList(ArrayList<OneTimeDetails> mTimeDetailsList) {
        this.mTimeDetailsList = mTimeDetailsList;
    }

    public byte[] getDrawable() {
        return drawable;
    }

    public void setDrawable(byte[] drawable) {
        this.drawable = drawable;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        //return super.equals(o);
        if (o == null) return false;
        if (this == o) return true;
        PackageInfo standardDetail = (PackageInfo) o;
        if (standardDetail.getmPackageName().equals(this.mPackageName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return (mPackageName + mUsedTime).hashCode();
    }

    @Override
    public String toString() {
        return "PackageInfo{" +
                "mUsedCount=" + mUsedCount +
                ", mUsedTime=" + mUsedTime +
                ", mPackageName='" + mPackageName + '\'' +
                ", mAppName='" + mAppName + '\'' +
                '}';
    }
}