package com.qzb.scoketlink.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qzb.scoketlink.databinding.ActivityClientBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ClientActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, ClientActivity.class);
        context.startActivity(intent);
    }

    private ActivityClientBinding binding;
    private WebSocketClient client;
    private StringBuilder sendStringBuilder;
    private StringBuilder receiveStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sendStringBuilder = new StringBuilder("");
        receiveStringBuilder = new StringBuilder("");

        initListener();
    }

    private void initListener() {
        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = binding.etIp.getText().toString();
                URI uri = URI.create("ws://" + ip + ":8090");
                refreshSendData("开始连接受控设备，ip:" + ip);
                client = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        refreshSendData("连接成功");
                    }

                    @Override
                    public void onMessage(String message) {
                        refreshReceiveData(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        refreshSendData("关闭连接:" + reason);
                    }

                    @Override
                    public void onError(Exception ex) {
                        refreshSendData("异常:" + ex.getMessage());
                    }
                };
                if (!client.isOpen()) {
                    client.connect();
                }
            }
        });

        binding.btnOpenPot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client == null || !client.isOpen()) {
                    return;
                }
                String sendData = "{\"cover\":1}";
                client.send(sendData);
                refreshSendData("发送数据：" + sendData);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null && client.isOpen()) {
            client.close();
        }
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
}