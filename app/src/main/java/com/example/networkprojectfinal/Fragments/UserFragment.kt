package com.example.networkprojectfinal.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.*
import com.example.networkprojectfinal.Adapter.onlineUserAdapter
import com.example.networkprojectfinal.Adapter.userAdapter
import com.example.networkprojectfinal.model.userModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.json.JSONArray


/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment(), onlineUserAdapter.onUserItemClickListener {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    lateinit var idList : Array<String>
    //var idList = ArrayList<String>()
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    var sourceName:String? =null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root= inflater.inflate(R.layout.fragment_user, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        app = activity!!.application as SocketCreate
        mSocket = app.getSocket()

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            activity!!.runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ")
            }
        }
        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT,  Emitter.Listener {
            activity!!.runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ")

            }
        })
        mSocket!!.on(Socket.EVENT_CONNECT) {
            Log.e("onConnect", "Socket Connected!")
        };
        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            activity!!.runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")
            }
        })

        getUserName(auth.currentUser!!.uid)
        getAllUsers()

        sendID()
        mSocket!!.on("IDS",onIDLIST)
        mSocket!!.connect()

        return root
    }


    private val onIDLIST = Emitter.Listener { args ->
        var onlineUsers = args[0] as JSONArray

        val sb = StringBuilder()
        for (i in 0 until onlineUsers.length()) {
            sb.append(onlineUsers.get(i)).append(",")
        }
        var idString =sb.toString()
        idList = idString.split(",").toTypedArray()

//        for (i in 0 until onlineUsers.length()) {
//            if (!idList.contains(onlineUsers[i].toString())){
//                idList.add(onlineUsers[i].toString())
//            }
//        }

        getOnlineUsers(idList)
        for (i in idList.indices) {
            Log.e("olaomar",idList[i])
        }
    }


    private fun getOnlineUsers(idList:Array<String>){
        val userList= ArrayList<userModel>()
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
                                userList.add(userModel(id,name))
                            }
                            rv_online_users.layoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL,false)
                            rv_online_users.setHasFixedSize(true)
                            val onlineUserAdapter = onlineUserAdapter(activity!!, userList,this)
                            rv_online_users.adapter = onlineUserAdapter
                            //userAdapter.notifyDataSetChanged()

                        }
                    }
            }

        }
    }


    //تعبئة الريسايكل العمودية (بتجيب كل اليوزر المسجلين بالفير بيز)
    private fun getAllUsers(){
        val userList= ArrayList<userModel>()
                db!!.collection("users")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val data = document.data
                                val name = data["username"] as String
                                val id = data["id"] as String
                                userList.add(userModel(id,name))
                            }
                            rv_users.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL,false)
                            rv_users.setHasFixedSize(true)
                            val userAdapter = userAdapter(activity!!, userList)
                            rv_users.adapter = userAdapter
                            userAdapter.notifyDataSetChanged()

                        }
                    }
            }

    //بتعبي السورس نيم(بلزمني عشان الاسم الي حيظهر جمب الرسالة عند المستقبل،
    // بنرسلو بالانتنت لما نضغط ع الايتم عشان نروح ع الشات)
    private fun getUserName(id:String){
                db!!.collection("users").whereEqualTo("id",id)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val data = document.data
                                sourceName = data["username"] as String
                            }
                        }
                    }
            }
//لما اضغط ع الاسم ببعت عن طريق الانتتت الاي دي والاسم تبعون الشخص الي ضغطت عليه (يعني المستقبل)
// وكمان الاي دي والاسم تبعون المستخدم الحالي
    //وبنتقل لصفحة الشات
    override fun onItemClick(data: userModel, position: Int) {
        var i = Intent(activity!!,MessageUserActivity::class.java)
        i.putExtra("desId",data.id)
        i.putExtra("desName",data.name)
        i.putExtra("sourceId",auth.currentUser!!.uid)
        i.putExtra("sourceName",sourceName)
        startActivity(i)

    }

    //بنرسل اي دي المستخدم الي سجل دخول عشان يتخزن بالسيرفر في الاونلاين ليست
    private fun sendID() {
        mSocket!!.emit("ID", auth.currentUser!!.uid)
    }




}