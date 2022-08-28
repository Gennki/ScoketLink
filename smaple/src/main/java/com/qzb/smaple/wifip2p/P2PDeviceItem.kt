package com.qzb.smaple.wifip2p

import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pDevice
import java.io.Serializable


data class P2PDeviceItem(
    var wifiP2pDevice: WifiP2pDevice,
    var isConnected: Boolean = false
) : Serializable