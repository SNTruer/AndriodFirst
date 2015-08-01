package com.example.catdog.myapplication;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class GroupData extends GroupMapSuperData {
    Integer groupIdx;

    public GroupData(String name,Integer groupIdx)
    {
        super(name);
        this.groupIdx=groupIdx;
    }
}
