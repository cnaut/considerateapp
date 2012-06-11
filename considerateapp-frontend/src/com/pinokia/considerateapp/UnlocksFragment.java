package com.pinokia.considerateapp;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UnlocksFragment extends Fragment {
    //There's no reason to have a timer like the other fragments since they can't change these numbers
    //and still see the screen

    // global variables
    private TextView text;
    private ChartView chart;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            update();
        }
    };

    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.stats_layout, container, false);
        text = (TextView) view.findViewById(R.id.text);
        chart = (ChartView) view.findViewById(R.id.chart);
        chart.setType(ChartView.chartType.AREA);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        setUserVisibleHint(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("OnPause: Unlocks");

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume: Unlocks");

        //Whenever the StatsService tells us these numbers have changed, we update our graphs. 
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(StatsService.BROADCAST_ACTION));
        update();
    }

    private void update() {
        ArrayList<Integer> numScreenViews = StatsService.getNumScreenViews();
        ArrayList<Integer> numUnlocks = StatsService.getNumUnlocks();

        XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
        TimeSeries views = new TimeSeries("Number of Screen Views");
        TimeSeries unlocks = new TimeSeries("Number of Unlocks");

        //Translate the stored history into data plot points
        int lastIndex = numScreenViews.size() - 1;
        Date today = new Date();
        for (int i = 0; i <= lastIndex; i++) {
            Date date = new Date(today.getTime() - (lastIndex - i) * 86400000);
            views.add(date, numScreenViews.get(i));
            unlocks.add(date, numUnlocks.get(i));
        }
        data.addSeries(views);
        data.addSeries(unlocks);
        chart.createChart(data);
        chart.invalidate();
        System.out.println("UNLOCKS UPDATE");

        text.setText("You have checked your phone "
                + numScreenViews.get(lastIndex).intValue()
                + " times\n and unlocked your phone "
                + numUnlocks.get(lastIndex).intValue() + " times today.");
    }
}
