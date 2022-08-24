package com.qzb.smaple

import android.net.nsd.NsdServiceInfo
import java.io.Serializable


data class DeviceItem(
    var nsdServiceInfo: NsdServiceInfo? = null,
    var port: Int,
    var isConnected: Boolean = false
) : Serializable