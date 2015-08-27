package com.example.catdog.myapplication;

import org.json.simple.JSONObject;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
class ClientTypeBeacon extends ClientJSON {
    private String id;

    public ClientTypeBeacon(String id) {
        this.id = id;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("beacon_id", this.id);

        return jsonObject;
    }
}
