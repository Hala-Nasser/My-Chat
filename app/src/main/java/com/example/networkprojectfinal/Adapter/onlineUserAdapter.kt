package com.example.networkprojectfinal.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.R
import com.example.networkprojectfinal.model.userModel
import kotlinx.android.synthetic.main.online_user_item.view.*

class onlineUserAdapter (var activity: Context, var data: ArrayList<userModel>, var clickListener: onlineUserAdapter.onUserItemClickListener) :
    RecyclerView.Adapter<onlineUserAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val user_image=itemView.user_online_item
        val user_name=itemView.name


        fun initialize(data: userModel, action:onUserItemClickListener){
            user_image.setImageResource(R.drawable.logo)
            user_name.text=data.name


            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.online_user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.initialize(data[position], clickListener)
    }

    interface onUserItemClickListener{
        fun onItemClick(data:userModel, position: Int)
    }
}