package com.example.zerodechet.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zerodechet.R
import kotlinx.android.synthetic.main.activity_hardware_detail.*

class HardwareDetailActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hardware_detail)
        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val ram = intent.getStringExtra("ram")
        val hardDiskDrive = intent.getStringExtra("hardDiskDrive")
        val processor = intent.getStringExtra("processor")
        val screenWidth = intent.getStringExtra("screenWidth")
        val otherComponents = intent.getStringExtra("otherComponents")
        txtTitle.setText(title)
        txtProcessor.setText(processor)
    }
}