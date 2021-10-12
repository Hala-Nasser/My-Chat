package com.example.networkprojectfinal.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.*
import com.example.networkprojectfinal.Adapter.groupAdpter
import com.example.networkprojectfinal.modle.groupModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_groups.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class GroupsFragment : Fragment(), groupAdpter.onGroupItemClickListener {


    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    var groupList= ArrayList<groupModel>()
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_groups, container, false)



        app = activity!!.application as SocketCreate
        mSocket = app.getSocket()
        auth = Firebase.auth

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

        root.add_group.setOnClickListener {
            val i = Intent(activity,AddGroupActivity::class.java)
            startActivity(i)
        }

        groupSend()
        mSocket!!.on("id_group_m",onIDGroupLIST)
        mSocket!!.on("id_group",onIDGroupLIST)
        mSocket!!.connect()

        return root
    }

    private val onIDGroupLIST = Emitter.Listener { args ->
        var getGroupList = args[0] as JSONArray
        try {
            for (i in 0 until getGroupList.length()){
                var obj = getGroupList[i] as JSONObject
                var name =  obj["groupName"] as String
                var list= obj["groupList"] as JSONArray

                Log.e("finally done","name is: $name & id is: $list")

                var id = ArrayList<String>()

                for (i in 0 until list.length()){
                    id.add(list[i].toString())
                }
                if (id.contains(auth.currentUser!!.uid)){
                    if (!groupList.contains(groupModel(name,id))){
                        groupList.add(groupModel(name,id))
                    }
                }
            }

            activity!!.runOnUiThread{
                Log.e("groupList new",groupList.toString())
                for (item in groupList){
                    if (!item.idList.contains(auth.currentUser!!.uid)){
                        groupList.remove(item)
                    }
                }
                rv_groups.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL,false)
                rv_groups.setHasFixedSize(true)
                val groupsAdapter = groupAdpter(activity!!, groupList,this)
                rv_groups.adapter = groupsAdapter
                groupsAdapter.notifyDataSetChanged()
            }

        }catch (e: JSONException){
            e.printStackTrace()
        }

    }

    private fun groupSend() {
        mSocket!!.emit("get_group", "group")
    }

    override fun onItemClick(data: groupModel, position: Int) {
        var i = Intent(activity!!, MessageGroupActivity::class.java)

        val sb = StringBuilder()

        for (i in data.idList) {
            sb.append(i).append(",")
        }
        var idString =sb.toString()
        Log.e("stringBuilder",idString)
        i.putExtra("groupId",idString)
        i.putExtra("groupName",data.name)
        startActivity(i)
    }

}
