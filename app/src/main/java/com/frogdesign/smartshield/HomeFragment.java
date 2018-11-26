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

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class HomeFragment extends Fragment {

    private List<String> names = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    void initDevices() {
        names.add("MacBook Pro");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2mWFoSbenRKHNP8Akv75PTExe88EmDMLDuuv1HNkTION4pGadOw");

        names.add("Alexa");
        imageUrls.add("https://images-na.ssl-images-amazon.com/images/I/51TFnR7AtGL._SY300_QL70_.jpg");

        names.add("WyzeCam");
        imageUrls.add("https://images-na.ssl-images-amazon.com/images/I/31pBkWRliML.jpg");

        names.add("iPhone X");
        imageUrls.add("https://static.mts.rs/GALERIJA/MOBILNI%20TELEFONI/IPHONE/IPHONE%20X/iPhone_X_1_popup_1500x1500px.jpg");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);

//        String strtext = getArguments().getString("traffic");
//        TextView test = view.findViewById(R.id.test_seg);
//        test.setText(strtext);

        SpeedView speedometer = view.findViewById(R.id.speedView);
        speedometer.speedTo(80, 4000);
        speedometer.setMarkColor(Color.argb(0,0,0,0));
        speedometer.setCenterCircleColor(Color.argb(0,0,0,0));

        speedometer.setLowSpeedPercent(30);
        speedometer.setMediumSpeedPercent(70);

        speedometer.setHighSpeedColor(Color.argb(255,120,180,40));
        speedometer.setMediumSpeedColor(Color.argb(255,245,210,70));
        speedometer.setLowSpeedColor(Color.argb(255,230,80,60));
        // speedometer.setSpeedometerWidth(120);
        speedometer.setTextColor(Color.argb(0,0,0,0));
        speedometer.setSpeedTextColor(Color.argb(0,0,0,0));
        speedometer.setUnitTextColor(Color.argb(0,0,0,0));

        speedometer.setIndicator(Indicator.Indicators.KiteIndicator);
        speedometer.setIndicatorWidth(60);
        speedometer.setIndicatorColor(Color.argb(150,205,205,205));
        speedometer.setIndicatorLightColor(Color.argb(100,255,255,255));

        initDevices();
        System.out.println("devices number = " + names.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_devices);
        recyclerView.setLayoutManager(layoutManager);
        DevicesRecyclerViewAdapter adapter = new DevicesRecyclerViewAdapter(view.getContext(), names, imageUrls);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
