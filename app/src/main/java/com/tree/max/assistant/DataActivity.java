package com.tree.max.assistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.PatternsCompat;
import android.util.Log;

import com.idtk.smallchart.chart.LineChart;
import com.idtk.smallchart.data.LineData;
import com.idtk.smallchart.interfaces.iData.ILineData;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 17-5-18.
 */

public class DataActivity extends Activity {
    LineChart lineChart;
    LineData lineData;
    ArrayList<ILineData> dataList;
    ArrayList<PointF> linePointList ;
    LocalReceiver localReceiver;
    PointF pointF;
    String patter = "(\\d+)";
    Pattern r = Pattern.compile(patter);
    Matcher matcher;
    int[] x = new int[12];
    int[] y = new int[12];
    final int NUMBER_CHANGED = 1;
    static String number;
    static Bundle bundle;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NUMBER_CHANGED:
                    Log.e("find","ddd");

                    lineChart.invalidate();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("DataActivity","start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_view);
        init();


        for (int i = 1;i<=9;i++)
        {
            y[i-1] = y[i];
        }
        y[0] = 100;
        y[1] = 0;
        linePointList.clear();
        for (int i = 0 ; i<=10;i++)
        {
            linePointList.add(new PointF(x[i],y[i]));
        }
        Log.e("DataActivity","Broadcast is touched");
        lineData.setValue(linePointList);
        dataList.clear();
        dataList.add(lineData);
        lineData.setColor(Color.CYAN);
        lineData.setPaintWidth(1);
        lineData.setTextSize(4);

        lineChart.isAnimated = false;
        lineChart.setDataList(dataList);



        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOCAL_BROAD_EV_PROGRESS");
        localReceiver= new LocalReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public void init()
    {
        lineChart = (LineChart)findViewById(R.id.times_data_view);
        lineData = new LineData();
        linePointList = new ArrayList<>();
        dataList = new ArrayList<>();

        for (int j= 0; j<12;j++)
        {
            x[j] = (j-2)*10;
        }
        x[1] = 0;
        x[0] = 0;
        x[2] = 0;
    }
    private class LocalReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "LOCAL_BROAD_EV_PROGRESS":
                    bundle = intent.getExtras();
                    number = bundle.getString("Times");
                    matcher = r.matcher(number);
                    if (matcher.find())
                        number = matcher.group(1);
                    else
                        number = "0";

                    for (int i = 3;i<=10;i++)
                    {
                        y[i-1] = y[i];
                    }

                    y[10] = Integer.valueOf(number);
                    linePointList.clear();

                    for (int i = 0 ; i<11;i++)
                    {
                        pointF = new PointF(x[i],y[i]);
                        linePointList.add(pointF);
                    }
                    Log.e("DataActivity","Broadcast is touched");
                    lineData.setValue(linePointList);
                    dataList.clear();
                    dataList.add(lineData);
                    lineData.setColor(Color.CYAN);
                    lineData.setPaintWidth(1);
                    lineData.setTextSize(4);
                    lineChart.isAnimated = false;

                    lineChart.setDataList(dataList);
                    Message msg =new Message();
                    msg.what =NUMBER_CHANGED;
                    handler.sendMessage(msg);

            }
        }
    }
}
