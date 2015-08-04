package com.example.catdog.myapplication;

import java.io.Serializable;

/**
 * Created by CATDOG on 2015-07-22.
 */
public class BeaconData implements Serializable {
    String Uuid;
    Integer MajorId;
    Integer MinorId;
    Double Distance;

    public BeaconData(String Uuid,Integer MajorId,Integer MinorId,Double Distance)
    {
        this.Uuid=Uuid;
        this.MajorId=MajorId;
        this.MinorId=MinorId;
        this.Distance=Distance;
    }
}
