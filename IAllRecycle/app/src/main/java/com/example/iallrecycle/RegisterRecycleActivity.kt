package com.example.iallrecycle

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.audiofx.BassBoost
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.iallrecycle.databinding.ActivityRegisterBinding
import com.example.iallrecycle.databinding.ActivityRegisterRecycleBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.jar.Pack200

class RegisterRecycleActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityRegisterRecycleBinding

    // Firebase section
    private lateinit var firebaseAuth: FirebaseAuth

    // Progress dialog
    private lateinit var progressDialog: ProgressDialog

    // To get user location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        // Binding section
        binding = ActivityRegisterRecycleBinding.inflate(layoutInflater)
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
        binding.registerRecycleBtn.setOnClickListener {
            // Step 1: Input data
            // Step 2: Validate data
            // Step 3: Create account - Firebase auth
            // Step 4: Save user info - Firebase Realtime Database
            validateData()
        }
    }

    // Login recycle variable declaration
    private var companyName = ""
    private var companyEmail = ""
    private var password = ""
    private var ownerName = ""
    private var contactNo = ""
    private var tempLongitude = ""
    private var tempLatitude = ""

    private fun validateData(){
        // Step 1: Input data
        companyName = binding.companyNameEt.text.toString().trim()
        companyEmail = binding.companyEmailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()
        ownerName = binding.ownerNameEt.text.toString().trim()
        contactNo = binding.contactNumberEt.text.toString()

        //Step 2: Validate data
        if(companyName.isEmpty()){
            // Empty name
            Toast.makeText(this, "Enter your company name...", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(companyEmail).matches()){
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
        else if(ownerName.isEmpty()){
            // Empty owner name
            Toast.makeText(this, "Enter your owner name...", Toast.LENGTH_SHORT).show()
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
            if(tempLatitude.isEmpty() || tempLatitude.isEmpty()){
                Toast.makeText(this, "Please enable you GPS and restart the application", Toast.LENGTH_SHORT).show()
            }
            else {
                createRecycleAccount()
            }
        }
    }

    private fun createRecycleAccount() {
        // Step 3: Create account - Firebase auth

        // Show progress
        progressDialog.setMessage("Creating Recycle Account...")
        progressDialog.show()

        // Create user in firebase
        firebaseAuth.createUserWithEmailAndPassword(companyEmail, password)
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
        hashMap["companyEmail"] = companyEmail
        hashMap["companyName"] = companyName
        hashMap["profileImage"] = ""
        hashMap["userType"] = "recyler" //data -> user/admin
        hashMap["ownerName"] = ownerName
        hashMap["contactNo"] = contactNo
        hashMap["timestamp"] = timestamp
        hashMap["longitude"] = tempLongitude
        hashMap["latitude"] = tempLatitude

        // Set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // user info saved
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterRecycleActivity, DashboardRecyclerActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                // Fail to add data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to save user info - ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun getCurrentLocation() {
        if(checkPermission()){

            if(isLocationEnabled()){
                // Get the final longitude and latitude
                if(ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                )!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                )!= PackageManager.PERMISSION_GRANTED){
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location: Location?=task.result
                    if(location == null){
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Location retrieved", Toast.LENGTH_SHORT).show()
                        tempLatitude = location.latitude.toString()
                        tempLongitude = location.longitude.toString()
                    }
                }
            }
            else {
                // Open the settings here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }

        }
        else {
            // Request permission here
            requestPermission()
        }

    }

    private fun isLocationEnabled(): Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun checkPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        } else {
            return false
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){

            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }

        }

    }
}