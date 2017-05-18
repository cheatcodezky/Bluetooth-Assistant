package com.tree.max.assistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by max on 17-5-17.
 */

public class ViewHandler extends Handler {
    public ViewHandler()
    {
    }
    public ViewHandler(Looper looper)
    {
        super(looper);
    }
    @Override
    public void handleMessage(Message msg)
    {
        super.handleMessage(msg);
        Bundle b = msg.getData();
        String times =b.getString("Size");
        sendMessage(msg);
    }
}
