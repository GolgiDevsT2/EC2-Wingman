package io.golgi.wingman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Comparator;

import io.golgi.api.GolgiStore;
import io.golgi.wingman.gen.InstanceData;

/**
 * Created by brian on 10/24/14.
 */
public class InstanceListAdapter extends ArrayAdapter<InstanceData> implements Comparator<InstanceData> {
    private WingmanActivity activity;
    private ListView listView;


    @Override
    public int compare(InstanceData m1, InstanceData m2){
        int rc;

        rc = m1.getState() - m2.getState();

        if(rc == 0) {
            rc = (int) (m2.getCpuUsage() - m1.getCpuUsage());
        }

        if(rc == 0){
            rc = m1.getName().compareTo(m2.getName());
        }


        return rc;
    }

    public static class ViewHolder{
        public View self;
        public TextView nameTv;
        public TextView cpuUsageTv;
        public InstanceData iData;
        public CPUView cpuView;
        public View runningView;
        public TextView stateView;
        public View statusCheckFailingView;
        public View customFilterIndicator;
        public int position;
    }

    private View initRowView(InstanceData iData){
        View rowView;
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        rowView = layoutInflater.inflate(R.layout.instance_details, null);
        viewHolder.nameTv = (TextView)rowView.findViewById(R.id.nameTv);
        viewHolder.cpuUsageTv = (TextView)rowView.findViewById(R.id.cpuUsageTv);
        viewHolder.cpuView = (CPUView)rowView.findViewById(R.id.cpuView);
        viewHolder.runningView = rowView.findViewById(R.id.runningView);
        viewHolder.stateView = (TextView)rowView.findViewById(R.id.stateView);
        viewHolder.statusCheckFailingView = rowView.findViewById(R.id.statusChecksFailingView);
        viewHolder.customFilterIndicator = rowView.findViewById(R.id.customFilterIndicator);
        viewHolder.iData = iData;
        rowView.setTag(viewHolder);
        return rowView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        InstanceData iData = getItem(position);
        ViewHolder holder;
        View rowView = convertView;
        // DBG.write("***************************** Get: " + position);

        if(rowView == null){
            rowView = initRowView(iData);
        }

        holder = (ViewHolder)rowView.getTag();
        holder.self = rowView;
        holder.position = position;
        holder.iData = iData;
        displayMessage(holder);

        return rowView;
    }


    private void displayMessage(ViewHolder holder){
        RelativeLayout.LayoutParams rlp;
        InstanceData iData = holder.iData;

        holder.nameTv.setText(iData.getName());
        holder.cpuUsageTv.setText("" + iData.getCpuUsage() + "%");
        holder.runningView.setVisibility(View.GONE);
        holder.stateView.setVisibility(View.GONE);
        holder.statusCheckFailingView.setVisibility(View.GONE);
        holder.customFilterIndicator.setVisibility(View.INVISIBLE);

        if(iData.getStatusCheckFailed() != 0){
            holder.statusCheckFailingView.setVisibility(View.VISIBLE);
            holder.self.setBackgroundColor(0xffaa0000);
        }
        else{
            holder.self.setBackgroundColor(0x00);
        }

        // 0 (pending)
        // 16 (running)
        // 32 (shutting-down)
        // 48 (terminated)
        // 64 (stopping)
        // 80 (stopped).
        String stateTxt = null;
        switch ((iData.getState()) & 0xff){
            case 0:
                stateTxt = "PENDING";
                break;
            case 16:
                stateTxt = "RUNNING";
                break;
            case 32:
                stateTxt = "SHUTTING-DOWN";
                break;
            case 48:
                stateTxt = "TERMINATED";
                break;
            case 64:
                stateTxt = "STOPPING";
                break;
            case 80:
                stateTxt = "STOPPED";
                break;
        }

        if(stateTxt == null){
            stateTxt = "UNKNOWN STATE: " + (iData.getState() & 0xff);
        }

        if(iData.getState() == 16){
            holder.runningView.setVisibility(View.VISIBLE);
        }
        else{
            holder.stateView.setText(stateTxt);
            holder.stateView.setVisibility(View.VISIBLE);
        }

        if(activity.hashBespokeFiltering(iData.getName())){
            holder.customFilterIndicator.setVisibility(View.VISIBLE);
        }

        holder.cpuView.percent = iData.getCpuUsage();
        holder.cpuView.invalidate();
        holder.self.invalidate();

    }


    public int reload(){
        boolean markedSome = false;
        int count;

        setNotifyOnChange(false);
        clear();

        String filter = GolgiStore.getString(activity, "FILTER-TEXT", "");

        InstanceData[] allData = GolgiService.getEc2List();
        DBG.write("Reloading Instance Data: " + allData.length + " using filter '" + filter + "'");
        count = 0;
        for(int i = 0; i < allData.length; i++){
            if(GolgiService.matchesCurrentFilter(activity, allData[i].getName())){
                add(allData[i]);
                count++;
            }
        }

        sort(this);

        notifyDataSetChanged();

        return count;
    }

    public InstanceListAdapter(WingmanActivity activity, ListView listView){
        super(activity, R.layout.instance_details);
        this.activity = activity;
        this.listView = listView;
    }


}
