package com.example.catdog.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientService extends Service {
    @Override
    public void onCreate() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Client client = new Client("127.0.0.1", 12000);

        client.setOnConnectedListener(new Client.onConnected() {
            @Override
            public void onConnected(String ip, int port) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속을 성공했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        client.setOnConnectErrorListener(new Client.onConnectError() {
            @Override
            public void onConnectError(String ip, int port, String result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        client.connectAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
