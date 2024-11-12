package com.example.robocup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ControlArmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_arm)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val buttonOne = findViewById<ImageView>(R.id.button_one)
        val buttonTwo = findViewById<ImageView>(R.id.button_two)
        val buttonTitle = findViewById<Button>(R.id.button_title)

        buttonOne.setOnClickListener {
            val intent = Intent(this, ControlArmActivity::class.java)
            startActivity(intent)
        }

        buttonTwo.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        buttonTitle.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}