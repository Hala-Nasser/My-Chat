package com.example.networkprojectfinal

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ListView
import com.example.networkprojectfinal.Adapter.MessageAdapter
import com.example.networkprojectfinal.model.MessageFormat
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_message_group.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MessageGroupActivity : AppCompatActivity() {

    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var groupList =JSONArray()
    var sourceId:String?=null
    var senderName:String?=null
//    private var hasConnection = false
//    private var messageListView: ListView? = null
    private var messageAdapter: MessageAdapter? = null

    private var thread2: Thread? = null
    private var startTyping = false
    private var time = 2

    @SuppressLint("HandlerLeak")
    var handler2: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.i("TAG", "handleMessage: typing stopped $startTyping")
            if (time == 0) {

                group_typing.text=""
                Log.i("TAG", "handleMessage: typing stopped time is $time")
                startTyping = false
                time = 2
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_group)

        app = application as SocketCreate
        mSocket = app.getSocket()
        auth = Firebase.auth
        db = Firebase.firestore

        var group_name= intent.getStringExtra("groupName")
        var groupListId=intent.getStringExtra("groupId")!!.split(",").toTypedArray()

        //groupList = desId
        for (i in groupListId){
            groupList.put(i)
        }

        Log.e("groupList hhh",groupList.toString())
        sourceId=auth.currentUser!!.uid
        var groupNameFirstCapital = group_name!![0].toUpperCase()+group_name!!.substring(1)
        txtGroupName.text= "$groupNameFirstCapital's chat"

        getUserName(sourceId!!)


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

        mSocket!!.connect()

        sendGroupButton.setOnClickListener {
            sendMessage()
        }

//        if (savedInstanceState != null) {
//            hasConnection = savedInstanceState.getBoolean("hasConnection")
//        }
//        if (hasConnection) {
//        } else {
            mSocket!!.connect()
            mSocket!!.on("message", onNewMessage)
            mSocket!!.on("on typing", onTyping)
//        }
//
//        hasConnection = true

       // messageListView = findViewById(R.id.rv_group_message)
        val messageFormatList: List<MessageFormat?> = java.util.ArrayList()
        messageAdapter = MessageAdapter(this, R.layout.item_message, messageFormatList)
        rv_group_message.adapter = messageAdapter
        onTypeButtonEnable()

    }


    private fun getUserName(id:String){
        db!!.collection("users").whereEqualTo("id",id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val data = document.data
                        senderName = data["username"] as String
                    }
                }
            }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean("hasConnection", hasConnection)
//    }


    private var onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            try {
                val data = args[0] as JSONObject
                val message=data.getString("message")
                val senderId = data.getString("sourceId")
                val sendName=data.getString("sourceName")
                Log.e("JSONObject",data.getJSONArray("desId").toString())

                var listGroup = data.getJSONArray("desId")

                var desId = ArrayList<String>()

                for (i in 0 until listGroup.length()){
                    if (!desId.contains(listGroup[i].toString())){
                        desId.add(listGroup[i].toString())
                    }
                }
                if (sourceId.equals(senderId)){
                    Log.e("name on message","sender id is: $senderId & sender name is $sendName")
                    val format = MessageFormat(senderId, sendName, message)
                    messageAdapter!!.add(format)
                } else if (desId.contains(sourceId)){
                    for (item in 0 until groupList.length()){
                        if (senderId == groupList[item]){
                            Log.e("name on message","sender id is: $senderId & sender name is $sendName")
                            val format = MessageFormat(senderId, sendName, message)
                            messageAdapter!!.add(format)
                        }
                    }

                }

            }catch (e:Exception){
                Log.e("TAG",e.toString())
            }
        }
    }

    private fun sendMessage() {
        var message = JSONObject()
        message.put("message", edMessage.text.toString())
        message.put("desId",groupList)
        message.put("sourceId",sourceId)
        message.put("sourceName",senderName)
        mSocket!!.emit("message", message)
        Log.e("msg",message.toString())
        edMessage.text.clear()
    }

    fun onTypeButtonEnable() {
        edMessage!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                val onTyping = JSONObject()
                try {
                    onTyping.put("typing", true)
                    onTyping.put("username", senderName)
                    onTyping.put("uniqueId", sourceId)
                    onTyping.put("desId", groupList)
                    Log.e("typing....",groupList.toString())
                    mSocket!!.emit("on typing", onTyping)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                //sendGroupButton!!.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    var onTyping = Emitter.Listener { args ->

        runOnUiThread(Runnable {
            var data =  args[0] as JSONObject
            try {
                var typingOrNot = data.getBoolean("typing");
                var userName = data.getString("username") + " is Typing......";
                var id = data.getString("uniqueId")
                var destenationId=data.getJSONArray("desId")
                Log.e("neeeww",destenationId.toString())

                if(id == sourceId){
                    typingOrNot = false;
                }else {
                    for (i in 0 until destenationId.length()) {
                        if (destenationId[i].toString() == sourceId){
                            for (item in 0 until groupList.length()){
                                if (id == groupList[item]){
                                    group_typing.text = userName
                                }
                            }
                        }
                    }

                }

                if(typingOrNot){
                    if(!startTyping){
                        startTyping = true;
                        thread2= Thread(
                            Runnable() {
                                while(time > 0) {
                                    synchronized (this){
                                        try {
                                            Thread.sleep(1000);
                                            Log.i("TAG", "run: typing " + time);
                                        } catch (e:InterruptedException) {
                                            e.printStackTrace();
                                        }
                                        time--
                                    }
                                    handler2.sendEmptyMessage(0);
                                }

                            })
                        thread2!!.start();

                    }else {
                        time = 2;
                    }

                }
            } catch (e:JSONException) {
                e.printStackTrace();
            }
        })
    }

}