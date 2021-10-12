package com.example.networkprojectfinal.Adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networkprojectfinal.R
import com.example.networkprojectfinal.modle.addGroupModel
import kotlinx.android.synthetic.main.add_group_item.view.*



var idInGroup=ArrayList<String>()
class addGroupAdpter(var activity: Context, var data: ArrayList<addGroupModel>, var click:onClick):RecyclerView.Adapter<addGroupAdpter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): addGroupAdpter.MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.add_group_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: addGroupAdpter.MyViewHolder, position: Int) {
        holder.user_name.text = data[position].name
        holder.user_image.setImageResource(R.drawable.logo)
        holder.user_Cheack.isChecked=data[position].isCheak!!


        holder.user_Cheack.setOnClickListener {
            data[position].isCheak = !(data[position].isCheak!!)
            Log.e(
                "user check",
                "name: " + data[position].name + " is check: " + data[position].isCheak.toString()
            )
            if (data[position].isCheak == true) {
                Log.e("groupList", "دخل الاف")
                if (!idInGroup.contains(data[position].id)) {
                    idInGroup.add(data[position].id!!)
                }
                Log.e("groupList", idInGroup.toString())
            } else {
                if (idInGroup.contains(data[position].id)) {
                    idInGroup.remove(data[position].id!!)
                }
                Log.e("groupList", idInGroup.toString())
            }
            click.onClickItem(idInGroup)
        }
        holder.card.setOnClickListener {
            data[position].isCheak = !(data[position].isCheak!!)
            holder.user_Cheack.isChecked = data[position].isCheak!!
            Log.e(
                "user check",
                "name: " + data[position].name + " is check: " + data[position].isCheak.toString()
            )
            if (data[position].isCheak == true) {
                Log.e("groupList", "دخل الاف")
                if (!idInGroup.contains(data[position].id)) {
                    idInGroup.add(data[position].id!!)
                }
                Log.e("groupList", idInGroup.toString())
            } else {
                if (idInGroup.contains(data[position].id)) {
                    idInGroup.remove(data[position].id!!)
                }
                Log.e("groupList", idInGroup.toString())
            }
            click.onClickItem(idInGroup)
        }
        idInGroup.clear()

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val user_image=itemView.avatar_add_group
        val user_name=itemView.name_user_add_group
        val user_Cheack=itemView.rdb_choose_user
        val card =itemView.card
    }

    interface onClick{
        fun onClickItem(list: ArrayList<String>)
    }

}


