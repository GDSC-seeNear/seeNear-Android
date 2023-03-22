package com.kgg.android.seenear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kgg.android.seenear.AuthActivity.IntroActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Loadingstart()
    }

    private fun Loadingstart() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            finish()
        }, 2000)
    }
}