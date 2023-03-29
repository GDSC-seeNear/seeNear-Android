package com.kgg.android.seenear.UserActivity.userchat

import android.util.Log
import com.kgg.android.seenear.App
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

open class WebSocketListener(private val viewModel: UserChatViewModel) : WebSocketListener() {
    // 웹 소켓에서 메시지를 수신하고, 수신된 메시지를 ChatViewModel의 LiveData에 추가하는 기능

    override fun onOpen(webSocket: WebSocket, response: Response) {
        // WebSocket 연결이 열릴 때 호출
        Log.d("Socket","Open")

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // WebSocket에서 새로운 메시지를 받았을 때 호출

        Log.d("Socket","Receiving : $text")
        val jsonObject = JSONObject(text)
        val content = jsonObject.getString("content")
        viewModel.addChatMessage(content) // 앞 뒤 따옴표 삭제
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("Socket", "Receiving bytes : $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        // WebSocket이 닫히기 전에 호출되는 콜백 메서드입니다.
        // 이 이벤트는 WebSocket이 닫히기 전에 WebSocket을 사용하는 측에서 마지막으로 메시지를 보내기 위해 사용됩니다.
        // onClosing에서는 클라이언트가 보낼 마지막 메시지를 작성하고, WebSocket을 닫기 전에 보낼 수 있습니다.

        Log.d("Socket","Closing : $code / $reason")
//        webSocket.close(NORMAL_CLOSURE_STATUS, null)
//        webSocket.cancel()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // WebSocket 연결 에러 처리

        Log.d("Socket","Error : " + t.message)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        // WebSocket이 닫혔을 때 호출

        Log.d("Socket","Closed : $code / $reason")

    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}