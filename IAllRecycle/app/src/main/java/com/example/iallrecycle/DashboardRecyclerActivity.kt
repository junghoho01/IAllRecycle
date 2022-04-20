package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iallrecycle.databinding.ActivityDashboardAdminBinding
import com.example.iallrecycle.databinding.ActivityDashboardRecyclerBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardRecyclerActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityDashboardRecyclerBinding

    // Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding initialization
        binding = ActivityDashboardRecyclerBinding.inflate(layoutInflater)
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

            binding.qrBtn.setOnClickListener {
                startActivity(Intent(this, GenerateQrcodeActivity::class.java))
                finish()
            }

        }
    }
}