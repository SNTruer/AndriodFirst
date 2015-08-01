package com.example.catdog.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class GroupMapListAdapter extends BaseAdapter {
    ArrayList<GroupMapSuperData> groupList;
    private Context mContext;
    class ViewHolder{
        TextView name;
        public void set(String name)
        {
            this.name.setText(name);
        }
        public ViewHolder(View view){
            name=(TextView)view.findViewById(R.id.contentText);
        }
    }

    public GroupMapListAdapter(Context mContext)
    {
        super();
        this.mContext=mContext;
        groupList=new ArrayList<>();

    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        GroupMapSuperData data = groupList.get(position);
        if(data==null) return null;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.group_map_listview_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else holder=(ViewHolder)convertView.getTag();

        holder.set(data.name);
        return convertView;
    }

    public void settingList(ArrayList list)
    {
        groupList=list;
    }
}
