package com.example.iallrecycle.adapters

import android.R.attr
import android.service.quicksettings.Tile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.R
import com.example.iallrecycle.databinding.ActivityNewsBinding
import com.example.iallrecycle.dataclass.News
import android.R.attr.data
import android.text.method.TextKeyListener.clear
import android.widget.ImageView
import com.google.firebase.database.core.RepoManager.clear


class MyAdapter(private val newsList : ArrayList<News>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick (position: Int)
    }

    fun setOnClickListener(listener: onItemClickListener){
        mListener = listener
    }

    fun deleteItem(i : Int){
        newsList.removeAt(i)
        var size = newsList.size
        newsList.clear()
        notifyDataSetChanged()
    }

    fun addItem(i : Int, news : News){
        newsList.add(i, news)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item ,parent,false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val news : News = newsList[position]
        holder.title.text = news.title
        holder.details.text = news.details
        holder.date.text = news.date
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    public class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.tvTitle)
        val details : TextView = itemView.findViewById(R.id.tvDetails)
        val date : TextView = itemView.findViewById(R.id.tvDate)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}