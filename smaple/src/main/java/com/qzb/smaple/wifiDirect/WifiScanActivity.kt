package com.qzb.smaple.wifiDirect

import android.content.Context
import android.content.Intent
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qzb.smaple.R
import com.qzb.smaple.databinding.ActivityWifiScanBinding
import com.qzb.socket.ServerListener
import com.qzb.socket.SocketDiscoveryListener
import com.qzb.socket.SocketUtils
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ServerHandshake
import java.lang.ref.WeakReference

class WifiScanActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, WifiScanActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWifiScanBinding
    private lateinit var adapter: DeviceAdapter
    private lateinit var data: MutableList<DeviceItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SocketUtils.startService(SocketUtils.getSelfServiceName(this@WifiScanActivity))

        data = ArrayList()
        adapter = DeviceAdapter(this, data)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemChildClickListener { view: View, position: Int ->
            when (view.id) {
                R.id.btn_connect -> {
                    val deviceBean = data[position]
                    if (!deviceBean.isConnected) {
                        connectServer(position)
                    } else {
                        disconnectServer(position)
                    }
                }
                R.id.btn_operate -> {
                    val ip = data[position].nsdServiceInfo?.host?.hostAddress
                    val port = getPort()
                    OperateActivity.launch(this, ip, port)
                }
            }
        }

        SocketUtils.startServer(SocketUtils.getSelfServiceName(this), object : ServerListener() {
            val reference = WeakReference(this@WifiScanActivity)
            override fun onMessage(conn: WebSocket?, message: String?) {
                super.onMessage(conn, message)
                reference.get()?.let { activity ->
                    activity.toast(activity, message)
                }
            }
        })
    }


    private fun connectServer(position: Int) {
        val ip = data[position].nsdServiceInfo?.host?.hostAddress
        val port = getPort()
        SocketUtils.connectServer(ip!!, port, object : com.qzb.socket.ClientListener() {
            val reference = WeakReference(this@WifiScanActivity)

            override fun onOpen(handshakedata: ServerHandshake?) {
                super.onOpen(handshakedata)
                reference.get()?.also { activity ->
                    activity.toast(activity, "连接成功")
                    activity.data[position].isConnected = true
                    activity.adapter.notifyItemChanged(position)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                super.onClose(code, reason, remote)
                reference.get()?.also { activity ->
                    activity.toast(activity, "断开连接")
                    activity.data[position].isConnected = false
                    activity.adapter.notifyItemChanged(position)
                }
            }

            override fun onError(ex: Exception?) {
                super.onError(ex)
                reference.get()?.also { activity ->
                    activity.toast(activity, ex?.message)
                }
            }
        })
    }

    private fun disconnectServer(position: Int) {
        val deviceBean = data[position]
        val ipAddress = deviceBean.nsdServiceInfo?.host?.hostAddress
        val port = deviceBean.port
        SocketUtils.disconnectServer(ipAddress ?: "", port)
    }


    private fun toast(context: Context, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        SocketUtils.discoverServer(object : SocketDiscoveryListener() {
            val reference = WeakReference(this@WifiScanActivity)
            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                super.onServiceResolved(serviceInfo)
                reference.get()?.let { activity ->
                    var isInList = false
                    for (deviceBean in activity.data) {
                        if (deviceBean.nsdServiceInfo?.serviceName == serviceInfo?.serviceName) {
                            isInList = true
                            break
                        }
                    }
                    if (!isInList) {
                        val port = getPort()
                        activity.data.add(DeviceItem(serviceInfo, port, false))
                        activity.adapter.notifyItemInserted(activity.data.size)
                    }
                }
            }


            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                super.onServiceLost(serviceInfo)
                reference.get()?.also { activity ->
                    val deviceItem = activity.data.find { it.nsdServiceInfo?.serviceName == serviceInfo?.serviceName }
                    deviceItem?.let {
                        activity.data.remove(it)
                        activity.adapter.notifyItemRemoved(activity.data.size)
                    }
                }
            }
        })

    }

    override fun onPause() {
        super.onPause()
        SocketUtils.stopDiscoverServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketUtils.stopService()
        SocketUtils.stopServer()
    }

    fun getPort(): Int {
        return SocketUtils.SERVER_PORT
    }
}