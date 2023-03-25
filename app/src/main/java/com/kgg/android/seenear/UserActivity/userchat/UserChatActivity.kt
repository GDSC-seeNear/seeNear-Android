package com.kgg.android.seenear.UserActivity.userchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.icu.util.MeasureUnit.BYTE
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextUtils.substring
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.kgg.android.seenear.AdminActivity.admindetail.AdminDetailActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModelFactory
import com.kgg.android.seenear.databinding.ActivityUserChatBinding
import com.kgg.android.seenear.network.ChatInterface
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.chat
import com.kgg.android.seenear.network.data.chatRequest
import com.kgg.android.seenear.network.data.chatResponse
import com.kgg.android.seenear.network.data.registerResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.ByteBuffer
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class UserChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserChatBinding
    private lateinit var viewModel : UserChatViewModel
    lateinit var chatAdapter: ChatAdapter
    val datas = mutableListOf<chat>()
    lateinit var userList: List<registerResponse>

    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat(("yyyy년 MM월 dd일 E요일"), Locale("ko", "KR"))
        val timeFormat = SimpleDateFormat(("HH:mm"), Locale("ko", "KR"))
        val curDate = dateFormat.format(Date(time)).toString()
        val curTime = timeFormat.format(Date(time)).toString()

        val repository = RetrofitRepository()
        val viewModelFactory = UserChatViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(UserChatViewModel::class.java)


        viewModel.chatResponse.observe(this, Observer { it ->
            if (it != null){
                App.prefs.accessToken?.let { viewModel.chatList() }
                chatAdapter.notifyDataSetChanged()
            }
        })

        App.prefs.accessToken?.let { viewModel.connectToWebSocket(it) }

        viewModel.chatList.observe(this, Observer {
            datas.clear()
            datas.addAll(it)
            Log.d("datasCheck!!", datas.toString())
            initRecycler()
            if (it.size>0)
                binding.recyclerView.scrollToPosition(datas.size - 1) // 가장 마지막 항목으로 스크롤
            binding.recyclerView.smoothScrollToPosition(datas.size - 1)

        })


        App.prefs.accessToken?.let { viewModel.chatList() }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)



        Log.d("datasAdd", datas.toString())
        initRecycler()

        chatAdapter.notifyDataSetChanged()

        binding.STTButton.setOnClickListener {
            // 권한 체크 코드
            if (Build.VERSION.SDK_INT >= 23)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), 1)
            chatAdapter.notifyDataSetChanged()

            startSTT()
        }
        binding.chatSendBtn.setOnClickListener {
            if (binding.chatContent.text.length>0){

                val contents = chatRequest(true, binding.chatContent.text.toString(), App.prefs.id!!)
                binding.chatContent.text = null
//                datas.add(chatResponse(0, App.prefs.id!!, contents.content, Timestamp(System.currentTimeMillis()).toString(), contents.is_user_send))
//                chatList()
//                viewModel.sendChat(contents)

                viewModel.sendChatMessage(contents)
            }

        }

        binding.chatContent?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                //텍스트가 변경 될때마다 Call back

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub

                // 키보드가 올라온 후 첫번째 타이핑 전에만 작동
                if (count == 0 && datas.size >=1 ){
                    Log.d("invalidtarget",(datas.size -1).toString())
                    binding.recyclerView.smoothScrollToPosition(datas.size - 1)
                }
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub

            }
        })
    }



    private fun initRecycler() {
        chatAdapter = ChatAdapter(this)
        binding.recyclerView.adapter = chatAdapter

        val lm = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.setHasFixedSize(false)

        Log.d("datas in recycler", datas.toString())

        datas.apply {
            chatAdapter.datas = datas
            chatAdapter.notifyDataSetChanged()

        }

    }


    inner class ChatAdapter(private val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        var datas = mutableListOf<chat>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_recycler,parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int  {
            Log.d("datas", datas.size.toString())
            return datas.size // 실제 데이터의 크기를 반환하도록 수정
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val partner_message: LinearLayout = itemView.findViewById(R.id.partner_message)
            private val my_message: ConstraintLayout = itemView.findViewById(R.id.my_message)
            private val my_message_text: TextView = itemView.findViewById(R.id.my_message_text)
            private val partner_message_text: TextView = itemView.findViewById(R.id.partner_message_text)
            private val loading_bar: LottieAnimationView = itemView.findViewById(R.id.loading_bar)
            private val chat_date: ConstraintLayout = itemView.findViewById(R.id.chat_date)
            private val chat_date_text: TextView = itemView.findViewById(R.id.chat_date_text)

            fun bind(item: chat) {

                if(position==0 || (position > 0 && !item.createdAt?.substring(0, 10).toString().equals(datas[position-1]?.createdAt?.substring(0, 10).toString()))){
                    chat_date.visibility = View.VISIBLE
                    chat_date_text.text = item.createdAt?.substring(0, 10)
                }
                else {
                    chat_date.visibility = View.GONE
                }

                Log.d("datas in bind", item.toString())
                if (item.userSend== true){
                    partner_message.visibility = View.GONE
                    my_message.visibility = View.VISIBLE
                    my_message_text.text = item.content
                }
                else{
                    my_message.visibility = View.GONE
                    partner_message.visibility = View.VISIBLE
                    loading_bar.visibility = View.GONE
                    partner_message_text.text = item.content
                    if (item.content.equals("loading...")){
                        partner_message_text.visibility = View.GONE
                        loading_bar.visibility = View.VISIBLE
                    }
                    else
                        partner_message_text.visibility = View.VISIBLE
                }



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

        override fun onReadyForSpeech(params: Bundle?) {
            Toast.makeText(this@UserChatActivity, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()
            binding.loadingBar.visibility = View.VISIBLE
            binding.STTButton.visibility = View.GONE
            binding.chatContent.isFocusable = false
            binding.chatContent.isFocusableInTouchMode = false
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {
            Toast.makeText(this@UserChatActivity, "음성인식을 종료합니다", Toast.LENGTH_SHORT).show()
            binding.loadingBar.visibility = View.GONE
            binding.STTButton.visibility = View.VISIBLE
            binding.recyclerView.smoothScrollToPosition(datas.size - 1)
            binding.chatContent.isFocusable = true
            binding.chatContent.isFocusableInTouchMode = true

        }

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@UserChatActivity, "권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
            binding.loadingBar.visibility = View.GONE
            binding.STTButton.visibility = View.VISIBLE
            binding.chatContent.isFocusable = true
            binding.chatContent.isFocusableInTouchMode = true

        }

        override fun onResults(results: Bundle) {
            Toast.makeText(this@UserChatActivity, results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0], Toast.LENGTH_SHORT).show()
            binding.chatContent.setText(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
        }
    }


}
