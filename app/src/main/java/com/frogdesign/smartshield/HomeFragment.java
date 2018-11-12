package com.frogdesign.smartshield;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private List<String> names = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    void initDevices() {
        for (int i = 0; i < 10; ++i) {
            names.add("MacBook Pro" + i);
            imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2mWFoSbenRKHNP8Akv75PTExe88EmDMLDuuv1HNkTION4pGadOw");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);

//        String strtext = getArguments().getString("traffic");
//        TextView test = view.findViewById(R.id.test_seg);
//        test.setText(strtext);

        TextView textView =view.findViewById(R.id.learn_more);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://smartshield.squarespace.com/'> learn more </a>";
        textView.setText(Html.fromHtml(text));

        initDevices();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_devices);
        recyclerView.setLayoutManager(layoutManager);
        DevicesRecyclerViewAdapter adapter = new DevicesRecyclerViewAdapter(view.getContext(), names, imageUrls);
        recyclerView.setAdapter(adapter);



        // Line chart
        LineChart mLineChart = view.findViewById(R.id.chart);

        final int deviceNumber = 3;
        final int dataSize = 10;

        String[] colors = {"#33CCCC", "#99CC00", "#FFCC00"};

        List<ILineDataSet> dataSets = new ArrayList<>();
        for (int k = 0; k < deviceNumber; ++k) {
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < dataSize; ++i) {
                entries.add(new Entry(i, 1000f + (float)k * 200f + (float)i * (i % 2 == 0 ? 200f : -200f)));
            }
            LineDataSet dataSet = new LineDataSet(entries, "MacBook Pro " + k);
            dataSet.setCircleRadius(1);
            dataSet.setColor(Color.parseColor(colors[k]));
            dataSet.setCircleColor(Color.parseColor(colors[k]));
            dataSet.setValueTextSize(0);
            dataSet.setDrawFilled(true);
            dataSets.add(dataSet);
        }

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[10];
        for (int i = 0; i < 10; ++i) {
            quarters[i] = "10:" + i * 5;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setGranularity(10f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        mLineChart.invalidate(); // refresh
        

        return view;
    }
}
