package com.example.robocup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.biometrics.BiometricManager.Strings
import android.os.Bundle
import android.text.format.Formatter
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONObject
import android.net.wifi.WifiManager
import android.widget.SeekBar
import android.view.Menu
import android.view.MenuItem
import androidx.collection.longListOf

class MainActivity : AppCompatActivity(), JoystickView.JoystickListener {


    private lateinit var appConfig: AppConfig
    private lateinit var ipTextView: TextView
    private lateinit var rosbridgeClient: RosbridgeClient
    private lateinit var imageViews: List<ImageView>
    private lateinit var joystickView: JoystickView
    private lateinit var cameraTopics: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Supprimer le titre par défaut dans la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Configuration de la Toolbar

        appConfig = AppConfig(this)

        // Associer les vues
        ipTextView = findViewById(R.id.ip)

        cameraTopics = listOf(
            appConfig.topicCam1,
            appConfig.topicCam2,
            appConfig.topicCam3,
            appConfig.topicCam4
        )
        imageViews = listOf(
            findViewById(R.id.ViewFL),
            findViewById(R.id.ViewFR),
            findViewById(R.id.ViewBL),
            findViewById(R.id.ViewBR)
        )

        joystickView = findViewById(R.id.joystickView)
        joystickView.setJoystickListener(this) // Set the listener

        val sliderAvantGauche = findViewById<SeekBar>(R.id.sliderFrontLeft)
        val sliderAvantDroit = findViewById<SeekBar>(R.id.sliderFrontRight)
        val sliderArriere = findViewById<SeekBar>(R.id.sliderBack)

        // Initialiser le client ROSBridge
        rosbridgeClient = RosbridgeClient(appConfig.rosbridgeUrl, this)
        rosbridgeClient.connect()
        val textString = "IP Address: 192.168.1.12 || ${appConfig.rosbridgeUrl} Connected: ${rosbridgeClient.getIsConnected()}"
        ipTextView.text = textString


        // Gérer les événements des sliders
        sliderAvantGauche.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                sendFlipperCommand(appConfig.topicFlipperFrontLeft,"FLF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sliderAvantDroit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                sendFlipperCommand(appConfig.topicFlipperFrontRight,"FRF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sliderArriere.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                sendFlipperCommand(appConfig.topicFlipperBack,"BF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        // Souscrire aux topics des caméras
        subscribeToCameraTopics()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_control_arm -> {
                // Lancer l'activité de contrôle du bras articulé
                val intent = Intent(this, ControlArmActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                // Lancer l'activité des paramètres
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    public fun updateRosBridgeConnexionStatus(status: Boolean){
        val textString = "IP Address: 192.168.1.12 || ${appConfig.rosbridgeUrl} Connected: ${rosbridgeClient.getIsConnected()}"
        ipTextView.text = textString
    }

    override fun onJoystickMoved(xPercent: Float, yPercent: Float) {
        // Appeler la méthode pour envoyer les valeurs du joystick via ROSBridge
        sendJoystickDirection(xPercent, yPercent)
    }

    private fun sendJoystickDirection(x: Float, y: Float) {
        // Créer le message JSON à envoyer via ROSBridge
        println("DEBUG $x , $y" )
        val message = JSONObject().apply {
            put("linear", JSONObject().apply {
                put("x", x)
                put("y", 0)
                put("z", 0)
            })
            put("angular", JSONObject().apply {
                put("x", 0)
                put("y", 0)
                put("z", y)
            })
        }

        // Envoyer le message au topic ROS souhaité
        rosbridgeClient.publish("/cmd_vel", message.toString())
    }

    private fun subscribeToCameraTopics() {
        for (i in cameraTopics.indices) {
            subscribeToCameraTopic(cameraTopics[i], imageViews[i])
        }
    }

    private fun subscribeToCameraTopic(topic: String, imageView: ImageView) {
        val subscribeMessage = JSONObject().apply {
            put("op", "subscribe")
            put("topic", topic)
        }

        rosbridgeClient.sendMessage(subscribeMessage.toString())

        rosbridgeClient.setOnMessageReceivedListener { message: String ->
            val jsonMessage = JSONObject(message)
            if (jsonMessage.has("data")) {
                val imageString = jsonMessage.getString("data")
                val imageData = Base64.decode(imageString, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
    private fun sendFlipperCommand(topic : String, flipper: String, angle: Int) {
        // Créer le message JSON à envoyer via ROSBridge
        val message = JSONObject().apply {
                put("flipper", flipper)
                put("angle", angle)
        }
        println("Debug Flipper message"+ message.toString())
        // Envoyer le message au topic ROSBridge
        rosbridgeClient.publish(topic, message.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        rosbridgeClient.close()
    }
}
