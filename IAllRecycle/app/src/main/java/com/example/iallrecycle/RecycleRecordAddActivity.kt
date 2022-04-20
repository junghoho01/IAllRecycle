package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityNewsAddBinding
import com.example.iallrecycle.databinding.ActivityRecycleRecordAddBinding
import com.example.iallrecycle.databinding.ActivityRecycleRecordBinding
import com.example.iallrecycle.dataclass.RecycleWaste
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class RecycleRecordAddActivity : AppCompatActivity() {

    // Set an image name
    val formatter = SimpleDateFormat("yyyy-MM-dd : hh-mm-ss", Locale.getDefault())
    val now = Date()
    val FILE_NAME = formatter.format(now)

    // Binding
    private lateinit var binding: ActivityRecycleRecordAddBinding

    // Firestore
    private lateinit var database: DatabaseReference

    // Firestore
    private lateinit var dbref: DatabaseReference

    // Progress dialog
    private lateinit var progressDialog: ProgressDialog

    // Firebase section
    private lateinit var firebaseAuth: FirebaseAuth

    val timestamp = System.currentTimeMillis()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivityRecycleRecordAddBinding.inflate(layoutInflater)

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            // Get all data and validate
            getAllData()
        }

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }

    private fun getAllData() {
        //Get all the data
        var glass = binding.glassEt.text.toString()
        var paper = binding.paperEt.text.toString()
        var plastic = binding.plasticEt.text.toString()
        var others = binding.othersEt.text.toString()

        //Start the validation
        if(glass.isEmpty()){
            // Empty glass quantity
            Toast.makeText(this, "Enter glass quantity...", Toast.LENGTH_SHORT).show()
        }else if (paper.isEmpty()){
            Toast.makeText(this, "Enter paper quantity...", Toast.LENGTH_SHORT).show()
        }else if (plastic.isEmpty()){
            Toast.makeText(this, "Enter plastic quantity...", Toast.LENGTH_SHORT).show()
        }else if (others.isEmpty()){
            Toast.makeText(this, "Enter others quantity...", Toast.LENGTH_SHORT).show()
        }else if(!glass.matches(".*[0-9].*".toRegex())){
            Toast.makeText(this, "Number only for glass quantity..", Toast.LENGTH_SHORT).show()
        }else if(!plastic.matches(".*[0-9].*".toRegex())){
            Toast.makeText(this, "Number only for plastic quantity..", Toast.LENGTH_SHORT).show()
        }else if(!paper.matches(".*[0-9].*".toRegex())){
            Toast.makeText(this, "Number only for paper quantity..", Toast.LENGTH_SHORT).show()
        }else if(!others.matches(".*[0-9].*".toRegex())){
            Toast.makeText(this, "Number only for others quantity..", Toast.LENGTH_SHORT).show()
        }else {
            createRecycleRecord(glass, paper, plastic, others)
        }
    }

    private fun createRecycleRecord(glass: String, paper: String, plastic: String, others: String) {
        // Database declaration
        database = FirebaseDatabase.getInstance().getReference("RecycleWaste")

        val waste = RecycleWaste(glass, plastic, paper, others, firebaseAuth.currentUser?.email, FILE_NAME, timestamp.toString())

        // Show progress
        progressDialog.setMessage("Creating recycle record...")
        progressDialog.show()

        // Save to firestore
        database.child(timestamp.toString()).setValue(waste)
            .addOnSuccessListener {
                Toast.makeText(applicationContext,"Successfully save the data",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RecycleRecordAddActivity, ActivityRecycleRecordBinding::class.java))
            }.addOnFailureListener {
                Toast.makeText(applicationContext,"Fail to save",Toast.LENGTH_SHORT).show()
            }
    }


}