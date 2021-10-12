package com.example.networkprojectfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var topAnim: Animation
    lateinit var bottomAnim:Animation
    lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        topAnim =android.view.animation.AnimationUtils.loadAnimation(this,R.anim.top_animation)

        img=findViewById(R.id.logo)

        img.animation=topAnim

        val handler= Handler()
        handler.postDelayed(Runnable {
            val intent= Intent(this,SignUp::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}
