package com.qzb.smaple.wifip2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

@SuppressLint("MissingPermission")
class WiFiDirectBroadcastReceiver(
    private val listener: P2PListener
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> listener.onP2PStateChanged(intent)
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> listener.onP2PPeersChanged(intent)
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->listener.onP2PConnectionChanged(intent)
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> listener.onP2PThisDeviceChanged(intent)
        }
    }

    public interface P2PListener {
        fun onP2PStateChanged(intent: Intent)
        fun onP2PPeersChanged(intent: Intent)
        fun onP2PConnectionChanged(intent: Intent)
        fun onP2PThisDeviceChanged(intent: Intent)
    }
}