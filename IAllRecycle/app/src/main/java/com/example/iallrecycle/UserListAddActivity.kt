package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityUserListAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class UserListAddActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityUserListAddBinding

    // Firestore
    private lateinit var firebaseAuth: FirebaseAuth

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
        binding = ActivityUserListAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Handle back button click, goto previous screen
        binding.goBackBtn.setOnClickListener {
            onBackPressed() // Go back to previous screen
        }

        // Handle click, begin register
        binding.createBtn.setOnClickListener {
            // Step 1: Input data
            // Step 2: Validate data
            // Step 3: Create account - Firebase auth
            // Step 4: Save user info - Firebase Realtime Database
            validateData()

        }

        // select on image button
        binding.selectImageBtn.setOnClickListener{
            selectImage()
        }
    }

    // Login variable declaration
    private var name = ""
    private var email = ""
    private var password = ""
    private var phone = ""
    //private var confirmPassword = ""

    private fun validateData() {
        // Step 1: Input data
        name = binding.userNameCreate.text.toString().trim()
        email = binding.userEmailCreate.text.toString().trim()
        phone = binding.phoneNumberCreate.text.toString().trim()
        password = binding.passwordCreate.text.toString().trim()
        val cPassword = binding.cfpasswordCreate.text.toString().trim()

        // Step 2: Validate data
        if (name.isEmpty()) {
            // Empty name
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Invalid email address pattern
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            // Empty password
            Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
            // Empty confirm password
            Toast.makeText(this, "Enter confirm password...", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            // Password mismatch
            Toast.makeText(this, "Password not match...", Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty()) {
            // Empty contact no
            Toast.makeText(this, "Enter contact number...", Toast.LENGTH_SHORT).show()
        } else if (!phone.matches(".*[0-9].*".toRegex())) {
            // Contact no must be number
            Toast.makeText(this, "Number only for contact number..", Toast.LENGTH_SHORT).show()
        } else if (phone.length > 11) {
            Toast.makeText(this, "Contact no must not be exceed...", Toast.LENGTH_SHORT).show()
        } else if (phone.length < 10) {
            Toast.makeText(this, "Invalid contact no...", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
            //uploadImage()
        }
    }

    private fun createUserAccount() {
        // Step 3: Create account - Firebase auth

        // Show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        // Create user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Account created
                // Add user data to db
                updateUserInfo()
            }
            .addOnFailureListener {
                // Account fail to create
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to create due to ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateUserInfo() {
        // Step 4: Save user info - Firebase Realtime Database
        progressDialog.setMessage("Saving user info...")

        // Timestamp
        val timestamp = System.currentTimeMillis()

        // Get current user uid
        val uid = firebaseAuth.uid

        // Setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = fileName
        hashMap["userType"] = "user" //data -> user/admin
        hashMap["phone"] = phone
        hashMap["timestamp"] = timestamp
        hashMap["points"] = "0"

        // Set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                uploadImage()

                // user info saved
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@UserListAddActivity, DashboardUserActivity::class.java))
                finish()

            }
            .addOnFailureListener {
                // Fail to add data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to save user info - ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    // upload image function
    private fun uploadImage() {

        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).
        addOnSuccessListener {
            binding.userImage.setImageURI(null)
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"New user added", Toast.LENGTH_SHORT).show()
            binding.userNameCreate.text.clear()
            binding.userEmailCreate.text.clear()
            binding.phoneNumberCreate.text.clear()
            binding.passwordCreate.text.clear()
            binding.cfpasswordCreate.text.clear()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Fail to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    // preview image code
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.userImage.setImageURI(ImageUri)
        }

    }
}