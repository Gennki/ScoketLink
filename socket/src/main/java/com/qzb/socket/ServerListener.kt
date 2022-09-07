package com.qzb.socket

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.lang.ref.WeakReference

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: ServerListener
 * @Author: Leon.Qin
 * @Date: 2022/8/23 15:12
 * @Description:
 */
abstract class ServerListener {

    open fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Log.d(TAG, "ServerListener onOpen")
    }

    open fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "ServerListener onClose")
    }

    open fun onMessage(conn: WebSocket?, message: String?) {
        Log.d(TAG, "ServerListener onMessage:$message")
    }

    open fun onError(conn: WebSocket?, ex: Exception?) {
        Log.d(TAG, "ServerListener onError:${ex?.message}")
    }

    open fun onStart() {
        Log.d(TAG, "ServerListener onStart")
    }
}