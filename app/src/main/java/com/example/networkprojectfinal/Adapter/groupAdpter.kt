package com.example.networkprojectfinal.Adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.networkprojectfinal.R
import com.example.networkprojectfinal.modle.groupModel
import kotlinx.android.synthetic.main.group_item.view.*


class groupAdpter(var activity: Context, var data: List<groupModel>, var clickListener: onGroupItemClickListener) :
    RecyclerView.Adapter<groupAdpter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val user_image=itemView.avatar_group
        val user_name=itemView.name_group


        fun initialize(data: groupModel, action:onGroupItemClickListener){
            user_image.setImageResource(R.drawable.logo)
            user_name.text=data.name


            itemView.setOnClickListener {
                action.onItemClick(data ,adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.group_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.initialize(data[position], clickListener)
    }

    interface onGroupItemClickListener{
        fun onItemClick(data:groupModel, position: Int)
    }
}
