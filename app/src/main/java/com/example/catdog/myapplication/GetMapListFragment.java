package com.example.catdog.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class GetMapListFragment extends Fragment {
    private View view;
    private ListView listview;
    private ArrayList<MapData> mapList;
    private GroupMapListAdapter mapAdapter;
    private int groupIdx;

    private void init()
    {
        /*view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(getFragmentManager().getBackStackEntryCount()>0) //getFragmentManager().popBackStack();
//                    getActivity().get
                    return true;
                }
                return false;
            }
        });*/
        try{
            mapList=RequestServer.getInstance().getMapList(groupIdx);
        }catch (Exception e) {
        }
        listview=(ListView)view.findViewById(R.id.grouplistview);
        mapAdapter = new GroupMapListAdapter(getActivity().getApplicationContext());
        mapAdapter.settingList(mapList);
        listview.setAdapter(mapAdapter);
        mapAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_get_group_map, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null) groupIdx=bundle.getInt("groupIdx");

        init();

        return view;
    }
}
