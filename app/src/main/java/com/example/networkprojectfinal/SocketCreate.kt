package com.example.networkprojectfinal

import android.app.Application
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class SocketCreate : Application() {

    private var mSocket: Socket? = IO.socket("http://192.168.1.117:4000")


    fun getSocket(): Socket? {
        return mSocket
    }
}
