package com.example.catdog.myapplication;

import java.util.Arrays;

/**
 * Created by CATDOG on 2015-07-22.
 */
public class Analyzed_Packet {
    public int MajorId;
    public int MinorId;
    public String Uuid;
    public int txPower;
    public double Distance;
    final static int UuidOffset = 9;
    final static int MajorOffset = 25;
    final static int MinorOffset=27;
    final static int txPowerOffset = 29;

    protected static int Get_Short_From_Byte(byte firstByte,byte secondByte)
    {
        int f1 = firstByte & 0xff;
        int f2 = secondByte & 0xff;
        return (f1<<8) | f2;
    }

    protected static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    public Analyzed_Packet(int rssi,byte[] scanRecord)
    {
        Uuid = byteArrayToHex(Arrays.copyOfRange(scanRecord, UuidOffset, 16));
        MajorId = Get_Short_From_Byte(scanRecord[MajorOffset],scanRecord[MajorOffset+1]);
        MinorId = Get_Short_From_Byte(scanRecord[MinorOffset],scanRecord[MinorOffset+1]);
        txPower=(int)scanRecord[txPowerOffset];
        Distance=calculateAccuracy(txPower,rssi);
    }
}
