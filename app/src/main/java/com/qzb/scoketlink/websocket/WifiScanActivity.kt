package com.qzb.scoketlink.websocket

import com.qzb.scoketlink.NsdHelper
import android.os.Bundle
import com.qzb.scoketlink.R
import android.net.nsd.NsdServiceInfo
import android.view.View
import org.java_websocket.handshake.ServerHandshake
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qzb.socket.SocketUtils
import com.qzb.scoketlink.bean.DeviceItem
import com.qzb.scoketlink.databinding.ActivityWifiScanBinding
import com.qzb.socket.ServerListener
import com.qzb.socket.SocketDiscoveryListener
import org.java_websocket.WebSocket
import java.lang.Exception
import java.util.ArrayList

class WifiScanActivity : AppCompatActivity() {
    private var binding: ActivityWifiScanBinding? = null
    private var nsdHelper: NsdHelper? = null
    private var adapter: DeviceListAdapter? = null
    private val data: MutableList<DeviceItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiScanBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        adapter = DeviceListAdapter(this)
        binding!!.recyclerView.adapter = adapter
        adapter!!.setOnItemChildClickListener { view: View, position: Int ->
            when (view.id) {
                R.id.btn_connect -> {
                    val deviceBean = adapter!!.currentList[position]
                    if (!deviceBean.isConnected) {
                        connectServer(position)
                    } else {
                        disconnectServer(position)
                    }
                }
                R.id.btn_operate -> OperateActivity.launch(this, adapter!!.currentList[position].nsdServiceInfo.host.hostAddress)
            }
        }


        SocketUtils.startServer(this, SocketUtils.getSelfServiceName(this), object : ServerListener() {
            override fun onMessage(conn: WebSocket?, message: String?) {
                super.onMessage(conn, message)
                toast(message)
            }
        })

        SocketUtils.discoverServer(object : SocketDiscoveryListener() {
            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                super.onServiceFound(serviceInfo)
                runOnUiThread {
                    var isInList = false
                    for (deviceBean in data) {
                        if (deviceBean.nsdServiceInfo.serviceName == serviceInfo?.serviceName) {
                            isInList = true
                            break
                        }
                    }
                    if (!isInList) {
                        val serviceName = serviceInfo?.serviceName ?: ""
                        val port = serviceName.substring(serviceName.lastIndexOf(":"), serviceName.length).toInt()
                        data.add(DeviceItem(serviceInfo, port, false))
                        adapter!!.submitList(ArrayList(data))
                    }
                }
            }
        })


    }


    private fun connectServer(position: Int) {
        val item = adapter!!.currentList[position]
        val nsdServiceInfo = item.nsdServiceInfo
        val serviceName = nsdServiceInfo.serviceName
        val port = serviceName.substring(serviceName.lastIndexOf(":"), serviceName.length).toInt()
        SocketUtils.connectServer(nsdServiceInfo.host.hostAddress!!, port, object : com.qzb.socket.ClientListener() {
            override fun onOpen(handshakedata: ServerHandshake?) {
                super.onOpen(handshakedata)
                toast("连接成功")
                val newItem = DeviceItem(item.nsdServiceInfo, port, true)
                data[position] = newItem
                adapter!!.submitList(ArrayList(data))
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                super.onClose(code, reason, remote)
                toast("断开连接")
                val newItem = DeviceItem(item.nsdServiceInfo, port, false)
                data[position] = newItem
                adapter!!.submitList(ArrayList(data))
            }

            override fun onError(ex: Exception?) {
                super.onError(ex)
                toast(ex?.message)
            }
        })
    }

    private fun disconnectServer(position: Int) {
        val deviceBean = adapter!!.currentList[position]
        val ipAddress = deviceBean.nsdServiceInfo.host.hostAddress
        val port = deviceBean.port
        SocketUtils.disconnectServer(ipAddress ?: "", port)
    }

    override fun onDestroy() {
        super.onDestroy()
        nsdHelper!!.stopDiscover()
    }

    private fun toast(text: String?) {
        runOnUiThread { Toast.makeText(this@WifiScanActivity, text, Toast.LENGTH_SHORT).show() }
    }

}