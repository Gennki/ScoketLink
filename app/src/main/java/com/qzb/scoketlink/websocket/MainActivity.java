package com.qzb.scoketlink.websocket;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.qzb.scoketlink.IPUtils;
import com.qzb.scoketlink.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListener();
        binding.tvIp.setText("当前ip：" + IPUtils.getIpAddress(this));

        IPUtils.sendDataToLoacl();
        binding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                IPUtils.readArp();
            }
        }, 5000);
    }

    private void initListener() {
        binding.btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientActivity.launch(MainActivity.this);
            }
        });

        binding.btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerActivity.launch(MainActivity.this);
            }
        });
    }


}