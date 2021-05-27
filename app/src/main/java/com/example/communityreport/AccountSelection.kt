package com.example.communityreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AccountSelection : AppCompatActivity() {
    private var adminBtn: Button? = null
    private var userBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_selection)

        adminBtn = findViewById(R.id.buttonAdmin);
        userBtn = findViewById(R.id.buttonUser);

        adminBtn?.setOnClickListener {
            val intent = Intent(this, ShowActivity::class.java)



            startActivity(intent)
        }

        userBtn?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)



            startActivity(intent)
        }
    }
}