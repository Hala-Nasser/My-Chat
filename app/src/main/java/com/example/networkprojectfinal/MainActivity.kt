package com.example.networkprojectfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.networkprojectfinal.Fragments.GroupsFragment
import com.example.networkprojectfinal.Fragments.UserFragment
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app = application as SocketCreate
        mSocket = app.getSocket()
        auth = Firebase.auth


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        logout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Log out")
            alertDialog.setMessage("Are you sure?")
            alertDialog.setCancelable(false)
            alertDialog.setIcon(R.drawable.out_black)

            alertDialog.setPositiveButton("Yes") { _, _ ->
                sendID()
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
            alertDialog.setCancelable(true)
            alertDialog.create().show()
        }

    }

    private fun sendID() {
        mSocket!!.emit("id_left", auth.currentUser!!.uid)
    }
}
