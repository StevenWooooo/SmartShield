package com.frogdesign.smartshield;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);
//        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
//        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = true;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle("Title");
//                    isShow = true;
//                } else if(isShow) {
//                    collapsingToolbarLayout.setTitle("Titleeee");//carefull there should a space between double quote otherwise it wont work
//                    isShow = false;
//                }
//            }
//        });
        LineChart chart = (LineChart) view.findViewById(R.id.chart);
        float[][] dataObjects = new float[10][2];
        for (int i = 0; i < 10; ++i) {
            dataObjects[i][0] = i;
            dataObjects[i][0] = 2 + (i % 2 == 1 ? 1 : -1);
        }

        List<Entry> entries = new ArrayList<>();

        for (float[] data : dataObjects) {
            entries.add(new Entry(data[0], data[1]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        //dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(777777);
        //dataSet.setValueTextColor(); // styling, ...

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate(); // refresh


        return inflater.inflate(R.layout.fragment_home, null);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//    }
}
