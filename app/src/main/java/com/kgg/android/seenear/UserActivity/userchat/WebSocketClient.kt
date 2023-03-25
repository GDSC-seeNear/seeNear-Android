package com.kgg.android.seenear.UserActivity.userchat

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketClient (private val listener: WebSocketListener) { // 웹 소켓 연결을 생성하고, 메시지를 보내고, 연결을 닫는 기능을 제공

    private val client: OkHttpClient = OkHttpClient() // OkHttpClient 객체 생성
    private var webSocket: WebSocket? = null // 웹소켓 객체 생성

    fun connect(accessToken: String) { // 연결
        val request = Request.Builder()
            .url("ws://34.28.97.245:8080/chat")
            .header("Authorization", "Bearer $accessToken")
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun close(){ // 연결 닫기
        webSocket?.close(1000, null)
    }

    fun send(message: String) { // 연결된 웹 소켓으로 메시지를 보냄
        webSocket?.send(message)
    }
}
