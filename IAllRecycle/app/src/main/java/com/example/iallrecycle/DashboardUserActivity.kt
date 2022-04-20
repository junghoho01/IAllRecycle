package com.example.iallrecycle

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iallrecycle.adapters.NewsAdapter
import com.example.iallrecycle.adapters.RecycleWasteAdapter
import com.example.iallrecycle.databinding.ActivityDashboardUserBinding
import com.example.iallrecycle.dataclass.News
import com.example.iallrecycle.dataclass.RecycleWaste
import com.example.iallrecycle.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class DashboardUserActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityDashboardUserBinding

    // Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // For recycler view for news
    private lateinit var dbref: DatabaseReference
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsArrayList: ArrayList<News>

    // For recycler view for recycle item
    private lateinit var rWasteRecyclerView: RecyclerView
    private lateinit var rWasteArrayList: ArrayList<RecycleWaste>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding segment
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Button for home
        val homeActivity = DashboardUserActivity()
        val recycleMapActivity = MapsActivity()
        val userProfileActivity = UserProfileActivity()
        val scanQrCodeActivity = ScanQrCodeActivity()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.ic_home -> startActivity(Intent(this@DashboardUserActivity, homeActivity::class.java))
                R.id.ic_map -> startActivity(Intent(this@DashboardUserActivity, recycleMapActivity::class.java))
                R.id.ic_profile -> startActivity(Intent(this@DashboardUserActivity, userProfileActivity::class.java))
                R.id.ic_qrcode -> startActivity(Intent(this@DashboardUserActivity, scanQrCodeActivity::class.java))
            }
            true
        }

        // Declare recycler view
        newsRecyclerView = binding.displayNewsList
        newsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newsRecyclerView.setHasFixedSize(true)
        newsArrayList = arrayListOf<News>()
        getNewsData()
    }

    private fun checkUser() {
        // Get current user type
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            // Not logged in, user can stay in dashboard without login
            binding.subTitlev.text = "Not logged in"
        }
        else {
            // Logged in, get and show user info
            val email = firebaseUser.email
            var tempImage = ""

            //Declare recycler view for recyling record
            rWasteRecyclerView = binding.displayRecycleList
            rWasteRecyclerView.layoutManager = LinearLayoutManager(this)
            rWasteRecyclerView.setHasFixedSize(true)
            rWasteArrayList = arrayListOf<RecycleWaste>()
            getRecycleData()

            dbref = FirebaseDatabase.getInstance().getReference("Users")
            dbref.orderByChild("email").equalTo(firebaseUser.email.toString()).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    Toast.makeText(applicationContext, "Got image", Toast.LENGTH_SHORT).show()
                    if(snapshot.exists()){
                        for(userSnapshot in snapshot.children){
                            val userRecycler = userSnapshot.getValue(User::class.java)
//                            Toast.makeText(applicationContext, "Has ${userRecycler?.name}", Toast.LENGTH_SHORT).show()
                            tempImage = userRecycler?.profileImage.toString()
//                            Toast.makeText(applicationContext, "$tempImage", Toast.LENGTH_SHORT).show()
                            var day = ""
                            val storageRef = FirebaseStorage.getInstance().reference.child("images/$tempImage")
                            val localfile = File.createTempFile("tempImage", "jpeg")
                            storageRef.getFile(localfile).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                binding.profileImageButton.setImageBitmap(bitmap)
                            }
                            val c: Calendar = Calendar.getInstance()
                            val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)

                            if (timeOfDay >= 0 && timeOfDay < 12) {
                                day = "Morning !"
                            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                                day = "Afternoon !"
                            } else if (timeOfDay >= 16 && timeOfDay < 21) {
                                day = "Evening !"
                            } else if (timeOfDay >= 21 && timeOfDay < 24) {
                                day = "Night !"
                            }

                            binding.userName.text =  day + ", " + userRecycler?.name
                            binding.userPoint.text = userRecycler?.points + " Points"
                        }
                    }
                    //checkDataExist()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Data fail", Toast.LENGTH_SHORT).show()
                }

            })

            // Set to text view of toolbar
            binding.subTitlev.text = email
        }
    }

    private fun getNewsData(){
        dbref = FirebaseDatabase.getInstance().getReference("News")
        dbref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(newsSnapshot in snapshot.children){
                        val news = newsSnapshot.getValue(News::class.java)
                        newsArrayList.add(news!!)
                    }

                    newsRecyclerView.adapter = NewsAdapter(newsArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getRecycleData(){

        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null){
            // Not logged in, user can stay in dashboard without login
            binding.subTitlev.text = "Not logged in"
            Toast.makeText(applicationContext, "no data", Toast.LENGTH_SHORT).show()
        } else {
            dbref = FirebaseDatabase.getInstance().getReference("RecycleWaste")
            dbref.orderByChild("recylerEmail").equalTo(firebaseUser.email.toString()).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Toast.makeText(applicationContext, "Has data", Toast.LENGTH_SHORT).show()
                    if(snapshot.exists()){
                        for(dataSnapshot in snapshot.children){
                            val recycleWaste = dataSnapshot.getValue(RecycleWaste::class.java)
                            rWasteArrayList.add(recycleWaste!!)
                        }
                        rWasteRecyclerView.adapter = RecycleWasteAdapter(rWasteArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
//            Toast.makeText(applicationContext, "Has data", Toast.LENGTH_SHORT).show()
        }

    }

}