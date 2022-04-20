package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iallrecycle.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardAdminActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityDashboardAdminBinding

    // Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding initialization
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Handle click, logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        // Get current user type
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            // Not logged in, go to main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else {
            // Logged in, get and show user info
            val email = firebaseUser.email

            // Set to text view of toolbar
            binding.subTitlev.text = email

            binding.newsBtn.setOnClickListener {
                startActivity(Intent(this, NewsActivity::class.java))
                finish()
            }

            binding.userBtn.setOnClickListener {
                startActivity(Intent(this, UserListActivity::class.java))
                finish()
            }

            binding.recycleBtn.setOnClickListener {
                startActivity(Intent(this, RecycleRecordActivity::class.java))
                finish()
            }
        }
    }
}