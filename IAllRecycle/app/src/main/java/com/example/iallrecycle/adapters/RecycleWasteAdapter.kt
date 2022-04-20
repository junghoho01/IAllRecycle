package com.example.iallrecycle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.R
import com.example.iallrecycle.dataclass.RecycleWaste

class RecycleWasteAdapter(private val recycleWasteList : ArrayList<RecycleWaste>): RecyclerView.Adapter<RecycleWasteAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_recycle_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecycleWasteAdapter.MyViewHolder, position: Int) {
        val currentItem = recycleWasteList[position]

        val totalPoint = currentItem.glass.toString().toInt() + currentItem.paper.toString().toInt() + currentItem.plastic.toString().toInt() + currentItem.others.toString().toInt()

        holder.displayPoints.text = "+ " + totalPoint.toString()
        holder.displayDate.text = currentItem.date.toString()
    }

    override fun getItemCount(): Int {
        return recycleWasteList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val displayPoints : TextView = itemView.findViewById(R.id.tvPointEarned)
        val displayDate : TextView = itemView.findViewById(R.id.tvDatetime)
//        val displatDatetime : TextView = itemView.findViewById(R.id.tvDatetime)
    }

}