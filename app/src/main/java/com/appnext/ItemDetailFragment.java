package com.appnext;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appnext.tooluntils.ApknameMap;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.appnext.placeholder.PlaceholderContent;
import com.appnext.databinding.FragmentItemDetailBinding;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;

import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.anychart.core.cartesian.series.RangeColumn;
import com.appnext.database.AppUsageInfo;

import org.litepal.LitePal;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The placeholder content this fragment is presenting.
     */
    private PlaceholderContent.PlaceholderItem mItem;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mTextView;
    private TextView mtimeView;
    private ListView listView;
    private TextView mavView;
    private TextView mnotifiView;
    private ImageView mView;
    private TextView category;
    private TextView lastuse;
    public static final List<AppUsageInfo> appUsageInfo = LitePal.findAll(AppUsageInfo.class);
    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
            mItem = PlaceholderContent.ITEM_MAP.get(clipDataItem.getText().toString());
            updateContent();
        }
        return true;
    };
    private FragmentItemDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PlaceholderContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        mnotifiView=binding.Notification;
        mavView=binding.Daverage;
        mView=binding.imagev;
        int count=0;
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (AppUsageInfo usageInfo : appUsageInfo) {
            if(usageInfo.getStartTime().substring(0,10).equals(sdf.format(d)))
            {
                if (usageInfo.getAppName().equals(mItem.content)){
                    count++;
                }
            }

        }
        String[] useTimeByAppName=new String[count];
        int index=0;
        for (AppUsageInfo usageInfo : appUsageInfo) {

            if(usageInfo.getStartTime().substring(0,10).equals(sdf.format(d)))
            {
                if(usageInfo.getAppName().equals(mItem.content)){
                    String name=usageInfo.getUsedTime()/60+"m "+usageInfo.getUsedTime()%60+"s";
                    name=String.format("%36s",name);
                    String time=usageInfo.getStartTime().substring(11,16)+
                            " - "+usageInfo.getEndTime().substring(11,16);
                    time=String.format("%-15s",time);
                    useTimeByAppName[index++]=time+name;

                }
            }

        }
        mnotifiView.setText(Integer.toString(count));
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.activity_listview, useTimeByAppName);

        listView = binding.countryList;
        listView.setAdapter(adapter);


//        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
          mTextView = binding.appnameView;
          mtimeView=binding.apptime;
          mavView=binding.Daverage;
          category=binding.category;
          lastuse=binding.lastused;
          category.setText(ApknameMap.CategoryMap[mItem.category+1]);
          lastuse.setText(mItem.lastused);


        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
//        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnyChartView columnChart = binding.columnView;
        setupColumnView(columnChart);

    }

    private void setupColumnView(AnyChartView columnView) {
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            data.add(new ValueDataEntry(String.format("%dh", i),mItem.usedTimeByHour[i]/60));
        }

        Column column = cartesian.column(data);

//        column.tooltip()
//                .titleFormat("{%X}")
//                .position(Position.CENTER_BOTTOM)
//                .anchor(Anchor.CENTER_BOTTOM)
//                .offsetX(0d)
//                .offsetY(5d)
//                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);

        cartesian.yScale().minimum(0d);
        cartesian.yScale().maximum(60d);
        cartesian.yAxis(0).labels().format("{%Value}m");

//        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
//        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        columnView.setChart(cartesian);
    }

    private void setupRangeView(AnyChartView rangeView) {
        Cartesian cartesian = AnyChart.cartesian();

        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomDataEntry("Jan",6.1, 8.9));
        data.add(new CustomDataEntry("Jan", 9.1, 10.4));
        data.add(new CustomDataEntry("Feb", 4.6, 6.1));
        data.add(new CustomDataEntry("Mar", 5.9, 8.1));
        data.add(new CustomDataEntry("Apr", 7.8, 10.7));
        data.add(new CustomDataEntry("May", 10.5, 13.7));
        data.add(new CustomDataEntry("June", 13.8, 17));
        data.add(new CustomDataEntry("July", 16.5, 18.5));
        data.add(new CustomDataEntry("Aug", 17.8, 19));
        data.add(new CustomDataEntry("Sep", 15.4, 17.8));
        data.add(new CustomDataEntry("Oct", 12.7, 15.3));
        data.add(new CustomDataEntry("Nov", 9.8, 13));
        data.add(new CustomDataEntry("Dec", 9, 10.1));

//        Set set = Set.instantiate();
//        set.data(data);
//        Mapping londonData = set.mapAs("{ x: 'x', high: 'londonHigh', low: 'londonLow' }");

        RangeColumn columnLondon = cartesian.rangeColumn(data);
        columnLondon.name("London");

        cartesian.xAxis(true);
        cartesian.yAxis(true);

        cartesian.yScale()
                .minimum(4d)
                .maximum(20d);

        cartesian.legend(true);

        cartesian.yGrid(true)
                .yMinorGrid(true);

        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})");

        rangeView.setChart(cartesian);
    }

    private void setupScatterView(AnyChartView scatterView) {
        Scatter scatter = AnyChart.scatter();

//        scatter.animation(true);

        scatter.xScale()
                .minimum(0d)
                .maximum(24d);
//        scatter.xScale().tick
        scatter.yScale()
                .minimum(0d)
                .maximum(60d);

        scatter.yGrid(true);

        scatter.yAxis(0).labels().format("{%Value}m");
        scatter.xAxis(0).drawFirstLabel(false).labels().format("{%Value}h");


        scatter.interactivity()
                .hoverMode(HoverMode.BY_SPOT)
                .spotRadius(0d);

//        scatter.tooltip().displayMode(TooltipDisplayMode.UNION);

        Marker marker = scatter.marker(getMarkerData());
        marker.type(MarkerType.CIRCLE)
                .size(2);

        marker.selected()
                .size(2);

//        marker.hovered()
//                .size(30d)
//                .fill(new SolidFill("gold", 1d))
//                .stroke("anychart.color.darken(gold)");

//        marker.tooltip()
//                .hAlign(HAlign.START)
//                .format("Waiting time: ${%Value} min.\\nDuration: ${%X} min.");

        scatterView.setChart(scatter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContent() {
        if (mItem != null) {
            mTextView.setText(mItem.content);
            mView.setImageBitmap(WidgetService.WidgetReceiver.drawableToBitmap(WidgetService.WidgetReceiver.getIconFromPackageName(mItem.pkgname, getActivity())));
            int time=Integer.parseInt(mItem.details.substring(0,mItem.details.length()-5));
            if(time>60)
            {
                if(time%60==0)
                {
                    mtimeView.setText(time/60+"h");
                }
                mtimeView.setText(time/60+"h "+time%60+"m");
            }
            else
            {
                mtimeView.setText(time+"m");
            }
            mavView.setText(time/24+"m");
            if (mToolbarLayout != null) {
                mToolbarLayout.setTitle(mItem.content);
            }
        }
    }

    private class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String x, Number londonHigh, Number londonLow) {
            setValue("x", x);
            setValue("high", londonHigh);
            setValue("low", londonLow);
        }
    }

    private List<DataEntry> getMarkerData() {
        List<DataEntry> data = new ArrayList<>();

        data.add(new ValueDataEntry(4, 20));
        data.add(new ValueDataEntry(3, 7));
        data.add(new ValueDataEntry(4, 5));
        data.add(new ValueDataEntry(4, 15));
        data.add(new ValueDataEntry(3, 8));
        data.add(new ValueDataEntry(4, 10));
        data.add(new ValueDataEntry(2, 5));
        data.add(new ValueDataEntry(4, 33));
        data.add(new ValueDataEntry(1, 5));
        data.add(new ValueDataEntry(4, 40));
        data.add(new ValueDataEntry(1, 5));
        data.add(new ValueDataEntry(4, 54));

        return data;
    }
}