package com.kgg.android.seenear

import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService  : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // 서버로 토큰 전송
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // 서버에 토큰 등록 API 호출
    }
}