package com.appnext.placeholder;

import android.util.Log;

import com.appnext.database.AppUsageInfoByAppName;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {



    /**
     * An array of sample (placeholder) items.
     */
    public static final List<AppUsageInfoByAppName> appUsageInfoByAppNames = LitePal.findAll(AppUsageInfoByAppName.class);
    public static  List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    private static final int COUNT = appUsageInfoByAppNames.size();

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT-1; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        AppUsageInfoByAppName appUsageInfoByAppName = appUsageInfoByAppNames.get(position);
        return new PlaceholderItem(String.valueOf(position),appUsageInfoByAppName.getAppName(), Integer.toString(appUsageInfoByAppName.getAllUsedTime()/60)+" mins",appUsageInfoByAppName.getUsedTimeByHour0(),appUsageInfoByAppName.getUsedTimeByHour1(),appUsageInfoByAppName.getUsedTimeByHour2(),appUsageInfoByAppName.getUsedTimeByHour3(),appUsageInfoByAppName.getUsedTimeByHour4(),appUsageInfoByAppName.getUsedTimeByHour5(),appUsageInfoByAppName.getUsedTimeByHour6(),appUsageInfoByAppName.getUsedTimeByHour7(),appUsageInfoByAppName.getUsedTimeByHour8(),appUsageInfoByAppName.getUsedTimeByHour9(),appUsageInfoByAppName.getUsedTimeByHour10(),appUsageInfoByAppName.getUsedTimeByHour11(),appUsageInfoByAppName.getUsedTimeByHour12(),appUsageInfoByAppName.getUsedTimeByHour13(),appUsageInfoByAppName.getUsedTimeByHour14(),appUsageInfoByAppName.getUsedTimeByHour15(),appUsageInfoByAppName.getUsedTimeByHour16(),appUsageInfoByAppName.getUsedTimeByHour17(),appUsageInfoByAppName.getUsedTimeByHour18(),appUsageInfoByAppName.getUsedTimeByHour19(),appUsageInfoByAppName.getUsedTimeByHour20(),appUsageInfoByAppName.getUsedTimeByHour21(),appUsageInfoByAppName.getUsedTimeByHour22(),appUsageInfoByAppName.getUsedTimeByHour23(),appUsageInfoByAppName.getPkgName(),appUsageInfoByAppName.getCategory(),appUsageInfoByAppName.getLastUsedTime());
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final String details;
        public final int []usedTimeByHour;
        public final String pkgname;
        public final int category;
        public final String lastused;

        public PlaceholderItem(String id, String content, String details, int usedTimeByHour0, int usedTimeByHour1, int usedTimeByHour2, int usedTimeByHour3, int usedTimeByHour4, int usedTimeByHour5, int usedTimeByHour6, int usedTimeByHour7, int usedTimeByHour8, int usedTimeByHour9, int usedTimeByHour10, int usedTimeByHour11, int usedTimeByHour12, int usedTimeByHour13, int usedTimeByHour14, int usedTimeByHour15, int usedTimeByHour16, int usedTimeByHour17, int usedTimeByHour18, int usedTimeByHour19, int usedTimeByHour20, int usedTimeByHour21, int usedTimeByHour22, int usedTimeByHour23,String pkgname,int category,String lastused) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.usedTimeByHour=new int[24];
            this.usedTimeByHour[0] = usedTimeByHour0;
            this.usedTimeByHour[1] = usedTimeByHour1;
            this.usedTimeByHour[2] = usedTimeByHour2;
            this.usedTimeByHour[3] = usedTimeByHour3;
            this.usedTimeByHour[4] = usedTimeByHour4;
            this.usedTimeByHour[5] = usedTimeByHour5;
            this.usedTimeByHour[6] = usedTimeByHour6;
            this.usedTimeByHour[7] = usedTimeByHour7;
            this.usedTimeByHour[8] = usedTimeByHour8;
            this.usedTimeByHour[9] = usedTimeByHour9;
            this.usedTimeByHour[10] = usedTimeByHour10;
            this.usedTimeByHour[11] = usedTimeByHour11;
            this.usedTimeByHour[12] = usedTimeByHour12;
            this.usedTimeByHour[13] = usedTimeByHour13;
            this.usedTimeByHour[14] = usedTimeByHour14;
            this.usedTimeByHour[15] = usedTimeByHour15;
            this.usedTimeByHour[16] = usedTimeByHour16;
            this.usedTimeByHour[17] = usedTimeByHour17;
            this.usedTimeByHour[18] = usedTimeByHour18;
            this.usedTimeByHour[19] = usedTimeByHour19;
            this.usedTimeByHour[20] = usedTimeByHour20;
            this.usedTimeByHour[21] = usedTimeByHour21;
            this.usedTimeByHour[22] = usedTimeByHour22;
            this.usedTimeByHour[23] = usedTimeByHour23;
            this.pkgname=pkgname;
            this.category=category;
            this.lastused=lastused;
        }


        @Override
        public String toString() {
            return content;
        }
    }
}