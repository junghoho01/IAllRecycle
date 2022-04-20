package com.example.iallrecycle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.R
import com.example.iallrecycle.dataclass.News
import com.example.iallrecycle.dataclass.RecycleWaste

class RecycleListAdapter(private val wasteList : ArrayList<RecycleWaste>) : RecyclerView.Adapter<RecycleListAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick (position: Int)
    }

    fun setOnClickListener(listener: onItemClickListener){
        mListener = listener
    }

    fun deleteItem(i : Int){
        wasteList.removeAt(i)
        var size = wasteList.size
        wasteList.clear()
        notifyDataSetChanged()
    }

    fun addItem(i : Int, recycleWaste : RecycleWaste){
        wasteList.add(i, recycleWaste)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_waste_record ,parent,false)
        return RecycleListAdapter.MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val waste : RecycleWaste = wasteList[position]
        holder.id.text = waste.timestamp
        holder.email.text = waste.recylerEmail
        holder.date.text = waste.date
    }

    override fun getItemCount(): Int {
        return wasteList.size
    }

    public class MyViewHolder(itemView : View, listener: RecycleListAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val id : TextView = itemView.findViewById(R.id.tvIdView)
        val email : TextView = itemView.findViewById(R.id.tvEmailView)
        val date : TextView = itemView.findViewById(R.id.tvDateView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

}