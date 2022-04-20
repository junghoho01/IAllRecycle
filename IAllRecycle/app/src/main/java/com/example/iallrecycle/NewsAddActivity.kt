package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityMainBinding
import com.example.iallrecycle.databinding.ActivityNewsAddBinding
import com.example.iallrecycle.dataclass.News
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class NewsAddActivity : AppCompatActivity() {

    // Binding
    private lateinit var binding: ActivityNewsAddBinding

    // Firestore
    private lateinit var database: DatabaseReference

    // Progress dialog
    private lateinit var progressDialog: ProgressDialog

    // Image URI
    lateinit var ImageUri : Uri

    // Set an image name
    val formatter = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.getDefault())
    val now = Date()
    val fileName = formatter.format(now)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivityNewsAddBinding.inflate(layoutInflater)

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        setContentView(binding.root)

        // Timestamp
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val timestamp = currentDate

        binding.submitBtn.setOnClickListener {
            val title = binding.newsTitle.text.toString()
            val description = binding.newsDetails.text.toString()
            val date = timestamp
            val picture = fileName

            // Database declaration
            database = FirebaseDatabase.getInstance().getReference("News")

            // News news = new News(title, date, details)
            val news = News(title, date, description, picture)

            // Show progress
            progressDialog.setMessage("Creating News...")
            progressDialog.show()

            // Save to firestore
            database.child(title).setValue(news)
                .addOnSuccessListener {
                    // Upload the image
                    uploadImage()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"Fail to add news",Toast.LENGTH_SHORT).show()
                }

        }

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }
        binding.selectImageBtn.setOnClickListener {
            selectImage()
        }
    }

    private fun uploadImage() {

        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).
            addOnSuccessListener {
                binding.firebaseImage.setImageURI(null)
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"New news added",Toast.LENGTH_SHORT).show()
                binding.newsTitle.text.clear()
                binding.newsDetails.text.clear()
                binding.newsDate.text.clear()
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Fail to upload image",Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.firebaseImage.setImageURI(ImageUri)
        }

    }
}