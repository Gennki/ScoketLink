package com.qzb.scoketlink.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qzb.scoketlink.databinding.ActivityServerBinding;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ServerActivity extends AppCompatActivity {

    private WebSocketServer server;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ServerActivity.class);
        context.startActivity(intent);
    }

    private ActivityServerBinding binding;
    private WebSocket webSocket;
    private StringBuilder sendStringBuilder;
    private StringBuilder receiveStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sendStringBuilder = new StringBuilder("");
        receiveStringBuilder = new StringBuilder("");

        server = new WebSocketServer(new InetSocketAddress(8090)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                webSocket = conn;
                refreshSendData("和控制端设备连接成功");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                refreshSendData("关闭连接:" + reason);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                refreshReceiveData(message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                refreshSendData("异常:" + ex.getMessage());
            }

            @Override
            public void onStart() {
                refreshSendData("启动服务器成功");
            }
        };

        server.start();
        refreshSendData("启动服务器");
    }

    private void refreshSendData(String text) {
        sendStringBuilder.append(text).append("\n");
        binding.tvSend.post(new Runnable() {
            @Override
            public void run() {
                binding.tvSend.setText(sendStringBuilder.toString());
            }
        });
    }

    private void refreshReceiveData(String text) {
        receiveStringBuilder.append(text).append("\n");
        binding.tvReceive.post(new Runnable() {
            @Override
            public void run() {
                binding.tvReceive.setText(receiveStringBuilder.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.close();
        }
        if (server != null) {
            try {
                server.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}