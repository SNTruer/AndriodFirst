package com.example.catdog.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by CATDOG on 2015-07-22.
 */
class ViewHolder {
    TextView Uuid;
    TextView MajorId;
    TextView MinorId;
    TextView Distance;

    public void set(BeaconData data)
    {
        Uuid.setText("Uuid : " + data.Uuid);
        MajorId.setText("Major : " + data.MajorId.toString());
        MinorId.setText("Minor : " + data.MinorId.toString());
        Distance.setText("Distance : " + data.Distance.toString());
    }

    public ViewHolder(View view)
    {
        Uuid = (TextView)view.findViewById(R.id.uuid);
        MajorId = (TextView)view.findViewById(R.id.majorid);
        MinorId = (TextView)view.findViewById(R.id.minorid);
        Distance = (TextView)view.findViewById(R.id.distance);
    }
}

public class BeaconListAdapter extends BaseAdapter {
    private Context m_Context;
    private ArrayList<BeaconData> BeaconList;

    public BeaconListAdapter(Context context){
        super();
        m_Context=context;
        BeaconList = new ArrayList<BeaconData>();
    }

    @Override
    public int getCount() {
        return BeaconList.size();
    }

    @Override
    public Object getItem(int position) {
        return BeaconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        BeaconData data=BeaconList.get(position);
        if(data==null) return null;
        if(convertView==null)
        {
            convertView=View.inflate(m_Context,R.layout.beacon_listview_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.set(data);
        return convertView;
    }

    public void AddItem(BeaconData data)
    {
        BeaconList.add(data);
    }

    public void Remove(int position)
    {
        BeaconList.remove(position);
    }

    public void SetList(ArrayList<BeaconData> list){
        BeaconList = list;
    }
}
