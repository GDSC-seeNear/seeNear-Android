package com.kgg.android.seenear.AuthActivity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.App
import com.kgg.android.seenear.MyFirebaseMessagingService
import com.kgg.android.seenear.SignupActivity
import com.kgg.android.seenear.SplashActivity
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityIntroBinding.inflate(layoutInflater)


//        FirebaseApp.initializeApp(this)
        // 어플 접속 시 FCM token 발급

//
//        val firebaseApp = MyFirebaseMessagingService()
//
//        // FCM 토큰을 가져옵니다.
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // 토큰이 성공적으로 발급된 후 서버로 전송
//            val token = task.result
//            Log.d(TAG, "FCM registration token: $token")
//            if (token != null) {
//                firebaseApp.onNewToken(token)
//            }
//        })


        setContentView(binding.root)

        Log.d("App.prefs.role", App.prefs.role.toString())
        Log.d("App.prefs.accessToken", App.prefs.accessToken.toString())
        Log.d("App.prefs.refreshToken", App.prefs.refreshToken.toString())


        if (App.prefs.accessToken != null){
            Log.d("App.prefs.role", App.prefs.role.toString())
            Log.d("App.prefs.accessToken", App.prefs.accessToken.toString())
            Log.d("App.prefs.refreshToken", App.prefs.refreshToken.toString())

            if (App.prefs.role.equals("user")){
                val intent = Intent(this, UserMainActivity::class.java)
                startActivity(intent)
            }
            else if (App.prefs.role.equals("admin")){
                val intent = Intent(this, AdminMainActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            //로딩화면
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }


        // 사용자 회원가입 바로가기
        binding.userSignupText.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("role", "user")
            startActivity(intent)
        }

        // 보호자 회원가입 바로가기
        binding.adminSignupText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("role", "admin")
            startActivity(intent)
        }

        binding.logIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private var lastTimeBackPressed : Long = 0

    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            finishAffinity()
        }
    }
}