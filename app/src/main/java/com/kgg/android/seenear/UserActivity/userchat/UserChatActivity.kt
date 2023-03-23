package com.kgg.android.seenear.UserActivity.userchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.icu.util.MeasureUnit.BYTE
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kgg.android.seenear.databinding.ActivityUserChatBinding
import com.kgg.android.seenear.network.RetrofitChatInterface
import com.kgg.android.seenear.network.data.chatRequest
import okhttp3.OkHttpClient
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.ByteBuffer
import java.util.*

class UserChatActivity : AppCompatActivity() {
    private lateinit var interpreter: Interpreter
    private lateinit var inputBuffer: ByteBuffer
    private lateinit var client: OkHttpClient
    private lateinit var binding: ActivityUserChatBinding

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.STTButton.setOnClickListener {
            // 권한 체크 코드
            if (Build.VERSION.SDK_INT >= 23)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), 1)

            startSTT()
        }
        binding.chatSendBtn.setOnClickListener {
            if (binding.chatContent.text.length>0){
                val contents = chatRequest(binding.chatContent.text.toString())
                val callApi = RetrofitChatInterface.createForImportChat().sendChat(contents)
                callApi.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful()) { // <--> response.code == 200
                            // 성공 처리
                            response.body()?.let {
                                Log.d("request Id in success :", response.code().toString())
                                Log.d("requestBody!!!", it.toString())
                                Toast.makeText(this@UserChatActivity, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else { // code == 401
                            // 실패 처리
                            response.body()?.let {
                                Log.d("request Id in not :", response.code().toString())

                            }
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("request Id in failure :", t.message.toString())
                    }
                })
            }

        }

    }


    /***
     *  SpeechToText 설정 및 동작
     */
    private  fun startSTT() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }

    }
    /***
     *  SpeechToText 기능 세팅
     */
    private fun recognitionListener() = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) = Toast.makeText(this@UserChatActivity, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {
            Toast.makeText(this@UserChatActivity, "음성인식을 종료합니다", Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@UserChatActivity, "권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(results: Bundle) {
            Toast.makeText(this@UserChatActivity, results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0], Toast.LENGTH_SHORT).show()
            binding.chatContent.setText(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
        }
    }


}
