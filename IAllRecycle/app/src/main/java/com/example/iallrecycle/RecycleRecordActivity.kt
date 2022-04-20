package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.adapters.MyAdapter
import com.example.iallrecycle.adapters.RecycleListAdapter
import com.example.iallrecycle.databinding.ActivityDashboardUserBinding
import com.example.iallrecycle.databinding.ActivityNewsBinding
import com.example.iallrecycle.databinding.ActivityRecycleRecordAddBinding
import com.example.iallrecycle.databinding.ActivityRecycleRecordBinding
import com.example.iallrecycle.dataclass.News
import com.example.iallrecycle.dataclass.RecycleWaste
import com.example.iallrecycle.gresture.SwipeGesture
import com.google.firebase.database.*

class RecycleRecordActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityRecycleRecordBinding

    // Recycler View
    private lateinit var dbref : DatabaseReference
    private lateinit var rRecordRecyclerView: RecyclerView
    private lateinit var rRecordArrayList: ArrayList<RecycleWaste>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivityRecycleRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rRecordRecyclerView = findViewById(R.id.rRecordList)
        rRecordRecyclerView.layoutManager = LinearLayoutManager(this)
        rRecordRecyclerView.setHasFixedSize(true)

        rRecordArrayList = arrayListOf<RecycleWaste>()

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, DashboardAdminActivity::class.java)) // Go back to previous screen
        }

        binding.addRecycleRecordBtn.setOnClickListener {
            startActivity(Intent(this@RecycleRecordActivity, RecycleRecordAddActivity ::class.java))
        }

        getRrecordData()
    }

    private fun getRrecordData() {
        dbref = FirebaseDatabase.getInstance().getReference("RecycleWaste")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (newsSnapshot in snapshot.children) {
                        val recycleWaste = newsSnapshot.getValue(RecycleWaste::class.java)
                        rRecordArrayList.add(recycleWaste!!)
                    }

                    var adapter = RecycleListAdapter(rRecordArrayList)

                    val swipeGesture = object : SwipeGesture(this@RecycleRecordActivity){

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            when(direction){
                                ItemTouchHelper.LEFT ->{
                                    var tempData = rRecordArrayList[viewHolder.adapterPosition].timestamp
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
                    touchHelper.attachToRecyclerView(rRecordRecyclerView)
//
                    // Show adapter
                    rRecordRecyclerView.adapter = adapter
//
//                    // Click to update
//                    // Past to NewsUpdateActivity
                    adapter.setOnClickListener(object : RecycleListAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            var time = rRecordArrayList[position].timestamp
                            var email = rRecordArrayList[position].recylerEmail
                            var date = rRecordArrayList[position].date
                            var glass = rRecordArrayList[position].glass
                            var paper = rRecordArrayList[position].paper
                            var plastic = rRecordArrayList[position].plastic
                            var others = rRecordArrayList[position].others

                            //Toast.makeText(this@NewsActivity, "You clicked on item no. $tempTitle", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RecycleRecordActivity, RecycleRecordUpdateActivity::class.java)
                            intent.putExtra("time", time)
                            intent.putExtra("email", email)
                            intent.putExtra("date", date)
                            intent.putExtra("glass", glass)
                            intent.putExtra("paper", paper)
                            intent.putExtra("plastic", plastic)
                            intent.putExtra("others", others)
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

    private fun deleteData(id: String, position : Int){
        dbref = FirebaseDatabase.getInstance().getReference("RecycleWaste")
        dbref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this@RecycleRecordActivity, "Record $id deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@RecycleRecordActivity, "Fail to delete", Toast.LENGTH_SHORT).show()
        }
    }
}