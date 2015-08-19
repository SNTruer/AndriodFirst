package com.example.catdog.myapplication;

import org.json.simple.JSONObject;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
class ClientUtility {
    public static String getDeviceID() {
        return "TEMP-DEVICEID-STRING-NUMBER";
    }

    public static String toJSONString(Client.Type type, Object content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString().toLowerCase());
        jsonObject.put("content", content);

        return jsonObject.toJSONString();
    }
}
