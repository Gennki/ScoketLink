package com.qzb.scoketlink.websocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.qzb.scoketlink.SocketUtils;
import com.qzb.scoketlink.databinding.ActivityOperateBinding;

import org.java_websocket.client.WebSocketClient;

public class OperateActivity extends AppCompatActivity {

    public static void launch(Context context, String ipAddress) {
        Intent intent = new Intent(context, OperateActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        context.startActivity(intent);
    }

    private com.qzb.scoketlink.databinding.ActivityOperateBinding binding;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOperateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ipAddress = getIntent().getStringExtra("ipAddress");
        WebSocketClient client = SocketUtils.getWebSocketClient(ipAddress);

        binding.btnSend.setOnClickListener(v -> {
            if (client != null) {
                String sendText = binding.etContent.getText().toString();
                client.send(sendText);
                binding.tvSend.setText(binding.tvSend.getText().toString() + "\n" + sendText);
            }
        });

    }
}