package com.tree.max.assistant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    final int NUMBER_CHANGED=1;//用来判断是否接到数据
    final int  WARNING =2 ;
    TextView numberView;
    final String name = "HC-06";//设备名字
    LinearLayout liitleButton ;
    FrameLayout contentFrameLayout ;
    Button startBuletooth,connectBlueTooth,dataButton;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter ;
    ListView listView;
    BroadcastReceiver receiver;
    String number = "0";
    LocalReceiver localReceiver;

    Vibrator vibrator ;
    long [] pattern = {100,400,100,400}; // 停止 开启 停止 开启

    String patter = "(\\d+)";
    Pattern r = Pattern.compile(patter);
    Matcher matcher;
    int i  = 0;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NUMBER_CHANGED:
                    connectBlueTooth.setText("已连接");
                    numberView.setText(number);//更改UI数据
                    break;
                case WARNING:
                    Toast.makeText(MainActivity.this,"Warning",Toast.LENGTH_LONG);
                    vibrator.vibrate(pattern,2);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        init();
        circle();

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        connectBlueTooth.setText("连接手环");

//        arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item);
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//蓝牙适配器

        if (bluetoothAdapter == null)
        {
            Snackbar.make(startBuletooth,"You have no bluetooth",Snackbar.LENGTH_INDEFINITE).show();
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                   // arrayAdapter.add(device.getName()+ " : "+device.getAddress());

                 if (device.getName().equals(name)) {
                     findDevice(device);
                     Log.e("device",device.getName());
                 }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//找到设备发出广播
        registerReceiver(receiver,intentFilter);






    }
    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOCAL_BROAD_EV_PROGRESS");//本地UI线程广播
        localReceiver= new LocalReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);


    }
    @Override
    public void onPause()
    {
        super.onPause();

    }
    public void findDevice(BluetoothDevice device)
    {
        if (device.getName().equals(name))
        {
            Log.e("device","find");
            if (bluetoothAdapter.isDiscovering())
            {
                bluetoothAdapter.cancelDiscovery();
            }
            AcceptThread acceptThread = new AcceptThread(device,MainActivity.this);
            acceptThread.start();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(localReceiver);

    }


    private void init(){
        numberView = (TextView)findViewById(R.id.numberTextView);
        liitleButton = (LinearLayout) findViewById(R.id.little_circle);
        contentFrameLayout = (FrameLayout) findViewById(R.id.contentLayout);
        startBuletooth = (Button)findViewById(R.id.startBluetooth);
        connectBlueTooth = (Button)findViewById(R.id.connectBluetooth);
        dataButton = (Button)findViewById(R.id.dataButton);
        listView = (ListView)findViewById(R.id.listView);

        ClickListener clickListener = new ClickListener();
        startBuletooth.setOnClickListener(clickListener);
        connectBlueTooth.setOnClickListener(clickListener);
        dataButton.setOnClickListener(clickListener);







    }

    public void circle()
    {
        Animation sanimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.circle_anim);
        liitleButton.startAnimation(sanimation);
        sanimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circle();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.startBluetooth://开启蓝牙
                    if (!bluetoothAdapter.isEnabled())
                    {
                        int REQUEST_ENABLE_BT = 1;
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,REQUEST_ENABLE_BT);
                    }

                    break;
                case R.id.connectBluetooth://扫描,连接设备
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                        i = 0;

                    }
                    bluetoothAdapter.startDiscovery();
                    break;
                case R.id.dataButton://进入数据界面
                    Intent intent = new Intent(MainActivity.this,DataActivity.class);
                    startActivity(intent);






            }
        }
    }
    private class LocalReceiver extends BroadcastReceiver{//本地ui控制广播

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction())
            {
                case "LOCAL_BROAD_EV_PROGRESS":
                    Bundle bundle = intent.getExtras();
                    number = bundle.getString("Times");
                    matcher = r.matcher(number);
                    if (matcher.find())
                        number = matcher.group(1);
                    else
                        number = "0";
                    int key = Integer.valueOf(number);
                    Message msg =new Message();
                    if (key!=0||key<90)
                    {

                        msg.what = WARNING;
                        handler.sendMessage(msg);
                    }

                    msg.what =NUMBER_CHANGED;
                    handler.sendMessage(msg);
                    i++;
                    if (i==1)
                    {
                        Snackbar.make(startBuletooth,"已接收到数据",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }


}
