package com.qzb.smaple

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qzb.smaple.databinding.ActivityMainBinding
import com.qzb.socket.SERVER_PORT
import com.qzb.socket.ServerListener
import com.qzb.socket.SocketDiscoveryListener
import com.qzb.socket.SocketUtils
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ServerHandshake

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var adapter: DeviceListAdapter? = null
    private lateinit var data: MutableList<DeviceItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        data = ArrayList()
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                R.id.btn_operate -> {
                    val ip = adapter!!.currentList[position].nsdServiceInfo?.host?.hostAddress
                    val port = SERVER_PORT
                    OperateActivity.launch(this, ip, port)
                }
            }
        }

        SocketUtils.startServer(this, SocketUtils.getSelfServiceName(this), object : ServerListener() {
            override fun onMessage(conn: WebSocket?, message: String?) {
                super.onMessage(conn, message)
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })

        SocketUtils.discoverServer(object : SocketDiscoveryListener() {
            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                super.onServiceResolved(serviceInfo)
                var isInList = false
                for (deviceBean in data) {
                    if (deviceBean.nsdServiceInfo?.serviceName == serviceInfo?.serviceName) {
                        isInList = true
                        break
                    }
                }
                if (!isInList) {
                    val port = SERVER_PORT
                    data.add(DeviceItem(serviceInfo, port, false))
                    adapter!!.submitList(data.toMutableList())
                }
            }


            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                super.onServiceLost(serviceInfo)
                val deviceItem = data.find { it.nsdServiceInfo?.serviceName == serviceInfo?.serviceName }
                deviceItem?.let { data.remove(it) }
                adapter!!.submitList(data.toMutableList())
            }
        })
    }


    private fun connectServer(position: Int) {
        val item = adapter!!.currentList[position]
        val nsdServiceInfo = item.nsdServiceInfo
        val port = SERVER_PORT
        SocketUtils.connectServer(nsdServiceInfo?.host?.hostAddress ?: "", port, object : com.qzb.socket.ClientListener() {
            override fun onOpen(handshakedata: ServerHandshake?) {
                super.onOpen(handshakedata)
                toast("连接成功")
                data[position] = data[position].copy(isConnected = true)
                adapter!!.submitList(data.toMutableList())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                super.onClose(code, reason, remote)
                toast("断开连接")
                data[position] = data[position].copy(isConnected = false)
                adapter!!.submitList(data.toMutableList())
            }

            override fun onError(ex: Exception?) {
                super.onError(ex)
                toast(ex?.message)
            }
        })
    }

    private fun disconnectServer(position: Int) {
        val deviceBean = adapter!!.currentList[position]
        val ipAddress = deviceBean.nsdServiceInfo?.host?.hostAddress
        val port = deviceBean.port
        SocketUtils.disconnectServer(ipAddress ?: "", port)
    }


    private fun toast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        adapter?.submitList(data.toMutableList())
        SocketUtils.stopService(this)
        SocketUtils.stopDiscoverServer()
    }
}