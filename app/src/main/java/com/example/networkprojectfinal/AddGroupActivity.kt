package com.example.networkprojectfinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.Adapter.addGroupAdpter
import com.example.networkprojectfinal.Fragments.GroupsFragment
import com.example.networkprojectfinal.modle.addGroupModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_group.*
import org.json.JSONArray
import org.json.JSONObject

class AddGroupActivity : AppCompatActivity(), addGroupAdpter.onClick {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    lateinit var idList : Array<String>
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    var groupName:String? =null
    var idGroupList = JSONArray()
    var final=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        auth = Firebase.auth
        db = Firebase.firestore
        app = application as SocketCreate
        mSocket = app.getSocket()

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ")
            }
        }
        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT,  Emitter.Listener {
            runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ")

            }
        })
        mSocket!!.on(Socket.EVENT_CONNECT) {
            Log.e("onConnect", "Socket Connected!")
        };
        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")
            }
        })

        fab_done.setOnClickListener {
            if (edt_add_group_name.text.isNotEmpty()){
                groupName= edt_add_group_name.text.toString()

                var final2 =final.distinct()
                Log.e("final2",final2.toString())
                for (i in final2){
                    idGroupList.put(i)
                }
                idGroupList.put(auth.currentUser!!.uid)
                Log.e("idGroupList",idGroupList.toString())

                var jsonObject=JSONObject()
                 jsonObject.put("groupName",groupName)
                 jsonObject.put("groupList",idGroupList)
                 mSocket!!.emit("group", jsonObject)

                onBackPressed()

                mSocket!!.emit("get_group", "group")

            }else{
                Toast.makeText(this,"Please fill the group name \uD83D\uDE15",Toast.LENGTH_LONG).show()
            }

        }

        getOnlineUser()
        mSocket!!.on("IDS_group_online",onIDLIST)
        mSocket!!.connect()

    }

    private val onIDLIST = Emitter.Listener { args ->
        Log.e("olaomar","دخل")
        var onlineList = args[0] as JSONArray
        val sb = StringBuilder()
        for (i in 0 until onlineList.length()) {
            sb.append(onlineList.get(i)).append(",")
        }
        var idString =sb.toString()
        idList = idString.split(",").toTypedArray()
        getAllUsers(idList)
        for (i in idList.indices) {
            Log.e("olaomar",idList[i])
        }
    }

    private fun getAllUsers(idList:Array<String>){
        val userList= ArrayList<addGroupModel>()
        for (i in idList.indices) {
            if (idList[i]!= auth.currentUser!!.uid){
                var item = idList[i]
                db!!.collection("users").whereEqualTo("id",item)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val data = document.data
                                val name = data["username"] as String
                                val id = data["id"] as String

                                userList.add(addGroupModel(id,name,false))
                            }
                            rv_add_groups.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
                            rv_add_groups.setHasFixedSize(true)
                            val addGroupAdapter = addGroupAdpter(this, userList,this)
                            rv_add_groups.adapter = addGroupAdapter
                        }
                    }
            }
        }
    }

    private fun getOnlineUser() {
        mSocket!!.emit("group_online", "online user")
    }

    override fun onClickItem(list: ArrayList<String>) {
        final = list
    }


}