package com.qzb.smaple

import android.app.Application
import android.widget.Toast
import com.qzb.socket.ServerListener
import com.qzb.socket.SocketUtils
import org.java_websocket.WebSocket

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: MyApplication
 * @Author: Leon.Qin
 * @Date: 2022/8/24 14:04
 * @Description:
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SocketUtils.init(this)
    }
}