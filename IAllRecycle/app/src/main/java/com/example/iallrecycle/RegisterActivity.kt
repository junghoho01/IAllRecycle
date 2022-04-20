package com.example.iallrecycle

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.iallrecycle.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Any arbitary number
private const val REQUEST_CODE = 42
private lateinit var photoFile: File

class RegisterActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityRegisterBinding

    // Firebase section
    private lateinit var firebaseAuth: FirebaseAuth

    // Progress dialog
    private lateinit var progressDialog: ProgressDialog

    // Set an image name
    val formatter = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.getDefault())
    val now = Date()
    val FILE_NAME = formatter.format(now)

    // Image URI
    lateinit var ImageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding section
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Handle back button click, goto previous screen
        binding.backBtn.setOnClickListener {
            onBackPressed() // Go back to previous screen
        }

        // Handle click, begin register
        binding.registerBtn.setOnClickListener {
            // Step 1: Input data
            // Step 2: Validate data
            // Step 3: Create account - Firebase auth
            // Step 4: Save user info - Firebase Realtime Database
            validateData()
        }

        binding.registerRecycleBtn.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, RegisterRecycleActivity::class.java))
        }

        binding.btnTakePicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            // This DOESN'T work for API >= 24 (Starting 2016)
            val fileProvider =  FileProvider.getUriForFile(this, "com.example.iallrecycle.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if(takePictureIntent.resolveActivity(this.packageManager) != null){
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }else{
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }

        // Select image
        binding.btnUploadPicture.setOnClickListener {
            selectImage()
        }
    }

    private fun getPhotoFile(fileName: String): File {
        // Use 'getExternalFilesDir' on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    // Login variable declaration
    private var name = ""
    private var email = ""
    private var password = ""
    private var contactNo = ""

    private fun validateData() {
        // Step 1: Input data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        contactNo = binding.contactNumberEt.text.toString()

        val cPassword = binding.cPasswordEt.text.toString().trim()

        // Step 2: Validate data
        if(name.isEmpty()){
            // Empty name
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // Invalid email address pattern
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            // Empty password
            Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show()
        }
        else if(cPassword.isEmpty()){
            // Empty confirm password
            Toast.makeText(this, "Enter confirm password...", Toast.LENGTH_SHORT).show()
        }
        else if(password != cPassword){
            // Password mismatch
            Toast.makeText(this, "Password not match...", Toast.LENGTH_SHORT).show()
        }
        else if(binding.imageView.getDrawable() == null){
            // No image taken
            Toast.makeText(this, "Capture an image...", Toast.LENGTH_SHORT).show()
        }
        else if(contactNo.isEmpty()){
            // Empty contact no
            Toast.makeText(this, "Enter contact number...", Toast.LENGTH_SHORT).show()
        }
        else if(!contactNo.matches(".*[0-9].*".toRegex())){
            // Contact no must be number
            Toast.makeText(this, "Number only for contact number..", Toast.LENGTH_SHORT).show()
        }
        else if(contactNo.length > 11) {
            Toast.makeText(this, "Contact no must not be exceed...", Toast.LENGTH_SHORT).show()
        }
        else if(contactNo.length < 10){
            Toast.makeText(this, "Invalid contact no...", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
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
                Toast.makeText(this, "Fail to create due to ${it.message}", Toast.LENGTH_SHORT).show()
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
        hashMap["profileImage"] = FILE_NAME
        hashMap["userType"] = "user" //data -> user/admin
        hashMap["timestamp"] = timestamp
        hashMap["points"] = "0"
        hashMap["phone"] = contactNo

        uploadImage()

        // Set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // user info saved
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@RegisterActivity, SuccessScanActivity::class.java)
//                intent.putExtra("imguri", ImageUri)
//                startActivity(intent)
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                // Fail to add data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to save user info - ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImage() {

        val storageReference = FirebaseStorage.getInstance().getReference("images/$FILE_NAME")

        storageReference.putFile(ImageUri).
        addOnSuccessListener {
        }.addOnFailureListener {
        }
    }

    // For capturing image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //val takenImage = data?.extras?.get("data") as Bitmap
            var bitmapImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.imageView.setImageBitmap(bitmapImage)
            ImageUri = getImageUri(applicationContext, bitmapImage)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.imageView.setImageURI(ImageUri)
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }
}