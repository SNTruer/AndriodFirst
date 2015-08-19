package com.example.catdog.myapplication;

import org.json.simple.JSONObject;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
class ClientTypeDevice extends ClientJSON {
    @Override
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", ClientUtility.getDeviceID());
        jsonObject.put("grant", "user");

        return jsonObject;
    }
}
