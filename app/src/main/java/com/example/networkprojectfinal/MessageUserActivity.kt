package com.example.networkprojectfinal

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.networkprojectfinal.Adapter.MessageAdapter
import com.example.networkprojectfinal.model.MessageFormat
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_message_user.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread

class MessageUserActivity : AppCompatActivity() {

    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    var sourceId:String?=null
    var desId:String?=null
    var desName:String?=null
    var sourceName:String?=null
    private var messageAdapter: MessageAdapter? = null

    private var thread2: Thread? = null
    private var startTyping = false
    private var time = 2

    @SuppressLint("HandlerLeak")
    var handler2= object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.i("TAG", "handleMessage: typing stopped $startTyping")
            if (time == 0) {
                typing.text=""
                Log.i("TAG", "handleMessage: typing stopped time is $time")
                startTyping = false
                time = 2
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_user)

        app = application as SocketCreate
        mSocket = app.getSocket()
        desName=intent.getStringExtra("desName")
        desId = intent.getStringExtra("desId")
        sourceId=intent.getStringExtra("sourceId")
        sourceName=intent.getStringExtra("sourceName")
        var desNameFirstCapital = desName!![0].toUpperCase()+desName!!.substring(1)
        txtReceiverName.text= "$desNameFirstCapital's chat"
        

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
            Log.e("onConnect", "Socket Connected!") }
        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")

            }
        })

        sendButton.setOnClickListener {
            sendMessage()
        }

            mSocket!!.connect()
            mSocket!!.on("message", onNewMessage)
            mSocket!!.on("on typing", onTyping)

        val messageFormatList: List<MessageFormat?> = ArrayList()
        messageAdapter = MessageAdapter(this, R.layout.item_message, messageFormatList)
        rv_message.adapter = messageAdapter
        onTypeButtonEnable()
    }


    private var onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            try {
                val data = args[0] as JSONObject
                var mDesId=data.getString("desId")
                var mSrcId=data.getString("sourceId")

                //ازا الايدي تبع المستخدم الي مسجل دخول الحين بيساوي الdesId الي جاي من الرسالة
                // و الايدي تبع اليوزر الي انا فاتحة ع الشات معو بيساوي الsrcId الي جاي من الرسالة

                if (sourceId.equals(mDesId) && desId.equals(mSrcId)){
                    val message=data.getString("message")
                    val senderId = data.getString("sourceId")
                    val format = MessageFormat(senderId, desName!!, message)
                    messageAdapter!!.add(format)
                }
                //
                else if(sourceId.equals(mSrcId) && desId.equals(mDesId)){
                    val message=data.getString("message")
                    val senderId = data.getString("sourceId")
                    val format = MessageFormat(senderId, desName!!, message)
                    messageAdapter!!.add(format)
                }

            }catch (e:Exception){
                Log.e("TAG",e.toString())
            }
        }
    }


    private fun sendMessage() {
        var message = JSONObject()
        message.put("message", textField.text.toString())
        message.put("desId",desId)
        message.put("sourceId",sourceId)
        mSocket!!.emit("message", message)
        textField.text.clear()
    }

    fun onTypeButtonEnable() {
        textField!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence,
                i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int, i1: Int, i2: Int) {
                val onTyping = JSONObject()
                try {
                    onTyping.put("typing", true)
                    onTyping.put("username", sourceName)
                    onTyping.put("uniqueId", sourceId)
                    onTyping.put("desId", desId)
                    mSocket!!.emit("on typing", onTyping)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.e("onTextChanged","onTextChanged")
            }

            override fun afterTextChanged(editable: Editable) {

//                val onTyping = JSONObject()
//                try {
//                    onTyping.put("typing", false)
//                    onTyping.put("username", sourceName)
//                    onTyping.put("uniqueId", sourceId)
//                    onTyping.put("desId", desId)
//
//                    mSocket!!.emit("on typing", onTyping)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//
//                Log.e("afterTextChanged","afterTextChanged")

            }
        })
    }

    var onTyping = Emitter.Listener { args ->

        runOnUiThread(Runnable {
            var data =  args[0] as JSONObject
            try {
                var typingOrNot = data.getBoolean("typing");
                var userName = data.getString("username") + " is Typing......";
                var id = data.getString("uniqueId")
                var destenationId=data.getString("desId")

                    if(id == sourceId){
                        typingOrNot = false
                    }else {
                        if (destenationId == sourceId && id == desId){
                            typing.text = userName
                        }

                    }

//                if (typingOrNot){
//                    Log.e("typingOrNot",typingOrNot.toString())
//                    if(id == sourceId){
//                        typingOrNot = false
//                    }else {
//                        if (destenationId == sourceId && id == desId){
//                            typing.text = userName
//                        }
//                    }
//
//                }
//
//                if (!typingOrNot){
//                    Log.e("typingOrNot",typingOrNot.toString())
//                    typing.text=""
//                }



                //ازا التايبنق او نت كانت بتساوي ترو
                // روح افحصلي الستارت تايبنق ازا كانت بتساوي فولس حطها ترو
                // و روح اعمل ثريد جديد لفلي فيه على التايم طول و هو اكبر من 2
                // اعمل سليب لمدة ثانية و نقص منو واحد
                if(typingOrNot){
                    if(!startTyping){
                        startTyping = true
                        thread2= Thread(
                            Runnable() {
                                while(time > 0) {
                                        try {
                                            Thread.sleep(1000);
                                            Log.i("TAG", "run: typing " + time);
                                        } catch (e:InterruptedException) {
                                            e.printStackTrace();
                                        }
                                        time--
                                    //بتبعت رسالة فاضية
                                    handler2.sendEmptyMessage(0)
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