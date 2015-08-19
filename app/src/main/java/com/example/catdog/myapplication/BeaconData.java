package com.example.catdog.myapplication;

import java.io.Serializable;

/**
 * Created by CATDOG on 2015-07-22.
 */
public class BeaconData implements Serializable,Comparable<BeaconData> {
    String Uuid;
    Integer MajorId;
    Integer MinorId;
    Double Distance;
    Integer GroupIdx;
    Integer MapIdx;
    String ImageUrl;
    Long time;

    public BeaconData(String Uuid,Integer MajorId,Integer MinorId,Double Distance)
    {
        this.Uuid=Uuid;
        this.MajorId=MajorId;
        this.MinorId=MinorId;
        this.Distance=Distance;
    }

    public BeaconData(Integer GroupIdx,Integer MapIdx,String ImageUrl)
    {
        this.GroupIdx=GroupIdx;
        this.MapIdx=MapIdx;
        this.ImageUrl=ImageUrl;
    }
    @Override
    public int compareTo(BeaconData another) {
        return (this.Distance>another.Distance) ? 1 : (this.Distance==another.Distance) ? 0 : -1;
    }
}
