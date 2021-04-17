package com.example.communityreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinner.adapter = ArrayAdapter(
        this,
        android.R.layout.simple_spinner_dropdown_item,
        CountryData.countryNames
        )
        button.setOnClickListener {
            val code = CountryData.countryAreaCodes[spinner.selectedItemPosition]
            if(mobileNumber.text.count() == 9){
                val i = Intent(this,OTPvalidation::class.java).apply {
                    putExtra("mobileNumber",code+mobileNumber.text.toString())
                }
                startActivity(i)

            } else {
                Toast.makeText(this,"Enter mobile number (without 0)",Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
}