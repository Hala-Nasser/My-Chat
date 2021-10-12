package com.example.networkprojectfinal.Adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.networkprojectfinal.R
import com.example.networkprojectfinal.model.MessageFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MessageAdapter(context: Context?, resource: Int, objects: List<MessageFormat?>?) :
    ArrayAdapter<MessageFormat?>(context!!, resource, objects!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var auth = Firebase.auth
        var uniqueId = auth.currentUser!!.uid

        val message: MessageFormat? = getItem(position)

        //(if)ازا كنت انا الي باعت الرسالة
            if (message!!.senderId.equals(uniqueId)) {
            convertView = (context as Activity).layoutInflater
                .inflate(R.layout.my_message, parent, false)
            val messageText = convertView!!.findViewById<TextView>(R.id.my_message_body)
                messageText.text = message.message
        }
            //(else)ازا كنت انا الي بدي استقبل الرسالة
            else {
            convertView = (context as Activity).layoutInflater
                .inflate(R.layout.their_message, parent, false)
            val messageText = convertView!!.findViewById<TextView>(R.id.their_message_body)
            val usernameText =
                convertView.findViewById<View>(R.id.name) as TextView
            messageText.visibility = View.VISIBLE
            usernameText.visibility = View.VISIBLE
                messageText.text = message.message
                usernameText.text = message.senderName
        }
        return convertView
    }
}