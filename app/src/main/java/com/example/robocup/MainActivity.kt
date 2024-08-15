package com.example.robocup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.Formatter
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import android.net.wifi.WifiManager

class MainActivity : AppCompatActivity(), JoystickView.JoystickListener {

    private lateinit var rosbridgeURL: String
    private lateinit var ipTextView: TextView
    private lateinit var rosbridgeClient: RosbridgeClient
    private lateinit var imageViews: List<ImageView>
    private lateinit var joystickView: JoystickView
    private val cameraTopics = listOf("RCR/cam1/image_raw", "RCR/cam2/image_raw", "RCR/cam3/image_raw", "RCR/cam4/image_raw")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Supprimer le titre par défaut dans la Toolbar
        supportActionBar?.hide()

        rosbridgeURL = "ws://192.168.1.7:9090"

        // Associer les vues
        ipTextView = findViewById(R.id.ip)

        imageViews = listOf(
            findViewById(R.id.ViewFL),
            findViewById(R.id.ViewFR),
            findViewById(R.id.ViewBL),
            findViewById(R.id.ViewBR)
        )

        joystickView = findViewById(R.id.joystickView)
        joystickView.setJoystickListener(this) // Set the listener


        // Initialiser le client ROSBridge
        rosbridgeClient = RosbridgeClient(rosbridgeURL, this)
        rosbridgeClient.connect()
        var textString = "IP Address: 192.168.1.12 || $rosbridgeURL Connected: ${rosbridgeClient.getIsConnected()}"
        ipTextView.text = textString

        // Souscrire aux topics des caméras
        subscribeToCameraTopics()
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

    override fun onDestroy() {
        super.onDestroy()
        rosbridgeClient.close()
    }
}
