package com.example.robocup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONObject
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView

class ControlArmActivity : AppCompatActivity() {

    private lateinit var appConfig: AppConfig
    private lateinit var rosbridgeClient: RosbridgeClient
    private lateinit var armCameraTopic: String
    private lateinit var armControlTopic: String
    private lateinit var armStatusTopic: String
    private lateinit var armCamImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_arm)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Boutons de la Toolbar
        val buttonArmActivity = findViewById<ImageView>(R.id.button_arm_activity)
        val buttonMainActivity = findViewById<Button>(R.id.button_main_activity)
        val buttonSettingsActivity = findViewById<ImageView>(R.id.button_setting_activity)
        // Configurer les clics des boutons
        buttonArmActivity.setOnClickListener { // Lancer "ControlArmActivity"
            val intent = Intent(this, ControlArmActivity::class.java); startActivity(intent)
        }
        buttonMainActivity.setOnClickListener { // Lancer "SettingsActivity"
            val intent = Intent(this, SettingsActivity::class.java); startActivity(intent)
        }
        buttonSettingsActivity.setOnClickListener { // Lancer sur "MainActivity"
            val intent = Intent(this, MainActivity::class.java); startActivity(intent)
        }



        // Obtenir l'instance partagée de RosbridgeClient
        rosbridgeClient = RosbridgeClientManager.getInstance(this)
        armCameraTopic = appConfig.topic_armcam
        armCamImageView = findViewById(R.id.armImageView)

        armControlTopic = appConfig.topic_arm_control
        armStatusTopic = appConfig.topic_arm_status


        Handler(Looper.getMainLooper()).postDelayed({
            subscribeToCameraTopic(armCameraTopic, armCamImageView)
        }, 500)



    }

    override fun onResume() {
        super.onResume()
        if (!rosbridgeClient.getIsConnected()) {
            rosbridgeClient.connect() // Rétablir la connexion si elle a été fermée
        }
        Handler(Looper.getMainLooper()).postDelayed({
            subscribeToCameraTopic(armCameraTopic, armCamImageView)
        }, 500)
    }

    private fun subscribeToCameraTopic(topic: String, imageView: ImageView) {
        val subscribeMessage = JSONObject().apply {
            put("op", "subscribe")
            put("topic", topic)
        }
        rosbridgeClient.sendMessage(subscribeMessage.toString())
        // Ajouter un listener spécifique pour chaque topic
        rosbridgeClient.addListener(topic) { message: String ->
            handleMessage(message, topic, imageView)
        }
    }

    private fun handleMessage(message: String, topic: String, imageView: ImageView) {
        var jsonMessage = JSONObject(message)
        Log.d("MainActivity", "Message from $topic: \n$message")

        if (jsonMessage.has("msg")) {
            jsonMessage = jsonMessage.getJSONObject("msg")
            if (jsonMessage.has("data")) {
                val imageString = jsonMessage.getString("data")
                displayImage(imageString, imageView)
            }
        }
    }

    private fun displayImage(imageString: String, imageView: ImageView) {
        try {
            val imageData = Base64.decode(imageString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

            runOnUiThread {
                imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error decoding and displaying image: ${e.message}")
        }
    }


}