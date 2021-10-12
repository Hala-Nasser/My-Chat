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
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    val TAG = "3la"
    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        db = Firebase.firestore

        btnLogRegister.setOnClickListener {
            onBackPressed()
        }

        button.setOnClickListener {

            createNewAccount(email.text.toString(), password.text.toString())

        }

    }

    private fun createNewAccount(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.e("TestAuthn", "user ${user!!.uid} + ${user.email}")
                addUser(
                    user.uid,
                    user_name.text.toString(),
                    user.email!!,
                    password.text.toString()
                )
                var i = Intent(this,LoginActivity::class.java)
                startActivity(i)
                finish()

            } else {
                Toast.makeText(this, "sing up failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun addUser( id: String, username: String, email: String, password: String) {

        val user = hashMapOf("id" to id,"username" to username, "email" to email, "password" to password)

        db!!.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "User added Successfully with user id ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "added User failed")
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

}