package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.adapters.RecycleListAdapter
import com.example.iallrecycle.adapters.UserAdapter
import com.example.iallrecycle.databinding.ActivityRecycleRecordBinding
import com.example.iallrecycle.databinding.ActivityUserListBinding
import com.example.iallrecycle.dataclass.RecycleWaste
import com.example.iallrecycle.dataclass.User
import com.example.iallrecycle.gresture.SwipeGesture
import com.google.firebase.database.*

class UserListActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityUserListBinding

    // Recycler View
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRecyclerView = findViewById(R.id.userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<User>()

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, DashboardAdminActivity::class.java)) // Go back to previous screen
        }

        binding.addUserBtn.setOnClickListener {
            startActivity(Intent(this@UserListActivity, UserListAddActivity ::class.java))
        }

        getRrecordData()
    }

    private fun getRrecordData() {
        dbref = FirebaseDatabase.getInstance().getReference("Users")

        dbref.orderByChild("userType").equalTo("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (newsSnapshot in snapshot.children) {
                        val user = newsSnapshot.getValue(User::class.java)
                        userArrayList.add(user!!)
                    }

                    var adapter = UserAdapter(userArrayList)

                    val swipeGesture = object : SwipeGesture(this@UserListActivity){

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            when(direction){
                                ItemTouchHelper.LEFT ->{
                                    var tempData = userArrayList[viewHolder.adapterPosition].uid
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
                    touchHelper.attachToRecyclerView(userRecyclerView)
//
                    // Show adapter
                    userRecyclerView.adapter = adapter
//
//                    // Click to update
//                    // Past to NewsUpdateActivity
                    adapter.setOnClickListener(object : UserAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            var uid = userArrayList[position].uid
                            var name = userArrayList[position].name
                            var email = userArrayList[position].email
                            var phone = userArrayList[position].phone
                            var picture = userArrayList[position].profileImage

                            //Toast.makeText(this@NewsActivity, "You clicked on item no. $tempTitle", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@UserListActivity, UserListUpdateActivity::class.java)
                            intent.putExtra("uid", uid)
                            intent.putExtra("name", name)
                            intent.putExtra("email", email)
                            intent.putExtra("phone", phone)
                            intent.putExtra("picture", picture)
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
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this@UserListActivity, "Record $id deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@UserListActivity, "Fail to delete", Toast.LENGTH_SHORT).show()
        }
    }
}