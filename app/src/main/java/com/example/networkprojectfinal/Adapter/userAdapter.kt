package com.example.networkprojectfinal.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.R
import com.example.networkprojectfinal.model.userModel
import kotlinx.android.synthetic.main.user_item.view.*

class userAdapter(var activity: Context, var data: ArrayList<userModel>) :
    RecyclerView.Adapter<userAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val user_image=itemView.avatar
        val user_name=itemView.name

        fun initialize(data: userModel){
            user_image.setImageResource(R.drawable.logo)
            user_name.text=data.name

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.initialize(data[position]
        )
    }

}
