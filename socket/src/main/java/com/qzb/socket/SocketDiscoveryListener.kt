package com.qzb.socket

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.lang.ref.WeakReference

/**
 * 生活白科技-居家小确幸
 * @ClassName: SocketDiscoveryListener
 * @Author: Leon.Qin
 * @Date: 2022/8/23 14:52
 * @Description:
 */
abstract class SocketDiscoveryListener<T> constructor(t: T) {
    var reference: WeakReference<T>

    init {
        reference = WeakReference<T>(t)
    }

    open fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onStartDiscoveryFailed, errorCode=$errorCode")
    }

    open fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onStopDiscoveryFailed, errorCode=$errorCode")
    }

    open fun onDiscoveryStarted(serviceType: String?, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onDiscoveryStarted")
    }

    open fun onDiscoveryStopped(serviceType: String?, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onDiscoveryStopped")
    }

    open fun onServiceFound(serviceInfo: NsdServiceInfo?, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onServiceFound, serviceName = ${serviceInfo?.serviceName},ip=${serviceInfo?.host?.hostAddress}")
    }

    open fun onServiceLost(serviceInfo: NsdServiceInfo?, t: T?) {
        Log.d(TAG, "SocketDiscoveryListener onServiceLost, serviceName = ${serviceInfo?.serviceName},ip=\${serviceInfo?.host?.hostAddress")
    }

    open fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int, t: T?) {
        Log.d(TAG, "onResolveFailed,serviceName=${serviceInfo?.serviceName},ip=${serviceInfo?.host?.hostAddress},errorCode=$errorCode")
    }

    open fun onServiceResolved(serviceInfo: NsdServiceInfo?, t: T?) {
        Log.d(TAG, "onServiceResolved,serviceName=${serviceInfo?.serviceName},ip=${serviceInfo?.host?.hostAddress}")
    }

}