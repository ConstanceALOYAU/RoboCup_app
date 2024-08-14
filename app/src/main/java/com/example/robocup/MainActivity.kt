package com.example.robocup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var rosbridgeURL: String
    private lateinit var ipTextView: TextView
    private lateinit var rosbridgeClient: RosbridgeClient
    private lateinit var imageViews: List<ImageView>
    private val cameraTopics = listOf("/camera1/image_raw", "/camera2/image_raw", "/camera3/image_raw", "/camera4/image_raw")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Supprimer le titre par défaut dans la Toolbar
        supportActionBar?.hide()

        rosbridgeURL = "ws://192.168.1.7:9090"

        // Associer les vues
        ipTextView = findViewById(R.id.ip)
        var textString = ""
        imageViews = listOf(
            findViewById(R.id.ViewFL),
            findViewById(R.id.ViewFR),
            findViewById(R.id.ViewBL),
            findViewById(R.id.ViewBR)
        )

        // Obtenir et afficher l'adresse IP
        try {
            val ipAddress = getIPAddress(this)
            if (ipAddress != null) {
                textString = "IP Address: $ipAddress"
            }
        } catch (e: Exception) {
            textString = "Error getting IP Address"
            Log.e("MainActivity", "Exception in getIPAddress: ${e.message}")
        }

        // Initialiser le client ROSBridge
        rosbridgeClient = RosbridgeClient(rosbridgeURL, this)
        rosbridgeClient.connect()
        textString += " $rosbridgeURL Connected: ${rosbridgeClient.getIsConnected()}"
        ipTextView.text = textString

        // Souscrire aux topics des caméras
        subscribeToCameraTopics()
        val joystick = findViewById<JoystickView>(R.id.joystickView)

        joystick.setOnTouchListener { _, _ ->
            val direction = joystick.getDirection()
            val x = direction.first
            val y = direction.second

            // Utilisez x et y comme vecteur directeur pour contrôler votre robot ou autre
            println("Vecteur directeur: ($x, $y)")

            true
        }

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

    // Fonction pour obtenir l'adresse IP de la tablette
    private fun getIPAddress(context: Context): String? {
        return try {
            val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            Formatter.formatIpAddress(ipAddress)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error getting IP address: ${e.message}")
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rosbridgeClient.close()
    }
}
