package com.qzb.socket

import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.server.WebSocketServer

/**
 * 生活白科技-居家小确幸
 * @ClassName: DeviceBean
 * @Author: Leon.Qin
 * @Date: 2022/8/23 15:32
 * @Description:
 */
data class DeviceBean(
    var ip: String? = null,
    var port: Int? = null,
    var webSocket: WebSocket? = null,
    var client: WebSocketClient? = null
)