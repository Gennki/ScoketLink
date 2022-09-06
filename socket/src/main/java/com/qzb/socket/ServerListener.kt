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
abstract class ServerListener<T> constructor(t: T) {
    var reference: WeakReference<T>

    init {
        reference = WeakReference<T>(t)
    }

    open fun onOpen(conn: WebSocket?, handshake: ClientHandshake?, t: T) {
        Log.d(TAG, "ServerListener onOpen")
    }

    open fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean, t: T) {
        Log.d(TAG, "ServerListener onClose")
    }

    open fun onMessage(conn: WebSocket?, message: String?, t: T) {
        Log.d(TAG, "ServerListener onMessage:$message")
    }

    open fun onError(conn: WebSocket?, ex: Exception?, t: T) {
        Log.d(TAG, "ServerListener onError:${ex?.message}")
    }

    open fun onStart(t: T) {
        Log.d(TAG, "ServerListener onStart")
    }
}