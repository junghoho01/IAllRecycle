package com.example.iallrecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.iallrecycle.databinding.ActivitySuccessScanBinding
import org.w3c.dom.Text

// View binding
private lateinit var binding: ActivitySuccessScanBinding

class SuccessScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Declare binding
        binding = ActivitySuccessScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Declare variables for text view
        val glassData : TextView = binding.glassDataTv
        val plasticData : TextView = binding.plasticDataTv
        val paperData : TextView = binding.paperDataTv
        val othersData : TextView = binding.otherDataTv
        val totalPointData : TextView = binding.pointDataTv

        // Get the data from previous activity
        val bundle : Bundle? = intent.extras
        val glassAmt = bundle!!.getString("glassAmt")
        val paperAmt = bundle!!.getString("paperAmt")
        val plasticAmt = bundle!!.getString("plasticAmt")
        val othersAmt = bundle!!.getString("othersAmt")
        val totalPoints = bundle!!.getString("totalPoint")

        // Set the value on the page
        glassData.text = "x  " + glassAmt
        plasticData.text = "x  " + paperAmt
        paperData.text = "x  " + plasticAmt
        othersData.text = "x  " + othersAmt
        totalPointData.text = totalPoints + "   pts"

        binding.okayBtn.setOnClickListener {
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }
    }
}