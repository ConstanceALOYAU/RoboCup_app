package com.example.robocup

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var rosbridgeURL: String
    private lateinit var titleTextView: TextView
    private lateinit var ipTextView: TextView
    private lateinit var ipRBview: TextView
    private lateinit var rosbridgeClient: RosbridgeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rosbridgeURL = "ws://192.168.1.7:9090"
        // Associer les vues
        titleTextView = findViewById(R.id.title)
        ipTextView = findViewById(R.id.ip)

        // Obtenir et afficher l'adresse IP
        try {
            val ipAddress = getIPAddress(this)
            if (ipAddress != null) {
                ipTextView.text = "IP Address: $ipAddress"
                // Initialiser et connecter le client ROSBRIDGE avec l'adresse IP de la tablette
            }
        } catch (e: Exception) {
            ipTextView.text = "Error getting IP Address"
            Log.e("MainActivity", "Exception in getIPAddress: ${e.message}")
        }

        rosbridgeClient = RosbridgeClient(rosbridgeURL, this)

        ipRBview = findViewById(R.id.ip_rb)
        ipRBview.text = rosbridgeURL
        rosbridgeClient.connect()
    }

    // Mettre à jour l'interface utilisateur avec le message reçu
    fun updateUIWithMessage(message: String) {
        runOnUiThread {
            titleTextView.text = message
        }
    }

    // Fonction pour obtenir l'adresse IP de la tablette
    private fun getIPAddress(context: Context): String? {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            return Formatter.formatIpAddress(ipAddress)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error getting IP address: ${e.message}")
            return null
        }
    }
}

