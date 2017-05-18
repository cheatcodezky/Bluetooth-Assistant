package com.tree.max.assistant;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;

/**
 * Created by max on 17-5-17.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    Handler mHandler;
    Message message;
    Bundle bundle;
    LocalBroadcastManager localBroadcastManager;
    Intent intent = new Intent("LOCAL_BROAD_EV_PROGRESS");


    public ConnectedThread(BluetoothSocket socket,Context context) {

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Log.e("ConnectedThread","start");
        message = new Message();
        bundle = new Bundle();
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity

                String str = new String(buffer);
                str = str.substring(0, bytes);


                while (bytes <3)
                {
                    bytes += mmInStream.read(buffer);
                    str += new String(buffer);
                    str = str.substring(0,bytes);
                }
                Log.e("recv", str);

                Bundle bundle = new Bundle();
                bundle.putString("Times",str);
                intent.putExtras(bundle);
                localBroadcastManager.sendBroadcast(intent);



            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
