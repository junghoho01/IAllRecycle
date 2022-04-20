package com.example.iallrecycle

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.iallrecycle.databinding.ActivityGenerateQrcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class GenerateQrcodeActivity : AppCompatActivity() {

    // QR Code variable declaration
    private lateinit var ivQRCode: ImageView
    private lateinit var glassData: EditText
    private lateinit var paperData: EditText
    private lateinit var plasticData: EditText
    private lateinit var othersData: EditText
    private lateinit var btnGenerateQRCode: Button

    // View binding
    private lateinit var binding: ActivityGenerateQrcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding declaration
        binding = ActivityGenerateQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // QR code variable
        ivQRCode = binding.ivQRCode
        glassData = binding.glassData
        paperData = binding.paperData
        plasticData = binding.plasticData
        othersData = binding.othersData
        btnGenerateQRCode = binding.btnGenerateQRcode

        btnGenerateQRCode.setOnClickListener {
            val data = glassData.text.toString() + ":" + paperData.text.toString() + ":" + plasticData.text.toString() + ":" + othersData.text.toString()

            if(glassData.text.isEmpty() || paperData.text.isEmpty() || plasticData.text.isEmpty() || othersData.text.isEmpty()){
                Toast.makeText(this, "Enter some data..", Toast.LENGTH_SHORT).show()
            }else{
                val writer = QRCodeWriter()
                try{
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for(x in 0 until width){
                        for(y in 0 until height){
                            bmp.setPixel(x, y, if(bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    ivQRCode.setImageBitmap(bmp)
                }catch (e: WriterException){
                    e.printStackTrace()
                }

            }
        }
    }
}