package com.example.robocup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView
import android.content.Intent

class SettingsActivity : AppCompatActivity() {

    private lateinit var appConfig: AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Supprimer le titre par défaut dans la Toolbar
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

        appConfig = AppConfig(this)

        // Associer les vues
        val editRosbridgeUrl: EditText = findViewById(R.id.edit_rosbridge_url)
        val editTopicJoy: EditText = findViewById(R.id.edit_topic_joy)
        val editTopicCam1: EditText = findViewById(R.id.edit_topic_cam1)
        val editTopicCam2: EditText = findViewById(R.id.edit_topic_cam2)
        val editTopicCam3: EditText = findViewById(R.id.edit_topic_cam3)
        val editTopicCam4: EditText = findViewById(R.id.edit_topic_cam4)
        val editTopicFlipperFrontLeft: EditText = findViewById(R.id.edit_topic_flipper_front_left)
        val editTopicFlipperFrontRight: EditText = findViewById(R.id.edit_topic_flipper_front_right)
        val editTopicFlipperBack: EditText = findViewById(R.id.edit_topic_flipper_back)
        val btnSave: Button = findViewById(R.id.btn_save)

        // Charger les valeurs existantes dans les champs
        editRosbridgeUrl.setText(appConfig.rosbridgeUrl)
        editTopicJoy.setText(appConfig.topicJoy)
        editTopicCam1.setText(appConfig.topicCam1)
        editTopicCam2.setText(appConfig.topicCam2)
        editTopicCam3.setText(appConfig.topicCam3)
        editTopicCam4.setText(appConfig.topicCam4)
        editTopicFlipperFrontLeft.setText(appConfig.topicFlipperFrontLeft)
        editTopicFlipperFrontRight.setText(appConfig.topicFlipperFrontRight)
        editTopicFlipperBack.setText(appConfig.topicFlipperBack)

        // Sauvegarder les changements
        btnSave.setOnClickListener {
            appConfig.rosbridgeUrl = editRosbridgeUrl.text.toString()
            appConfig.topicJoy = editTopicJoy.text.toString()
            appConfig.topicCam1 = editTopicCam1.text.toString()
            appConfig.topicCam2 = editTopicCam2.text.toString()
            appConfig.topicCam3 = editTopicCam3.text.toString()
            appConfig.topicCam4 = editTopicCam4.text.toString()
            appConfig.topicFlipperFrontLeft = editTopicFlipperFrontLeft.text.toString()
            appConfig.topicFlipperFrontRight = editTopicFlipperFrontRight.text.toString()
            appConfig.topicFlipperBack = editTopicFlipperBack.text.toString()

            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
