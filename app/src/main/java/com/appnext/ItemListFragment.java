package com.appnext;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContentInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.appnext.database.AppUsageInfoByAppName;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.appnext.databinding.FragmentItemListBinding;
import com.appnext.databinding.ItemListContentBinding;

import com.appnext.placeholder.PlaceholderContent;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Polar;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.PolarSeriesType;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.ScaleTypes;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.scales.Linear;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * A fragment representing a list of Items. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ItemListFragment";
    public  List<AppUsageInfoByAppName> appUsageInfoByAppNames = LitePal.findAll(AppUsageInfoByAppName.class);
    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentItemListBinding binding;
    private MaterialButton weekly_button;
    private MaterialButton daily_button;
    private TextView dateText;
    private TextView timeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        weekly_button = binding.weeklyButton;
        weekly_button.setOnClickListener(this);
        daily_button = binding.dailyButton;
        daily_button.setOnClickListener(this);
        daily_button.setSelected(true);
        dateText=binding.dateText;
        timeText=binding.timeText;

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        AnyChartView pieView = binding.pieView;
        setupPieView(pieView);

        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            PlaceholderContent.PlaceholderItem item =
                    (PlaceholderContent.PlaceholderItem) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            PlaceholderContent.PlaceholderItem item =
                    (PlaceholderContent.PlaceholderItem) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener);
    }

    private void setupPieView(AnyChartView pieView) {
        Polar polar = AnyChart.polar();

        List<DataEntry> data = new ArrayList<>();

        for(int i=0;i<=11;i++)
        {
            int sum=0;
            for(int j=0;j<appUsageInfoByAppNames.size();j++)
            {
                sum=sum+appUsageInfoByAppNames.get(j).getUsedTimeByHour0();
            }

        }
        int sum0=0;
        int sum1=0;
        int sum2=0;
        int sum3=0;
        int sum4=0;
        int sum5=0;
        int sum6=0;
        int sum7=0;
        int sum8=0;
        int sum9=0;
        int sum10=0;
        int sum11=0;
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum0=sum0+appUsageInfoByAppNames.get(j).getUsedTimeByHour0();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum1=sum1+appUsageInfoByAppNames.get(j).getUsedTimeByHour1();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum2=sum2+appUsageInfoByAppNames.get(j).getUsedTimeByHour2();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum3=sum3+appUsageInfoByAppNames.get(j).getUsedTimeByHour3();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum4=sum4+appUsageInfoByAppNames.get(j).getUsedTimeByHour4();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum5=sum5+appUsageInfoByAppNames.get(j).getUsedTimeByHour5();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum6=sum6+appUsageInfoByAppNames.get(j).getUsedTimeByHour6();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum7=sum7+appUsageInfoByAppNames.get(j).getUsedTimeByHour7();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum8=sum8+appUsageInfoByAppNames.get(j).getUsedTimeByHour8();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum9=sum9+appUsageInfoByAppNames.get(j).getUsedTimeByHour9();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum10=sum10+appUsageInfoByAppNames.get(j).getUsedTimeByHour10();
        }
        for(int j=0;j<appUsageInfoByAppNames.size();j++)
        {
            sum11=sum11+appUsageInfoByAppNames.get(j).getUsedTimeByHour11();
        }
        System.out.println(sum0+sum1+sum10);

        data.add(new CustomDataEntry("0", sum0,0,0));
        data.add(new CustomDataEntry("1", sum1, 0, 0));
        data.add(new CustomDataEntry("2", sum2, 0, 0));
        data.add(new CustomDataEntry("3", sum3, 0, 0));
        data.add(new CustomDataEntry("4", sum4, 0, 0));
        data.add(new CustomDataEntry("5", sum5, 0, 0));
        data.add(new CustomDataEntry("6", sum6, 0, 0));
        data.add(new CustomDataEntry("7", sum7, 0, 0));
        data.add(new CustomDataEntry("8", sum8, 0, 0));
        data.add(new CustomDataEntry("9", sum9, 0, 0));
        data.add(new CustomDataEntry("10", sum10, 0, 0));
        data.add(new CustomDataEntry("11", sum11, 0, 0));

        Set set = Set.instantiate();
        set.data(data);

            Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
            polar.column(series1Data);

//        polar.title("APP Usage Time");

        polar.sortPointsByX(true)
                .defaultSeriesType(PolarSeriesType.COLUMN)
                .yAxis(false)
                .xScale(ScaleTypes.ORDINAL);

        polar.title().margin().bottom(20d);
        ((Linear) polar.yScale(Linear.class)).stackMode(ScaleStackMode.VALUE);
       polar.tooltip()
                .displayMode(TooltipDisplayMode.SEPARATED);

        pieView.setChart(polar);

    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                PlaceholderContent.ITEMS,
                onClickListener,
                onContextClickListener
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weekly_button:
                weekly_button.setSelected(true);
                daily_button.setSelected(false);
                binding.dateText.setText("Daily");
                binding.timeText.setText("2h 25m");
                break;
            case R.id.daily_button:
                String[] sss={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                weekly_button.setSelected(false);
                daily_button.setSelected(true);
                Calendar now = Calendar.getInstance();
                binding.dateText.setText(sss[now.get(Calendar.MONTH)]+" "+now.get(Calendar.DAY_OF_MONTH));
                int sum=0;
                for (PlaceholderContent.PlaceholderItem mValue : SimpleItemRecyclerViewAdapter.mValues) {
                    sum=sum+Integer.parseInt(mValue.details.substring(0,mValue.details.length()-5));
                }
                int h=sum/60;
                int min=sum%60;
                binding.timeText.setText(h+"h "+min+"m");
                break;
        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        public static List<PlaceholderContent.PlaceholderItem> mValues;
        private final View.OnClickListener mOnClickListener;
        private final View.OnContextClickListener mOnContextClickListener;

        SimpleItemRecyclerViewAdapter(List<PlaceholderContent.PlaceholderItem> items,
                                      View.OnClickListener onClickListener,
                                      View.OnContextClickListener onContextClickListener) {
            Collections.sort(items,new Comparator<PlaceholderContent.PlaceholderItem>() {
                @Override
                public int compare(PlaceholderContent.PlaceholderItem a, PlaceholderContent.PlaceholderItem b) {  //
                    return Integer.parseInt(b.details.substring(0,b.details.length()-5)) - Integer.parseInt(a.details.substring(0,a.details.length()-5));
                }
            });


            mValues = items;
            mOnClickListener = onClickListener;
            mOnContextClickListener = onContextClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
            holder.itemView.setTag(mValues.get(position));
            holder.imageView.setImageBitmap(WidgetService.WidgetReceiver.drawableToBitmap(WidgetService.WidgetReceiver.getIconFromPackageName(mValues.get(position).pkgname, holder.imageView.getContext())));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mIdView.setText(mValues.get(position).content);
            holder.mContentView.setText(mValues.get(position).details);
            holder.mBur.setProgress(Integer.parseInt(mValues.get(position).details.substring(0,mValues.get(position).details.length()-5)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setOnContextClickListener(mOnContextClickListener);
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).id);
                ClipData dragData = new ClipData(
                        ((PlaceholderContent.PlaceholderItem) v.getTag()).content,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
           final TextView mIdView;
           final TextView mContentView;
           final NumberProgressBar mBur;
           final ImageView imageView;
            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
            mIdView = binding.appText;
            mContentView=binding.timeText;
            mBur=binding.timeBar;
            imageView=binding.iconImg;
//                mContentView = binding.content;
            }

        }
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }

}