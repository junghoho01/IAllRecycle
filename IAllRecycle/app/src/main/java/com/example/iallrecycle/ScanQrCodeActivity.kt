package com.example.iallrecycle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.iallrecycle.dataclass.RecycleWaste
import com.example.iallrecycle.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

// Set an image name
val formatter = SimpleDateFormat("yyyy-MM-dd : hh-mm-ss", Locale.getDefault())
val now = Date()
val FILE_NAME = formatter.format(now)

class ScanQrCodeActivity : AppCompatActivity() {

    // Code scanner declaration
    private lateinit var codeScanner: CodeScanner

    // Recycle item variable declaration
    private var glassAmt = "";
    private var paperAmt = "";
    private var plasticAmt = "";
    private var othersAmt = "";
    private var newPoint = 0

    // Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // Firestore
    private lateinit var database: DatabaseReference
    private lateinit var dbref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)

        // Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }else{
            startScanning()
        }
    }

    private fun startScanning(){
        val scannerView: CodeScannerView = findViewById(R.id.scannerView)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_SHORT).show()

                // Get the data and seperate it
                var tempHoldData = it.text
                val str = tempHoldData
                val delim = ":"

                // Seperate the data into list
                val list = str.split(delim)

                // Firebase init
                val firebaseUser = firebaseAuth.currentUser

                // Timestamp
                val timestamp = System.currentTimeMillis().toString()

                glassAmt = list[0]
                paperAmt = list[1]
                plasticAmt = list[2]
                othersAmt = list[3]

                var totalPoints = list[0].toInt() + list[1].toInt() + list[2].toInt() + list[3].toInt()

                if(firebaseUser!=null){
                    // Declare email
                    val email = firebaseUser.email

                    // Database declaration
                    database = FirebaseDatabase.getInstance().getReference("RecycleWaste")

                    val recyclewaste = RecycleWaste(glassAmt, paperAmt, plasticAmt, othersAmt, firebaseUser.email, FILE_NAME, timestamp)

                    // Save to firestore
                    database.child(timestamp).setValue(recyclewaste)
                        .addOnSuccessListener {
                            //Toast.makeText(applicationContext,"Successfully save the data",Toast.LENGTH_SHORT).show()

                            dbref = FirebaseDatabase.getInstance().getReference("Users")
                            dbref.orderByChild("uid").equalTo(firebaseUser?.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for(userSnapshot in snapshot.children) {
                                            val user = userSnapshot.getValue(User::class.java)
                                            val currentPoint = user?.points.toString().toInt()
                                            updatePoints(totalPoints.toString(), firebaseUser.uid, currentPoint)
                                            break
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                            //finish()
                            //startActivity(Intent(this, SuccessScanActivity::class.java))

                            // Pass data to print recycle item result
                            val intent = Intent(this@ScanQrCodeActivity, SuccessScanActivity::class.java)
                            intent.putExtra("glassAmt", glassAmt)
                            intent.putExtra("paperAmt", paperAmt)
                            intent.putExtra("plasticAmt", plasticAmt)
                            intent.putExtra("othersAmt", othersAmt)
                            intent.putExtra("totalPoint", totalPoints.toString())
                            startActivity(intent)

                        }.addOnFailureListener {
                            Toast.makeText(applicationContext,"Fail to save",Toast.LENGTH_SHORT).show()
                        }
                }

            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                startScanning()
            }else{
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume(){
        super.onResume()
        if(::codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause(){
        if(::codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()
    }

    private fun updatePoints(points: String, uid: String, currentPoint: Int){
        Toast.makeText(this, "$points & $uid & $currentPoint",Toast.LENGTH_SHORT).show()

        if(currentPoint != 0){
            newPoint = points.toInt() + currentPoint
        }
        else{
            newPoint = points.toInt()
        }

        //Toast.makeText(this, "new = $newPoint",Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance().getReference("Users")
            val recycleWaste = mapOf<String,String>(
                "points" to newPoint.toString(),
        )
        database.child(uid).updateChildren(recycleWaste).addOnSuccessListener {
            Toast.makeText(this@ScanQrCodeActivity, "New Point Updated",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@ScanQrCodeActivity, "Fail to update point",Toast.LENGTH_SHORT).show()
        }


    }

}