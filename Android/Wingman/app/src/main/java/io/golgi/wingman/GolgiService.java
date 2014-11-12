package io.golgi.wingman;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.openmindnetworks.golgi.api.GolgiAPIHandler;
import com.openmindnetworks.golgi.api.GolgiException;

import java.util.ArrayList;
import java.util.Hashtable;

import io.golgi.api.GolgiPersistentHash;
import io.golgi.api.GolgiStore;
import io.golgi.apiimpl.android.GolgiAbstractService;
import io.golgi.wingman.gen.GolgiKeys;
import io.golgi.wingman.gen.InstanceData;
import io.golgi.wingman.gen.InstanceList;
import io.golgi.wingman.gen.WingmanService;

/**
 * Created by brian on 10/23/14.
 */
public class GolgiService extends GolgiAbstractService{
    private static Object syncObj = new Object();
    private static GolgiService theInstance = null;

    private InstanceData[] ec2List = new InstanceData[0];
    private Hashtable<String,InstanceData> ec2Hash = new Hashtable<String, InstanceData>();
    private long lastUpdateTime = 0;
    private int nextAlertId = 0;
    private GolgiPersistentHash gpHash;

    public static GolgiService getServiceInstance(){
        return theInstance;
    }

    private static void DBG(String str){
        DBG.write("WINGMAN", str);
    }

    public static String getGolgiId(Context context) {
        String golgiId = GolgiStore.getString(context, "WINGMAN-GOLGI-ID", "");
        if (golgiId.length() == 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 20; i++) {
                sb.append((char) ('A' + (int) (Math.random() * ('z' - 'A'))));
            }

            golgiId = sb.toString();
            GolgiStore.putString(context, "WINGMAN-GOLGI-ID", golgiId);
        }
        return golgiId;
    }

    public static GolgiPersistentHash getGpHash(){
        synchronized(syncObj) {
            if (theInstance != null) {
                return theInstance.gpHash;
            }
        }
        return null;
    }

    public static InstanceData[] getEc2List(){
        synchronized (syncObj){
            if(theInstance != null) {
                return theInstance.ec2List;
            }
            else{
                return new InstanceData[0];
            }
        }
    }

    public static long getLastUpdateTime(){
        synchronized (syncObj){
            if(theInstance != null) {
                return theInstance.lastUpdateTime;
            }
            else{
                return 0;
            }
        }
    }

    private void listUpdated(){
        ActivityBridge.listUpdated();
    }

    private void refreshList(){
        WingmanService.list.sendTo(new WingmanService.list.ResultReceiver() {
            @Override
            public void failure(GolgiException ex) {
                DBG("list() Failed: " + ex.getErrText());
            }

            @Override
            public void success(InstanceList result) {
                DBG("list() success: " + result.getIList().size());
                InstanceData instances[] = result.getIList().toArray(new InstanceData[0]);
                Hashtable<String,InstanceData> hash = new Hashtable<String, InstanceData>();
                for(int i = 0; i < instances.length; i++){
                    InstanceData iData = instances[i];
                    hash.put(iData.getName(), iData);
                    DBG("[" + i + "]: '" + iData.getName() + "' CPU " + iData.getCpuUsage());
                }
                synchronized (syncObj) {
                    ec2List = instances;
                    ec2Hash = hash;
                }
                lastUpdateTime = System.currentTimeMillis();
                listUpdated();
            }
        }, "SERVER");
    }

    public static void refresh(){
        GolgiService dis;

        synchronized (syncObj) {
            dis = theInstance;
        }
        if(dis != null) {
            dis.refreshList();
        }
    }

    public static boolean matchesCurrentFilter(Context context, String name){
        boolean result = true;
        String filter = GolgiStore.getString(context, "FILTER-TEXT", "");
        if(filter.length() > 0 && name.toLowerCase().indexOf(filter) < 0){
            result = false;
        }

        return result;
    }

    public boolean booleanEnabled(String instName, String key, boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean result = false;
        InstanceData iData = ec2Hash.get(instName);

        if (sharedPrefs.getBoolean("onlyNamedAlertEnabled", true) && iData != null){
            if (iData.getName().compareTo(iData.getInstanceId()) == 0) {
                return false;
            }
        }

        if(!sharedPrefs.getBoolean("filteredAlertEnabled", false) || matchesCurrentFilter(this, instName)) {
            result = sharedPrefs.getBoolean(key, defaultValue);
        }

        return result;
    }

    public boolean orangeAlertEnabled(String instName){
        boolean result = false;

        if(gpHash.getInteger(instName.toLowerCase() + ":NO-ORANGE", 0) == 0){
            result = booleanEnabled(instName, "orangeAlertEnabled", true);
        }

        return result;
    }
    public boolean redAlertEnabled(String instName){
        boolean result = false;

        if(gpHash.getInteger(instName.toLowerCase() + ":NO-RED", 0) == 0){
            result = booleanEnabled(instName, "redAlertEnabled", true);
        }
        return result;
    }

    public boolean scFailedAlertEnabled(String instName){
        return booleanEnabled(instName, "scFailedAlertEnabled", true);
    }

    public boolean stateChangeAlertEnabled(String instName){
        return booleanEnabled(instName, "stateChangeAlertEnabled", true);
    }

    public boolean filteredAlertEnabled(String instName){
        return booleanEnabled(instName, "filteredAlertEnabled", true);
    }


    public void displayNewAlert(String type, String details){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String uriStr;
        uriStr = sharedPrefs.getString("alertSound", "");
        if(uriStr.length() == 0){
            uriStr = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
        }

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, WingmanActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, intent, 0);

        NotificationCompat.Builder builder;



        builder = new NotificationCompat.Builder(this.context).setContentTitle(type).
                setContentText(details).
                setSmallIcon(R.drawable.icon_36).
                setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_64)).
                setContentIntent(contentIntent);

        builder.setSound(Uri.parse(uriStr));


        Notification notification = builder.getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(nextAlertId++, notification);
    }


    private WingmanService.raiseOrangeCpuAlarm.RequestReceiver orangeAlarmHandler = new WingmanService.raiseOrangeCpuAlarm.RequestReceiver() {
        @Override
        public void receiveFrom(WingmanService.raiseOrangeCpuAlarm.ResultSender resultSender, String instName, int cpu) {
            if(orangeAlertEnabled(instName)){
                DBG("raiseOrangeCpuAlarm for '" + instName + "' CPU: " + cpu + "%");
                displayNewAlert("Orange CPU Usage (" + cpu + "%)", instName);
            }
            resultSender.success();
            refresh();
        }
    };

    private WingmanService.raiseRedCpuAlarm.RequestReceiver redAlarmHandler = new WingmanService.raiseRedCpuAlarm.RequestReceiver() {
        @Override
        public void receiveFrom(WingmanService.raiseRedCpuAlarm.ResultSender resultSender, String instName, int cpu) {
            if(redAlertEnabled(instName)){
                DBG("raiseRedCpuAlarm for '" + instName + "' CPU: " + cpu + "%");
                displayNewAlert("Red CPU Usage (" + cpu + "%)", instName);
            }
            resultSender.success();
            refresh();
        }
    };

    private WingmanService.raiseStateChangeAlarm.RequestReceiver stateChangeAlarmHandler = new WingmanService.raiseStateChangeAlarm.RequestReceiver() {
        @Override
        public void receiveFrom(WingmanService.raiseStateChangeAlarm.ResultSender resultSender, String instName, int oldState, int newState) {
            if(stateChangeAlertEnabled(instName)) {
                DBG("raiseStateChangeAlarm for '" + instName + "' State Change: " + oldState + " ==> " + newState);
                displayNewAlert("Instance State Change", instName);
            }
            resultSender.success();
            refresh();
        }
    };

    private WingmanService.raiseStatusCheckAlarm.RequestReceiver statusCheckAlarmHandler = new WingmanService.raiseStatusCheckAlarm.RequestReceiver() {
        @Override
        public void receiveFrom(WingmanService.raiseStatusCheckAlarm.ResultSender resultSender, String instName) {
            if(scFailedAlertEnabled(instName)) {
                DBG("raiseStausCheckFailAlarm for '" + instName );
                displayNewAlert("Status Check Failing", instName);
            }
            resultSender.success();
            refresh();
        }
    };

    @Override
    public void readyForRegister(){
        synchronized(syncObj){
            if(theInstance != null && theInstance != this){
                DBG("**************************");
                DBG("Changing Service Instance");
                DBG("**************************");
            }
            theInstance = this;
        }

        gpHash = new GolgiPersistentHash(context, "instanceFilters");

        nextAlertId = (int)(System.currentTimeMillis()/ 1000);

        ActivityBridge.serviceStarted();

        WingmanService.raiseOrangeCpuAlarm.registerReceiver(orangeAlarmHandler);
        WingmanService.raiseRedCpuAlarm.registerReceiver(redAlarmHandler);
        WingmanService.raiseStateChangeAlarm.registerReceiver(stateChangeAlarmHandler);
        WingmanService.raiseStatusCheckAlarm.registerReceiver(statusCheckAlarmHandler);


        registerGolgi(new GolgiAPIHandler() {
                          @Override
                          public void registerSuccess() {
                              DBG.write("register SUCCESS");
                              refreshList();
                          }


                          @Override
                          public void registerFailure() {
                              DBG.write("register FAIL");
                          }
                      },
                GolgiKeys.DEV_KEY,
                GolgiKeys.APP_KEY,
                getGolgiId(this));
    }
}
