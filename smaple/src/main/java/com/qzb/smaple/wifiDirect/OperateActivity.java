package com.qzb.smaple.wifiDirect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.qzb.smaple.databinding.ActivityOperateBinding;
import com.qzb.socket.SocketUtils;

public class OperateActivity extends AppCompatActivity {

    public static void launch(Context context, String ip, int port) {
        Intent intent = new Intent(context, OperateActivity.class);
        intent.putExtra("ip", ip);
        intent.putExtra("port", port);
        context.startActivity(intent);
    }

    private ActivityOperateBinding binding;
    private String ip;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOperateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", 0);

        binding.btnSend.setOnClickListener(v -> {
            String sendText = binding.etContent.getText().toString();
//            SocketUtils.INSTANCE.sendMessage(ip, port, sendText);
            SocketUtils.INSTANCE.sendAllMessage(sendText);
            binding.tvSend.setText(binding.tvSend.getText().toString() + "\n" + sendText);
        });

    }
}