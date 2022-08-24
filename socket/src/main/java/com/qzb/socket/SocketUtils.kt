package com.qzb.socket

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: ServiceUtils
 * @Author: Leon.Qin
 * @Date: 2022/8/23 14:29
 * @Description:
 */

const val TAG = "Socket"
const val SERVER_PORT = 10086
const val SERVICE_PORT = 10010

object SocketUtils {
    /**
     * 当前服务是否已经启动
     */
    var isServiceStarted = false

    /**
     * 外部设备连接本机时，存储的外部设备列表
     */
    private val deviceInList: ArrayList<DeviceBean> = ArrayList()

    /**
     * 本机连接外部设备时，存储的外部设备链表
     */
    private val deviceOutList: ArrayList<DeviceBean> = ArrayList()

    private const val SERVICE_TYPE = "_tineco._tcp."
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null
    private var resolveListenerBusy = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.Main)
    private var nsdManager: NsdManager? = null
    private var webSocketServer: WebSocketServer? = null

    /*==============================================开启服务相关=============================================*/
    /**
     * 开启服务端
     */
    fun startServer(context: Context, serviceName: String, serverListener: ServerListener) {
        // 开启前先关闭所有服务，防止端口被占用
        stopService(context)

        webSocketServer = object : WebSocketServer(InetSocketAddress(SERVER_PORT)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                // 本机被外部设备连接上了
                val ip = conn?.remoteSocketAddress?.address?.hostAddress
                val port = conn?.remoteSocketAddress?.port
                val deviceBean = deviceInList.find { it.ip == ip && it.port == port }
                if (deviceBean == null) {
                    deviceInList.add(
                        DeviceBean(
                            ip = ip,
                            port = port,
                            webSocket = conn
                        )
                    )
                }
                scope.launch {
                    serverListener.onOpen(conn, handshake)
                }
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                // 本机检测到外部设备断连了
                val ip = conn?.remoteSocketAddress?.address?.hostAddress
                val port = conn?.remoteSocketAddress?.port
                val deviceBean = deviceInList.find { it.ip == ip && it.port == port }
                deviceBean?.let { deviceInList.remove(it) }
                scope.launch {
                    serverListener.onClose(conn, code, reason, remote)
                }
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                scope.launch {
                    serverListener.onMessage(conn, message)
                }
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                scope.launch {
                    serverListener.onError(conn, ex)
                }
            }

            override fun onStart() {
                scope.launch {
                    serverListener.onStart()
                }
            }
        }
        webSocketServer?.start()
        startService(context.applicationContext, serviceName)
    }


    /**
     * 开启服务
     */
    private fun startService(context: Context, serviceName: String) {
        stopService(context)
        if (registrationListener == null) {
            registrationListener = object : NsdManager.RegistrationListener {
                override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    Log.d(TAG, "SocketUtils onRegistrationFailed, errorCode=$errorCode")
                }

                override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    Log.d(TAG, "SocketUtils onUnregistrationFailed, errorCode=$errorCode")
                }

                override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                    Log.d(TAG, "SocketUtils onServiceRegistered")
                    isServiceStarted = true
                }

                override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                    Log.d(TAG, "SocketUtils onServiceUnregistered")
                    isServiceStarted = false
                }
            }
        }
        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = serviceName
        serviceInfo.serviceType = SERVICE_TYPE
        serviceInfo.port = SERVICE_PORT
        getNsdManager(context).registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun stopService(context: Context) {
        try {
            registrationListener?.let { getNsdManager(context).unregisterService(it) }
        } catch (e: Exception) {
        }
        webSocketServer?.stop()
        webSocketServer = null
    }

    /*==============================================发现服务相关=============================================*/
    /**
     * 发现服务
     */
    fun discoverServer(socketDiscoveryListener: SocketDiscoveryListener) {
        if (discoveryListener == null) {
            discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                    scope.launch {
                        socketDiscoveryListener.onStartDiscoveryFailed(serviceType, errorCode)
                    }
                }

                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                    scope.launch {
                        socketDiscoveryListener.onStopDiscoveryFailed(serviceType, errorCode)
                    }
                }

                override fun onDiscoveryStarted(serviceType: String?) {
                    scope.launch {
                        socketDiscoveryListener.onDiscoveryStarted(serviceType)
                    }
                }

                override fun onDiscoveryStopped(serviceType: String?) {
                    scope.launch {
                        socketDiscoveryListener.onDiscoveryStopped(serviceType)
                    }
                }

                override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                    // 发现服务后，需要解析这个服务的信息，只有解析成功的服务才能被使用
                    if (resolveListener == null) {
                        resolveListener = object : NsdManager.ResolveListener {
                            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                                scope.launch {
                                    socketDiscoveryListener.onResolveFailed(serviceInfo, errorCode)
                                }
                            }

                            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                                scope.launch {
                                    socketDiscoveryListener.onServiceResolved(serviceInfo)
                                }
                                resolveNextInQueue()
                            }

                        }
                    }
                    if (resolveListenerBusy.compareAndSet(false, true)) {
                        nsdManager?.resolveService(serviceInfo, resolveListener)
                    } else {
                        pendingNsdServices.add(serviceInfo)
                    }
                    if (serviceInfo?.host?.hostAddress != null) {
                        socketDiscoveryListener.onServiceFound(serviceInfo)
                    }
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                    pendingNsdServices.remove(serviceInfo)
                    scope.launch {
                        socketDiscoveryListener.onServiceLost(serviceInfo)
                    }
                }

            }
        }
        nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    /**
     * 停止发现服务
     */
    fun stopDiscoverServer() {
        discoveryListener?.let {
            nsdManager?.stopServiceDiscovery(it)
        }
        discoveryListener = null
    }


    /*==============================================连接服务相关=============================================*/
    /**
     * 连接服务
     */
    fun connectServer(ip: String, port: Int, clientListener: ClientListener) {
        val uri = URI.create("ws://$ip:$port")
        val client = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                // 本机连上了外部设备
                val deviceBean = deviceOutList.find { it.ip == ip && it.port == port }
                if (deviceBean == null) {
                    deviceOutList.add(
                        DeviceBean(
                            ip = ip,
                            port = port,
                            client = this
                        )
                    )
                }
                scope.launch {
                    clientListener.onOpen(handshakedata)
                }
            }

            override fun onMessage(message: String?) {
                scope.launch {
                    clientListener.onMessage(message)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // 本机检测到外部设备断连了
                val deviceBean = deviceOutList.find { it.ip == ip && it.port == port }
                deviceBean?.let { deviceOutList.remove(it) }
                scope.launch {
                    clientListener.onClose(code, reason, remote)
                }
            }

            override fun onError(ex: Exception?) {
                scope.launch {
                    clientListener.onError(ex)
                }
            }
        }
        if (!client.isOpen) {
            client.connect()
        }
    }

    fun disconnectServer(remoteIp: String, remotePort: Int) {
        val deviceBean = deviceOutList.find { it.ip == remoteIp && it.port == remotePort }
        deviceBean?.client?.close()
    }


    /*===================================================收发数据相关==================================================*/
    /**
     * 向所有外部设备发送消息
     */
    fun sendAllMessage(message: String) {
        val iterator = deviceOutList.iterator()
        while (iterator.hasNext()) {
            val deviceBean = iterator.next()
            if (deviceBean.client?.isOpen == true) {
                deviceBean.client?.send(message)
            }
        }
    }

    /**
     * 向指定设备发送消息
     */
    fun sendMessage(ip: String, port: Int, message: String) {
        val deviceBean = deviceOutList.find { it.ip == ip && it.port == port }
        deviceBean?.let {
            if (it.client?.isOpen == true) {
                it.client?.send(message)
            }
        }
    }

    /**
     * 获取一个可用的端口号
     */
    private fun getUnUsedPort(): Int {
        val serverSocket = ServerSocket(0)
        serverSocket.close()
        return serverSocket.localPort
    }

    private fun getNsdManager(context: Context): NsdManager {
        if (nsdManager == null) {
            nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        }
        return nsdManager!!
    }


    private var pendingNsdServices = ConcurrentLinkedQueue<NsdServiceInfo>()

    // Resolve next NSD service pending resolution
    private fun resolveNextInQueue() {
        // Get the next NSD service waiting to be resolved from the queue
        val nextNsdService = pendingNsdServices.poll()
        if (nextNsdService != null) {
            // There was one. Send to be resolved.
            nsdManager?.resolveService(nextNsdService, resolveListener)
        } else {
            // There was no pending service. Release the flag
            resolveListenerBusy.set(false)
        }
    }


    /**
     * 获取当前设备名字
     */
    fun getSelfServiceName(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, "bluetooth_name")
    }


}
