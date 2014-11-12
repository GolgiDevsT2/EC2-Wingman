package io.golgi.wingman;

import android.os.Handler;
import android.os.Message;

/**
 * Created by brian on 10/24/14.
 */
public class ActivityBridge {
    private static final int SERVICE_STARTED = 1;
    private static final int LIST_UPDATED = 2;

    private static Object syncObj = new Object();
    private static ActivityBridge theInstance = null;
    private WingmanActivity activity = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SERVICE_STARTED:
                    activity.serviceStarted();
                    break;
                case LIST_UPDATED:
                    activity.listUpdated();
                    break;
            }
        }
    };


    private static void sendMessage(int what){
        Message m = null;
        Handler h = null;
        synchronized (syncObj){
            if(theInstance != null){
                h = theInstance.handler;
            }
        }

        if(h != null){
            m = new Message();
            m.what = what;
            h.sendMessage(m);
        }
    }

    public static void serviceStarted(){
        sendMessage(SERVICE_STARTED);
    }

    public static void listUpdated(){
        sendMessage(LIST_UPDATED);
    }

    public ActivityBridge(WingmanActivity activity){
        this.activity = activity;
        synchronized(syncObj){
            theInstance = this;
        }
    }
}
