package com.qzb.smaple.wifip2p

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.qzb.smaple.databinding.ActivityWifiP2pScanBinding
import com.qzb.socket.TAG

@SuppressLint("MissingPermission")
class WifiP2pScanActivity : AppCompatActivity() {


    private lateinit var manager: WifiP2pManager
    private lateinit var binding: ActivityWifiP2pScanBinding
    private lateinit var receiver: WiFiDirectBroadcastReceiver
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var intentFilter: IntentFilter
    private val data: ArrayList<P2PDeviceItem> = ArrayList()
    private lateinit var adapter: P2PDeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiP2pScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "wifi感知" + packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE))

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请开启定位权限", Toast.LENGTH_SHORT).show()
            return
        }

        adapter = P2PDeviceListAdapter(this)
        adapter.setOnItemChildClickListener { view, position ->
            val item = data[position]
            data[position] = item.copy(isConnected = true)
            val config = WifiP2pConfig()
            config.deviceAddress = item.wifiP2pDevice.deviceAddress
            manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "连接成功")
                    item.isConnected = true
                    adapter.submitList(data.toMutableList())
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(this@WifiP2pScanActivity, "连接失败，错误代码$reason", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.recyclerView.adapter = adapter

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        receiver = WiFiDirectBroadcastReceiver(object : WiFiDirectBroadcastReceiver.P2PListener {
            override fun onP2PStateChanged(intent: Intent) {
                // 判断是否支持p2p直连
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> Log.d(TAG, "Wi-Fi P2P is enabled")
                    else -> Log.d(TAG, "Wi-Fi P2P is not enabled")
                }
            }

            override fun onP2PPeersChanged(intent: Intent) {
                // 调用 WifiP2pManager.requestPeers() 以获取当前对等点的列表
                Log.d(TAG, "成功发现设备")
                requestDevices()
            }

            override fun onP2PConnectionChanged(intent: Intent) {
            }

            override fun onP2PThisDeviceChanged(intent: Intent) {
            }

        })

        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
        requestDevices()
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // 发现设备成功后，会在广播的WIFI_P2P_PEERS_CHANGED_ACTION回调
                Log.d(TAG, "成功执行发现设备操作")
            }

            override fun onFailure(p0: Int) {
                Log.d(TAG, "发现设备操作执行失败，错误代码$p0")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun requestDevices() {
        manager.requestPeers(channel) { wifiP2pDeviceList ->
            wifiP2pDeviceList.deviceList.forEach { wifiP2pDevice ->
                Log.d(TAG, "发现设备：${wifiP2pDevice.deviceName}")
                val item = data.find { it.wifiP2pDevice.deviceAddress == wifiP2pDevice.deviceAddress && it.wifiP2pDevice.deviceName == wifiP2pDevice.deviceName }
                if (item == null) {
                    data.add(P2PDeviceItem(wifiP2pDevice, false))
                }
            }

            val iterator = data.iterator()
            if (iterator.hasNext()) {
                val p2PDeviceItem = iterator.next()
                val item = wifiP2pDeviceList.deviceList.find { it.deviceAddress == p2PDeviceItem.wifiP2pDevice.deviceAddress && it.deviceName == p2PDeviceItem.wifiP2pDevice.deviceName }
                if (item == null) {
                    iterator.remove()
                }
            }
            adapter.submitList(data.toMutableList())
        }
    }
}