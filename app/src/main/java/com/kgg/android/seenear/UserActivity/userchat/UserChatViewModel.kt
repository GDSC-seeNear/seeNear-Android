package com.kgg.android.seenear.UserActivity.userchat

import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kgg.android.seenear.App
import com.kgg.android.seenear.network.ChatInterface
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp

class UserChatViewModel(private val repository : RetrofitRepository) : ViewModel()  {
    val chatResponse = MutableLiveData<chat>()

    val chatList = MutableLiveData<List<chat>>()
    val chatMessages: LiveData<List<chat>> = chatList

    fun addChatMessage(chatMessage: String) {
        val chatMessages = chatList.value?.filter { it.content != "loading..." }.orEmpty() + chat(0, App.prefs.id!!, chatMessage, Timestamp(System.currentTimeMillis()).toString(), false, )
        chatList.postValue(chatMessages)
    }

//    fun sendChat(contents : chatRequest){
//        val callApi = ChatInterface.createForImportChat().sendChat(contents)
//        callApi.enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                if (response.isSuccessful()) { // <--> response.code == 200
//                    // 성공 처리
//                    response.body()?.let {
//                        Log.d("request Id in success :", response.code().toString())
//                        Log.d("requestBody!!!", it.toString())
//
//                    }
//                } else { // code == 401
//                    // 실패 처리
//                    response.body()?.let {
//                        Log.d("request Id in not :", response.code().toString())
//
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d("request Id in failure :", t.message.toString())
//            }
//        })
//    }

    // WebSocket 연결 관련 코드
    private var webSocketClient: WebSocketClient? = null

    fun connectToWebSocket(accessToken: String) {
        webSocketClient = WebSocketClient(WebSocketListener(this))
        webSocketClient?.connect(accessToken)
    }

    fun sendChatMessage(contents: chatRequest) {
        val chatMessages = chatList.value.orEmpty() + chat(0, contents.elderlyId, contents.content, Timestamp(System.currentTimeMillis()).toString(), contents.userSend)
        chatList.postValue(chatMessages)

        Handler().postDelayed({
            // 3초 후에 실행될 코드 작성
            val chatMessages2 = chatList.value.orEmpty() + chat(0, contents.elderlyId, "loading...", Timestamp(System.currentTimeMillis()).toString(), false)
            chatList.postValue(chatMessages2)
        }, 1000)


        val json = JSONObject()
        json.put("elderlyId", contents.elderlyId)
        json.put("content", contents.content)
        json.put("userSend", contents.elderlyId)
        val message = json.toString()

        webSocketClient?.send(message)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient?.close()
    }

    fun chatList(){
        val header = "Bearer " + App.prefs.accessToken
        val callApi = RetrofitInterface.createForImport().getChatList(header, App.prefs.id!!)
        callApi.enqueue(object : Callback<chatResponse> {
            override fun onResponse(call: Call<chatResponse>, response: Response<chatResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리

                    response.body()?.let {
                        Log.d("request Id in success :", response.code().toString())
                        Log.d("requestBody!!!", it.toString())
                        chatList.value = it.chatList
                    }
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let {
                        Log.d("request Id in not :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call<chatResponse>, t: Throwable) {
                Log.d("request Id in failure :", "here " + t.message.toString())
            }
        })
    }


    fun statusCheck(statusCheckRequest: statusCheckRequest) {

        val header = "Bearer " + App.prefs.accessToken
        val callApi = RetrofitInterface.createForImport().statusCheck( statusCheckRequest, statusCheckRequest.type!!, statusCheckRequest.chatId)
        callApi.enqueue(object : Callback<statusCheckResponse> {
            override fun onResponse(call: Call<statusCheckResponse>, response: Response<statusCheckResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리

                    response.body()?.let {
                        Log.d("request Id in success :", response.code().toString())
                        Log.d("requestBody!!!", it.toString())
                    }
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let {
                        Log.d("request Id in not :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call<statusCheckResponse>, t: Throwable) {
                Log.d("request Id in failure :", "here " + t.message.toString())
            }
        })
    }

}