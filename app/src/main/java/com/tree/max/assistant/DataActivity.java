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

import com.idtk.smallchart.chart.LineChart;
import com.idtk.smallchart.data.LineData;
import com.idtk.smallchart.interfaces.iData.ILineData;

import java.util.ArrayList;

/**
 * Created by max on 17-5-18.
 */

public class DataActivity extends Activity {
    LineChart lineChart;
    LineData lineData;
    ArrayList<ILineData> dataList;
    ArrayList<PointF> linePointList ;
    LocalReceiver localReceiver;
    int[] x = new int[10];
    int[] y = new int[10];
    final int NUMBER_CHANGED = 1;
    String number;


    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NUMBER_CHANGED:
                    lineChart.setDataList(dataList);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_view);
        init();


        linePointList.add(new PointF(1,2));
        linePointList.add(new PointF(10,10));
        linePointList.add(new PointF(100,100));
        linePointList.add(new PointF(200,200));
        lineData.setValue(linePointList);
        lineData.setColor(Color.CYAN);
        lineData.setPaintWidth(1);
        lineData.setTextSize(4);
        dataList.add(lineData);

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
        unregisterReceiver(localReceiver);
    }

    public void init()
    {
        lineChart = (LineChart)findViewById(R.id.times_data_view);
        lineData = new LineData();
        linePointList = new ArrayList<>();
        dataList = new ArrayList<>();

        for (int j= 0; j<10;j++)
        {
            x[j] = j*10;
        }
    }
    private class LocalReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "LOCAL_BROAD_EV_PROGRESS":
                    Bundle bundle = intent.getExtras();
                    number = bundle.getString("Times");
                    for (int i = 1;i<=9;i++)
                    {
                        x[i-1] = x[i];
                        y[i-1] = y[i];
                    }
                    x[9] = 10;
                    y[9] = 10;
                    linePointList.clear();
                    for (int i = 0 ; i<10;i++)
                    {
                        linePointList.add(new PointF(x[i],y[i]));
                    }
                    lineData.setValue(linePointList);
                    dataList.clear();
                    dataList.add(lineData);
                    Message msg =new Message();
                    msg.what =NUMBER_CHANGED;
                    handler.sendMessage(msg);

            }
        }
    }
}
