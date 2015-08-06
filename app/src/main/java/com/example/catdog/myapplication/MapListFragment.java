package com.example.catdog.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class MapListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    private ListView listview;
    private ArrayList<MapData> mapList;
    private GroupMapListAdapter mapAdapter;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";
    private int groupIdx;
    private String parameter;

    private void init()
    {
        try{
            ServerUtill.postRequest(parameter,new ServerUtill.OnComplete(){

                @Override
                public void onComplete(byte[] byteArray) {
                    getMapList(byteArray);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getMapList(final byte[] byteArray)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mapList=MapData.getMapListFromDom(DomChanger.byteToDom(byteArray));
                    listview = (ListView) view.findViewById(R.id.grouplistview);
                    mapAdapter = new GroupMapListAdapter(getActivity().getApplicationContext());
                    mapAdapter.settingList(mapList);
                    listview.setAdapter(mapAdapter);
                    listview.setOnItemClickListener(MapListFragment.this);
                    mapAdapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_get_group_map, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null) groupIdx=bundle.getInt("groupIdx");

        try {
            parameter = URLEncoder.encode("group_idx","UTF-8") + "=" + ((Integer)groupIdx).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        init();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(BROADCAST_LOCAL);
        intent.putExtra("imageUrl",mapList.get(position).imageUrl);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
    }
}
