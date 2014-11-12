package io.golgi.wingman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openmindnetworks.golgi.api.GolgiAPI;

import java.util.Timer;
import java.util.TimerTask;

import io.golgi.api.GolgiPersistentHash;
import io.golgi.api.GolgiStore;
import io.golgi.wingman.gen.InstanceData;


public class WingmanActivity extends Activity {

    private static final String ALERT_SOUND = "ALERT_SOUND";
    public static final int SETTINGS_COMPLETE = 1;



    private static Object syncObj = new Object();
    private static WingmanActivity theInstance = null;

    private  boolean inFg;
    private ActivityBridge activityBridge;
    private InstanceListAdapter instanceListAdapter;
    private Timer refreshTimer;
    private TextView lastUpdatedTv;
    private TextView filterWarningTv;
    private EditText filterEt;
    private RelativeLayout instanceFilterLayout;
    private TextView ifNameTv;
    private CheckBox ifSupressOrangeCb;
    private CheckBox ifSupressRedCb;
    private GolgiPersistentHash gpHash;
    private String currentName;
    private boolean refreshInFlight = false;
    private boolean instanceFilterVisible = false;


    public void serviceStarted(){
        DBG.write("In Activity, we now know the service has started");
        gpHash = GolgiService.getGpHash();
    }

    public void listUpdated(){
        refreshInFlight = false;
        InstanceData[] ec2List = GolgiService.getEc2List();
        DBG.write("List updated in activity: " + ec2List.length);
        if(instanceListAdapter != null){
            instanceListAdapter.reload();
        }
    }

    public boolean hashBespokeFiltering(String name){
        return (gpHash.getInteger(name.toLowerCase() + ":BESPOKE", 0) != 0) ? true : false;
    }


    private Handler houseKeepHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(inFg){
                long ts = GolgiService.getLastUpdateTime();
                if(ts == 0){
                    lastUpdatedTv.setText("Last Refreshed: NEVER");
                }
                else{
                    int elapsed = (int)((System.currentTimeMillis() - ts) / 1000);
                    if(elapsed < 5){
                        lastUpdatedTv.setText("Last Refreshed: Just Now");
                    }
                    else if(elapsed < 120){
                        lastUpdatedTv.setText("Last Refreshed: " + elapsed + " seconds ago");
                    }
                    else{
                        lastUpdatedTv.setText("Last Refreshed: " + (elapsed/60) + " minutes ago");
                    }

                    if(elapsed > 60 && refreshInFlight == false){
                        refreshInFlight = true;
                        GolgiService.refresh();
                    }
                }
            }
        }
    };


    private void maybeShowFilterWarning(){
        String currentFilter = GolgiStore.getString(this, "FILTER-TEXT", "");

        if(currentFilter.length() == 0){
            filterWarningTv.setVisibility(View.GONE);
        }
        else{
            filterWarningTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_wingman);
        theInstance = this;
        activityBridge = new ActivityBridge(this);
    }

    @Override
    public void onResume() {
        DBG.write("onResume()");
        super.onResume();
        inFg = true;
        GolgiAPI.usePersistentConnection();
        instanceFilterVisible = false;

        if (!GolgiService.isRunning(this)) {
            DBG.write("Start the service");
            GolgiService.startService(this.getApplicationContext());
        }
        else {
            gpHash = GolgiService.getGpHash();
            DBG.write("Service already started");
        }

        instanceFilterLayout = (RelativeLayout)findViewById(R.id.instanceFilterLayout);
        instanceFilterLayout.setVisibility(View.INVISIBLE);

        ifNameTv = (TextView)instanceFilterLayout.findViewById(R.id.ifNameTv);
        ifSupressOrangeCb = (CheckBox)instanceFilterLayout.findViewById(R.id.ifSupressOrangeCb);
        ifSupressRedCb = (CheckBox)instanceFilterLayout.findViewById(R.id.ifSupressRedCb);


        Button okButton = (Button)instanceFilterLayout.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                gpHash.deleteInteger(currentName.toLowerCase() + ":NO-ORANGE");
                gpHash.deleteInteger(currentName.toLowerCase() + ":NO-RED");
                gpHash.deleteInteger(currentName.toLowerCase() + ":BESPOKE");
                if(ifSupressRedCb.isChecked()){
                    gpHash.putInteger(currentName.toLowerCase() + ":NO-RED", 1);
                    count++;
                }

                if(ifSupressOrangeCb.isChecked()){
                    gpHash.putInteger(currentName.toLowerCase() + ":NO-ORANGE", 1);
                    count++;
                }

                if(count > 0){
                    gpHash.putInteger(currentName.toLowerCase() + ":BESPOKE", 1);
                }

                instanceFilterLayout.setVisibility(View.INVISIBLE);
                instanceFilterVisible = false;
                instanceListAdapter.reload();
            }
        });

        ListView lv = (ListView)findViewById(R.id.instanceListView);
        instanceListAdapter = new InstanceListAdapter(this, lv);
        lv.setAdapter(instanceListAdapter);
        instanceListAdapter.reload();

        lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!instanceFilterVisible) {
                    String name = instanceListAdapter.getItem(i).getName();
                    DBG.write("Clicked on: " + i + "/" + l + "'" + name + "'");
                    currentName = name;
                    ifNameTv.setText(name);
                    ifSupressOrangeCb.setChecked(gpHash.getInteger(name.toLowerCase() + ":NO-ORANGE", 0) != 0 ? true : false);
                    ifSupressRedCb.setChecked(gpHash.getInteger(name.toLowerCase() + ":NO-RED", 0) != 0 ? true : false);
                    instanceFilterLayout.setVisibility(View.VISIBLE);
                    instanceFilterVisible = true;
                }
            }
        });

        filterWarningTv = (TextView)findViewById(R.id.filterWarningTv);

        maybeShowFilterWarning();

        filterEt = (EditText)findViewById(R.id.filterEt);

        filterEt.setText(GolgiStore.getString(this, "FILTER-TEXT", ""));

        filterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DBG.write("New Text: '" + editable.toString() + "'");
                String newFilter = editable.toString().toLowerCase().trim();
                String oldFilter = GolgiStore.getString(WingmanActivity.this, "FILTER-TEXT", "");
                if(oldFilter.compareTo(newFilter) != 0) {
                    GolgiStore.deleteString(WingmanActivity.this, "FILTER-TEXT");
                    GolgiStore.putString(WingmanActivity.this, "FILTER-TEXT", newFilter);
                    instanceListAdapter.reload();
                    maybeShowFilterWarning();
                }
            }
        });

        lastUpdatedTv = (TextView)findViewById(R.id.lastUpdatedTv);
        lastUpdatedTv.setText("");

        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                houseKeepHandler.sendEmptyMessage(0);
            }
        }, 100, 1000);


    }

    @Override
    public void onPause() {
        DBG.write("onPause()");
        super.onPause();
        inFg = false;
        GolgiAPI.useEphemeralConnection();
        refreshTimer.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wingman, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(getBaseContext(), WingmanSettings.class);
            startActivityForResult( settingsActivity, SETTINGS_COMPLETE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        synchronized(syncObj){
            if(theInstance == this){
                theInstance = null;
            }
        }
    }

}
