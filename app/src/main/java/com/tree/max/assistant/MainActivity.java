package com.tree.max.assistant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
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

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    final int NUMBER_CHANGED=1;
    TextView numberView;
    LinearLayout liitleButton ;
    FrameLayout contentFrameLayout ;
    Button startBuletooth,connectBlueTooth,dataButton;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter ;
    ListView listView;
    BroadcastReceiver receiver;
    String number = "0";
    LocalReceiver localReceiver;
    int i  = 0;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NUMBER_CHANGED:
                    numberView.setText(number);
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


//        arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item);
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

                 if (device.getName().equals("HC-06")) {
                     findDevice(device);
                     Log.e("device",device.getName());
                 }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,intentFilter);






    }
    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOCAL_BROAD_EV_PROGRESS");
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
        if (device.getName().equals("HC-06"))
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
                case R.id.startBluetooth:
                    if (!bluetoothAdapter.isEnabled())
                    {
                        int REQUEST_ENABLE_BT = 1;
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,REQUEST_ENABLE_BT);
                    }

                    break;
                case R.id.connectBluetooth:
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                        i = 0;

                    }
                    bluetoothAdapter.startDiscovery();
                    break;
                case R.id.dataButton:
                    Intent intent = new Intent(MainActivity.this,DataActivity.class);
                    startActivity(intent);






            }
        }
    }
    private class LocalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction())
            {
                case "LOCAL_BROAD_EV_PROGRESS":
                    Bundle bundle = intent.getExtras();
                    number = bundle.getString("Times");
                    Message msg =new Message();
                    msg.what =NUMBER_CHANGED;
                    handler.sendMessage(msg);
                    i++;
                    if (i==1)
                    {
                        Snackbar.make(startBuletooth,"已连接到手环",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }


}
