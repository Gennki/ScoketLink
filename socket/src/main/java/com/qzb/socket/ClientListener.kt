package com.qzb.socket

import android.util.Log
import org.java_websocket.handshake.ServerHandshake
import java.lang.ref.WeakReference

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: ServerListener
 * @Author: Leon.Qin
 * @Date: 2022/8/23 15:12
 * @Description:
 */
abstract class ClientListener<T> constructor(t: T) {
    var reference: WeakReference<T>

    init {
        reference = WeakReference<T>(t)
    }


    open fun onOpen(handshakedata: ServerHandshake?, t: T?) {
        Log.d(TAG, "ClientListener onOpen")
    }

    open fun onMessage(message: String?, t: T?) {
        Log.d(TAG, "ClientListener onMessage:$message")
    }

    open fun onClose(code: Int, reason: String?, remote: Boolean, t: T?) {
        Log.d(TAG, "ClientListener onClose:code=$code,reason=$reason")
    }

    open fun onError(ex: Exception?, t: T?) {
        Log.d(TAG, "ClientListener onError:${ex?.message}")
    }
}