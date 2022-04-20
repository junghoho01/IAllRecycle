package com.example.iallrecycle.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.R
import com.example.iallrecycle.dataclass.User
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UserAdapter (private val userList : ArrayList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>(){
    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick (position: Int)
    }

    fun setOnClickListener(listener: onItemClickListener){
        mListener = listener
    }

    fun deleteItem(i : Int){
        userList.removeAt(i)
        var size = userList.size
        userList.clear()
        notifyDataSetChanged()
    }

    fun addItem(i : Int, user : User){
        userList.add(i, user)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_user_record ,parent,false)
        return UserAdapter.MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user : User = userList[position]
        holder.name.text = user.name
        holder.email.text = user.email
        holder.phone.text = user.phone.toString()

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${user.profileImage}")
        val localfile = File.createTempFile("tempImage", "jpeg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.picture.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    public class MyViewHolder(itemView : View, listener: UserAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.tvName)
        val email : TextView = itemView.findViewById(R.id.tvEmail)
        val phone : TextView = itemView.findViewById(R.id.tvPhone)
        val picture: ImageView = itemView.findViewById(R.id.userImage)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}