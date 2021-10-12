package com.example.networkprojectfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        db = Firebase.firestore

        btnRegLogin.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

        }
        log_button.setOnClickListener {
            LogInAccount(email.text.toString(), password.text.toString())

        }

    }

    private fun LogInAccount(email: String, pass: String) {

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.e("log in", "user ${user!!.uid} + ${user.email}")
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}