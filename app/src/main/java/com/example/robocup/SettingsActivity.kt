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


        appConfig = AppConfig(this)

        // Associer les vues
        val editRosbridgeUrl: EditText = findViewById(R.id.edit_rosbridge_url)
        val editTopicJoy: EditText = findViewById(R.id.edit_topic_joy)
        val editTopicCam1: EditText = findViewById(R.id.edit_topic_cam1)
        val editTopicCam2: EditText = findViewById(R.id.edit_topic_cam2)
        val editTopicCam3: EditText = findViewById(R.id.edit_topic_cam3)
        val editTopicCam4: EditText = findViewById(R.id.edit_topic_cam4)
        val editTopicCamArm: EditText = findViewById(R.id.edit_topic_cam_arm)
        val editTopicArmControl: EditText = findViewById(R.id.edit_topic_arm_control)
        val editTopicArmStatus: EditText = findViewById(R.id.edit_topic_arm_status)
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
        editTopicCamArm.setText(appConfig.topic_armcam)
        editTopicArmStatus.setText(appConfig.topic_arm_status)
        editTopicArmControl.setText(appConfig.topic_arm_control)

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
            appConfig.topic_armcam = editTopicCamArm.text.toString()
            appConfig.topic_arm_control = editTopicArmControl.text.toString()
            appConfig.topic_arm_status = editTopicArmStatus.text.toString()
            appConfig.topicFlipperFrontLeft = editTopicFlipperFrontLeft.text.toString()
            appConfig.topicFlipperFrontRight = editTopicFlipperFrontRight.text.toString()
            appConfig.topicFlipperBack = editTopicFlipperBack.text.toString()

            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
