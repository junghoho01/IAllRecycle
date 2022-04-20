package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.adapters.MyAdapter
import com.example.iallrecycle.databinding.ActivityNewsBinding
import com.example.iallrecycle.dataclass.News
import com.example.iallrecycle.gresture.SwipeGesture
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class NewsActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityNewsBinding

    // Recycler View
    private lateinit var dbref : DatabaseReference
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsArrayList: ArrayList<News>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addNewsBtn.setOnClickListener {
            startActivity(Intent(this, NewsAddActivity::class.java))
            finish()
        }

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, DashboardAdminActivity::class.java))
            finish()
        }

        newsRecyclerView = findViewById(R.id.newsList)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsRecyclerView.setHasFixedSize(true)

        newsArrayList = arrayListOf<News>()

        getNewsData()
    }

    private fun getNewsData() {

        // Select * from news
        dbref = FirebaseDatabase.getInstance().getReference("News")

        // Select * from news where details = ?
        //dbref.orderByChild("details").equalTo("testing").addValueEventListener(object : ValueEventListener {

        dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (newsSnapshot in snapshot.children) {
                        val news = newsSnapshot.getValue(News::class.java)
                        newsArrayList.add(news!!)
                    }

                    var adapter = MyAdapter(newsArrayList)

                    // swipeGesture start here
                    val swipeGesture = object : SwipeGesture(this@NewsActivity){

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            when(direction){
                                ItemTouchHelper.LEFT ->{
                                    var tempData = newsArrayList[viewHolder.adapterPosition].title
                                    var tempPosition = viewHolder.adapterPosition
                                    if(tempData != null){
                                        deleteData(tempData, tempPosition)
                                    }
                                    adapter.deleteItem(viewHolder.adapterPosition)
                                    //Toast.makeText(this@NewsActivity, "Item $tempData deleted", Toast.LENGTH_SHORT).show()

                                }

                            }

                        }

                    }

                    // To show the swipe gesture animation
                    val touchHelper = ItemTouchHelper(swipeGesture)
                    touchHelper.attachToRecyclerView(newsRecyclerView)

                    // Show adapter
                    newsRecyclerView.adapter = adapter

                    // Click to update
                    // Past to NewsUpdateActivity
                    adapter.setOnClickListener(object : MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            var tempTitle = newsArrayList[position].title
                            var tempDetails = newsArrayList[position].details
                            var tempDate = newsArrayList[position].date
                            //Toast.makeText(this@NewsActivity, "You clicked on item no. $tempTitle", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@NewsActivity, NewsUpdateActivity::class.java)
                            intent.putExtra("title", tempTitle)
                            intent.putExtra("details", tempDetails)
                            intent.putExtra("date", tempDate)
                            startActivity(intent)

                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun deleteData(title: String, position : Int){
        dbref = FirebaseDatabase.getInstance().getReference("News")
        dbref.child(title).removeValue().addOnSuccessListener {
            Toast.makeText(this@NewsActivity, "News $title deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@NewsActivity, "Fail to delete", Toast.LENGTH_SHORT).show()
        }
    }
}