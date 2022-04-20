package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityNewsUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivityNewsUpdateBinding
private lateinit var database: DatabaseReference

private lateinit var progressDialog: ProgressDialog

class NewsUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Binding declaration
        binding = ActivityNewsUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleNews : TextView = binding.editNewsTitle
        val detailsNews : TextView = binding.editNewsDetails
        val dateNews : TextView = binding.editNewsDate

        val bundle : Bundle?= intent.extras
        val title = bundle!!.getString("title")
        val details = bundle!!.getString("details")
        val date = bundle!!.getString("date")

        titleNews.text = title
        detailsNews.text = details
        dateNews.text = date

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }

        binding.updateBtn.setOnClickListener {

            // Get data
            val getDetails = binding.editNewsDetails.text.toString()
            val getTitle = binding.editNewsTitle.text.toString()
            val getDate = binding.editNewsDate.text.toString()

            updateData(getTitle,getDetails,getDate)

        }

    }

    private fun updateData(title: String, details: String, date: String) {

        database = FirebaseDatabase.getInstance().getReference("News")
        val news = mapOf<String,String>(
            "title" to title,
            "details" to details,
            "date" to date
        )

        progressDialog.setMessage("Creating News...")
        progressDialog.show()

        database.child(title).updateChildren(news).addOnSuccessListener {
            binding.editNewsTitle.text.clear()
            binding.editNewsDetails.text.clear()
            binding.editNewsDate.text.clear()
            progressDialog.dismiss()
            Toast.makeText(this, "Successfully Update",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to update",Toast.LENGTH_SHORT).show()
        }

    }
}