package com.qzb.scoketlink.websocket;


import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qzb.scoketlink.NsdHelper;
import com.qzb.scoketlink.R;
import com.qzb.scoketlink.SocketUtils;
import com.qzb.scoketlink.bean.DeviceBean;
import com.qzb.scoketlink.databinding.ActivityWifiScanBinding;
import com.qzb.scoketlink.websocket.listener.ClientListener;
import com.qzb.scoketlink.websocket.listener.ScanServiceListener;
import com.qzb.scoketlink.websocket.listener.SocketServerListener;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.List;

public class WifiScanActivity extends AppCompatActivity {
    public static final String TAG = "NsdHelper";
    private ActivityWifiScanBinding binding;
    private NsdHelper nsdHelper;
    private DeviceListAdapter adapter;
    private List<DeviceBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new DeviceListAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener((view, position) -> {
            switch (view.getId()) {
                case R.id.btn_connect:
                    DeviceBean deviceBean = adapter.getCurrentList().get(position);
                    if (!deviceBean.isConnected()) {
                        connectServer(position);
                    } else {
                        disconnectServer(position);
                    }
                    break;
                case R.id.btn_operate:
                    OperateActivity.launch(this, adapter.getCurrentList().get(position).getNsdServiceInfo().getHost().getHostAddress());
                    break;
            }
        });

        startServer();
        scanServer();
    }


    private void scanServer() {
        nsdHelper = NsdHelper.get(this);
        nsdHelper.scanServer(new ScanServiceListener() {
            @Override
            public void onSuccess(NsdServiceInfo serviceInfo) {
                super.onSuccess(serviceInfo);
                runOnUiThread(() -> {
                    boolean isInList = false;
                    for (DeviceBean deviceBean : data) {
                        if (deviceBean.getNsdServiceInfo().getServiceName().equals(serviceInfo.getServiceName())) {
                            isInList = true;
                            break;
                        }
                    }
                    if (!isInList) {
                        data.add(new DeviceBean(serviceInfo, false));
                        adapter.submitList(new ArrayList<>(data));
                    }
                });
            }
        });
    }

    private void startServer() {
        SocketUtils.startServer(10086, new SocketServerListener() {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                super.onOpen(conn, handshake);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                super.onClose(conn, code, reason, remote);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                super.onMessage(conn, message);
                toast("收到数据：" + message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                super.onError(conn, ex);
                toast(ex.getMessage());
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    private void connectServer(int position) {
        DeviceBean item = adapter.getCurrentList().get(position);
        NsdServiceInfo nsdServiceInfo = item.getNsdServiceInfo();
        SocketUtils.connect(nsdServiceInfo.getHost().getHostAddress(), new ClientListener() {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                toast("连接成功");
                DeviceBean newItem = new DeviceBean(item.getNsdServiceInfo(), true);
                data.set(position, newItem);
                adapter.submitList(new ArrayList<>(data));
            }

            @Override
            public void onMessage(String message) {
                super.onMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                toast("断开连接");
                DeviceBean newItem = new DeviceBean(item.getNsdServiceInfo(), false);
                data.set(position, newItem);
                adapter.submitList(new ArrayList<>(data));
            }

            @Override
            public void onError(Exception ex) {
                super.onError(ex);
                toast(ex.getMessage());
            }
        });
    }

    private void disconnectServer(int position) {
        DeviceBean deviceBean = adapter.getCurrentList().get(position);
        String ipAddress = deviceBean.getNsdServiceInfo().getHost().getHostAddress();
        WebSocketClient client = SocketUtils.getWebSocketClient(ipAddress);
        if (client != null) {
            client.close();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        nsdHelper.stopDiscover();
    }

    private void toast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WifiScanActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}