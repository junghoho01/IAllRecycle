package com.example.iallrecycle

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityDashboardUserBinding
import com.example.iallrecycle.databinding.ActivityUserProfileBinding
import com.example.iallrecycle.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private lateinit var progressDialog: ProgressDialog

class UserProfileActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityUserProfileBinding

    // Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // For recycler view for news
    private lateinit var dbref: DatabaseReference

    // Image URI
    lateinit var ImageUri : Uri

    // Set an image name
    val formatter = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.getDefault())
    val now = Date()
    val FILE_NAME = formatter.format(now)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding segment
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        getAllUserData()

        binding.profileImageButton.setOnClickListener {
            selectImage()
        }

        binding.updateeBtn.setOnClickListener {
            //Validate data
            validateData()
        }

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
                R.id.ic_home -> startActivity(Intent(this@UserProfileActivity, homeActivity::class.java))
                R.id.ic_map -> startActivity(Intent(this@UserProfileActivity, recycleMapActivity::class.java))
                R.id.ic_profile -> startActivity(Intent(this@UserProfileActivity, userProfileActivity::class.java))
                R.id.ic_qrcode -> startActivity(Intent(this@UserProfileActivity, scanQrCodeActivity::class.java))
            }
            true
        }
    }

    private fun getAllUserData() {

        // Get current user type
        val firebaseUser = firebaseAuth.currentUser

        if(firebaseUser != null){
            dbref = FirebaseDatabase.getInstance().getReference("Users")
            dbref.orderByChild("email").equalTo(firebaseUser?.email.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapshot in snapshot.children){
                            val normalUser = userSnapshot.getValue(User::class.java)
                            binding.nameEt.text = Editable.Factory.getInstance().newEditable(normalUser?.name)
                            binding.emailEt.text = Editable.Factory.getInstance().newEditable(normalUser?.email)
                            binding.contactNumberEt.text = Editable.Factory.getInstance().newEditable(normalUser?.phone)

                            val storageRef = FirebaseStorage.getInstance().reference.child("images/${normalUser?.profileImage}")
                            val localfile = File.createTempFile("tempImage", "jpeg")
                            storageRef.getFile(localfile).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                binding.profileImageButton.setImageBitmap(bitmap)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    // For capturing image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.profileImageButton.setImageURI(ImageUri)
        }
    }

    private fun validateData() {
        // get alll data
        val getName =  binding.nameEt.text.toString().trim()
        val getPhone = binding.contactNumberEt.text.toString().trim()
        //val getPassword = binding.passwordEt.text.toString().trim()

        if(getName.isEmpty()){
            // Empty name
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        }
        else if(binding.profileImageButton.getDrawable() == null){
            // No image taken
            Toast.makeText(this, "Upload an image...", Toast.LENGTH_SHORT).show()
        }
        else if(getPhone.isEmpty()){
            // Empty contact no
            Toast.makeText(this, "Enter contact number...", Toast.LENGTH_SHORT).show()
        }
        else if(!getPhone.matches(".*[0-9].*".toRegex())){
            // Contact no must be number
            Toast.makeText(this, "Number only for contact number..", Toast.LENGTH_SHORT).show()
        }
        else if(getPhone.length > 11) {
            Toast.makeText(this, "Contact no must not be exceed...", Toast.LENGTH_SHORT).show()
        }
        else if(getPhone.length < 10){
            Toast.makeText(this, "Invalid contact no...", Toast.LENGTH_SHORT).show()
        }
        else{
           /* if(getPassword.isEmpty()){
                updateAccount(getName, getPhone, "0")
            }else{*/
                updateAccount(getName, getPhone)
            }
        }


    private fun updateAccount(name: String, phone: String){

        dbref = FirebaseDatabase.getInstance().getReference("Users")

        // Get current user type
        val firebaseUser = firebaseAuth.currentUser

        progressDialog.setMessage("Updating profile...")
        progressDialog.show()

        /*if(password != "0"){*/
            val user = mapOf<String,String>(
                "name" to name,
                "phone" to phone,
            )

            dbref.child(firebaseUser?.uid.toString()).updateChildren(user).addOnSuccessListener {
                binding.nameEt.text.clear()
                //binding.passwordEt.text.clear()
                binding.contactNumberEt.text.clear()
                progressDialog.dismiss()
                Toast.makeText(this, "Successfully Update",Toast.LENGTH_SHORT).show()
            }
        /*else{
            val user = mapOf<String,String>(
                "name" to name,
                "phone" to phone
            )

            dbref.child(firebaseUser?.uid.toString()).updateChildren(user).addOnSuccessListener {
                binding.nameEt.text.clear()
                binding.passwordEt.text.clear()
                binding.contactNumberEt.text.clear()
                progressDialog.dismiss()
                firebaseUser?.updatePassword(password)
                Toast.makeText(this, "Successfully Update",Toast.LENGTH_SHORT).show()
            }*/

    }
}