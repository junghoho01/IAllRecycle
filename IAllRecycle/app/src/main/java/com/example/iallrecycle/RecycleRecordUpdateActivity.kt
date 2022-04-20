package com.example.iallrecycle

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityNewsUpdateBinding
import com.example.iallrecycle.databinding.ActivityRecycleRecordUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivityRecycleRecordUpdateBinding
private lateinit var database: DatabaseReference
private lateinit var progressDialog: ProgressDialog

class RecycleRecordUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        // Binding declaration
        binding = ActivityRecycleRecordUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val glass: TextView = binding.glassEt
        val paper: TextView = binding.paperEt
        val plastic: TextView = binding.plasticEt
        val others: TextView = binding.othersEt

        val bundle : Bundle?= intent.extras
        val getGlass = bundle!!.getString("glass")
        val getPaper = bundle!!.getString("paper")
        val getPlastic = bundle!!.getString("plastic")
        val getOthers = bundle!!.getString("others")
        val timestamp = bundle!!.getString("time")

        glass.text = getGlass
        paper.text = getPaper
        plastic.text = getPlastic
        others.text = getOthers

        binding.goBackBtn.setOnClickListener {
            startActivity(Intent(this, RecycleRecordActivity::class.java))
            finish()
        }

        binding.submitBtn.setOnClickListener {

            // Get data
            val getGlass = binding.glassEt.text.toString()
            val getPaper = binding.paperEt.text.toString()
            val getPlastic = binding.plasticEt.text.toString()
            val getOthers = binding.othersEt.text.toString()

            updateData(getGlass,getPaper,getPlastic,getOthers, timestamp.toString())
        }

    }

    private fun updateData(glass: String, paper: String, plastic: String, others: String, timestamp: String) {
        database = FirebaseDatabase.getInstance().getReference("RecycleWaste")
        val waste = mapOf<String,String>(
            "glass" to glass,
            "paper" to paper,
            "plastic" to plastic,
            "others" to others
        )

        progressDialog.setMessage("Updating record...")
        progressDialog.show()

        database.child(timestamp).updateChildren(waste).addOnSuccessListener {
            binding.glassEt.text.clear()
            binding.paperEt.text.clear()
            binding.othersEt.text.clear()
            binding.plasticEt.text.clear()
            progressDialog.dismiss()
            Toast.makeText(this, "Successfully Update", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }
}