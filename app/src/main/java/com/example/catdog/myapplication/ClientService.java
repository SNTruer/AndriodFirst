package com.example.catdog.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientService extends Service {

    public Client client;
    private Handler handler;
    private IBinder binder = new ClientBinder();

    public class ClientBinder extends Binder {
        ClientService getService() {
            return ClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        // 핸들러 등록
        handler = new Handler(Looper.getMainLooper());

        // 클라이언트 생성
        client = new Client("172.16.101.48", 12000);

        // 클라이언트 접속 성공 이벤트
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

        // 클라이언트 접속 실패 이벤트
        client.setOnConnectErrorListener(new Client.onConnectError() {
            @Override
            public void onConnectError(String ip, int port, final String result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 클라이언트 데이터 전송 이벤트
        client.setOnDataSentListener(new Client.onDataSent() {
            @Override
            public void onDataSent(final String result, boolean isError) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 클라이언트 데이터 수신 이벤트
        client.setOnDataReceivedListener(new Client.onDataReceived() {
            @Override
            public void onDataReceived(String result, boolean isError) {
                // TODO 푸시 관련 요청인경우 분석 후 처리 필요
                // TODO 재난 메시지를 받은 경우 명준이형에게 브로드캐스트 발송 필요
                System.out.println(result);
            }
        });

        client.connectAsync();
    }
}
