package com.example.iallrecycle.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.dataclass.News
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.iallrecycle.DashboardUserActivity
import com.example.iallrecycle.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File

private lateinit var binding: DashboardUserActivity

class NewsAdapter(private val newsList : ArrayList<News>): RecyclerView.Adapter<NewsAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_news_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newsList[position]
        val imageName = newsList[position].pictureName.toString()

        holder.displayTitle.text = currentItem.title
//        holder.displayTitle.text = imageName
        holder.displayDetails.text = currentItem.details
        holder.displayDate.text= currentItem.date

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val localfile = File.createTempFile("tempImage", "jpeg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.displayImage.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val displayTitle : TextView = itemView.findViewById(R.id.tvTitleView)
        val displayDetails : TextView = itemView.findViewById(R.id.tvDetailsView)
        val displayDate : TextView = itemView.findViewById(R.id.tvDateView)
        val displayImage : ImageView = itemView.findViewById(R.id.newsImage)
    }

}