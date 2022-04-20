package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityUserListUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivityUserListUpdateBinding
private lateinit var database: DatabaseReference
private lateinit var progressDialog: ProgressDialog

class UserListUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Binding declaration
        binding = ActivityUserListUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameUser: TextView = binding.editUserName
        val emailUser: TextView = binding.editUserEmail
        val phoneUser: TextView = binding.editUserPhone
        // val passwordUser: TextView = binding.editUserPassword

        val bundle: Bundle? = intent.extras

        val uid = bundle!!.getString("uid")
        val name = bundle!!.getString("name")
        val email = bundle!!.getString("email")
        val phone = bundle!!.getString("phone")
        //val picture = bundle!!.getString("picture")

        // display text
        nameUser.text = name
        emailUser.text = email
        phoneUser.text = phone
        // passwordUser.text = password

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
            finish()
        }

        binding.updateBtn.setOnClickListener {

            // Get data
            val getName = binding.editUserName.text.toString()
            val getEmail = binding.editUserEmail.text.toString()
            val getPhone = binding.editUserPhone.text.toString()

            updateData(getName, getEmail, getPhone, uid.toString())
        }

    }

    private fun updateData(name: String, email: String, phone: String, uid: String) {

        database = FirebaseDatabase.getInstance().getReference("Users")
        val user = mapOf<String, String>(
            "name" to name,
            "email" to email,
            "phone" to phone,
        )

        progressDialog.setMessage("Updating Users...")
        progressDialog.show()

        database.child(uid).updateChildren(user).addOnSuccessListener {
            binding.editUserName.text.clear()
            binding.editUserEmail.text.clear()
            binding.editUserPhone.text.clear()
            progressDialog.dismiss()
            Toast.makeText(this, "Successfully Update", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }
}
