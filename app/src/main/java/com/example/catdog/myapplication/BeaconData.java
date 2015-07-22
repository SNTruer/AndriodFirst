package com.example.catdog.myapplication;

/**
 * Created by CATDOG on 2015-07-22.
 */
public class BeaconData {
    String Uuid;
    Short MajorId;
    Short MinorId;
    Double Distance;

    public BeaconData(String Uuid,Short MajorId,Short MinorId,Double Distance)
    {
        this.Uuid=Uuid;
        this.MajorId=MajorId;
        this.MinorId=MinorId;
        this.Distance=Distance;
    }
}
