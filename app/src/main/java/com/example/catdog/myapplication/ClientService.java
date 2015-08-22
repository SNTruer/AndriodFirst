package com.example.catdog.myapplication;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.WindowManager;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientService extends Service {

    public Client client;
    private Handler handler;
    private Context context;

    @Override
    public void onCreate() {

        // 리시버 등록
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.catdog.myapplication.LOCATE");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String actionName = intent.getAction();
                switch (actionName) {
                    case "com.example.catdog.myapplication.LOCATE":
                        client.request(Client.Type.Locate, new ClientTypeBeacon("BEACON_ID_HERE"));
                        break;

                    default:
                        break;

                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        // 핸들러 등록
        handler = new Handler(Looper.getMainLooper());

        // 컨텍스트 등록
        context = this;

        // 클라이언트 생성
        client = new Client("192.168.11.8", 12000);

        // 클라이언트 접속 성공 이벤트
        client.setOnConnectedListener(new Client.onConnected() {
            @Override
            public void onConnected(String ip, int port) {
                ClientUtility.showToast(context, handler, "서버 접속을 성공했습니다.");
            }
        });

        // 클라이언트 접속 실패 이벤트
        client.setOnConnectErrorListener(new Client.onConnectError() {
            @Override
            public void onConnectError(String ip, int port, final String result) {
                ClientUtility.showToast(context, handler, "서버 접속을 실패했습니다.");
            }
        });

        // 클라이언트 데이터 전송 이벤트
        client.setOnDataSentListener(new Client.onDataSent() {
            @Override
            public void onDataSent(final String result, boolean isError) {
                ClientUtility.showToast(context, handler, result);
            }
        });

        // 클라이언트 데이터 수신 이벤트
        client.setOnDataReceivedListener(new Client.onDataReceived() {
            @Override
            public void onDataReceived(final String result, boolean isError) {
                // TODO 푸시 관련 요청인경우 분석 후 처리 필요
                // TODO 재난 메시지를 받은 경우 명준이형에게 브로드캐스트 발송 필요

                // 데이터 수신 성공
                if (!isError) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder
                                    .setCancelable(false)
                                    .setTitle("긴급")
                                    .setMessage("현재 해당 지역은 긴급 재난 지역으로 선언되었습니다. 시스템의 안내에 따라 가장 빠른 비상 대피로를 통해 신속히 대피해주시기 바랍니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            alert.show();
                        }
                    }, 3000);
                }
            }
        });

        client.connectAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
