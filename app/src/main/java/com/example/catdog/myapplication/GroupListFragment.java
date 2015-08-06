package com.example.catdog.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by MyeongJun on 2015. 7. 30..
 */
public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View view;
    private ListView listview;
    GroupMapListAdapter listAdapter;
    ArrayList<GroupData> groupList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(){
        listview = (ListView)view.findViewById(R.id.grouplistview);
        try {
            ServerUtill.normalRequest(new ServerUtill.OnComplete(){

                @Override
                public void onComplete(byte[] byteArray) {
                    getGroupList(byteArray);
                }
            });
        }catch(Exception e){
            Log.e("Error!","Error!");
        }
        Log.d("request","request");
    }

    private void getGroupList(final byte[] byteArray)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    groupList = GroupData.getGroupListFromDom(DomChanger.byteToDom(byteArray));
                    listAdapter = new GroupMapListAdapter(getActivity().getApplicationContext());
                    listAdapter.settingList(groupList);
                    listview.setAdapter(listAdapter);
                    listview.setOnItemClickListener(GroupListFragment.this);
                }
                catch (Exception e){

                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_get_group_map,container,false);

        init();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment frg = new MapListFragment();
        Bundle args = new Bundle();
        args.putInt("groupIdx",groupList.get(position).groupIdx);
        frg.setArguments(args);
        FragmentTransaction trs = getFragmentManager().beginTransaction();
        trs.add(R.id.firstlayout, frg,"map");
        trs.addToBackStack("map");

        trs.commit();
    }
}
