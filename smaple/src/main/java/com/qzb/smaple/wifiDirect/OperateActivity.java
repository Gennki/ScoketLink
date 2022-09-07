package com.qzb.smaple.wifiDirect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.qzb.smaple.databinding.ActivityOperateBinding;
import com.qzb.socket.SocketUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
    private long sendNum = 0;
    private Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            SocketUtils.INSTANCE.sendAllMessage(String.valueOf(sendNum++));
        }
    };

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


        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            private boolean isTimerRun = false;

            @Override
            public void onClick(View v) {
                if (!isTimerRun) {
                    timer.schedule(task, 1000, 1000);
                    isTimerRun = true;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }
}