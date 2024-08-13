package com.example.robocup

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {


    private lateinit var rosbridgeURL: String
    private lateinit var titleTextView: TextView
    private lateinit var ipTextView: TextView
    private lateinit var ipRBview: TextView
    private lateinit var rosbridgeClient: RosbridgeClient
    private val mediaPlayers = mutableListOf<MediaPlayer>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Supprimer le titre par défaut dans la Toolbar
        supportActionBar?.hide()

        rosbridgeURL = "ws://192.168.1.7:9090"

        // Associer les vues
        ipTextView = findViewById(R.id.ip)
        ipRBview = findViewById(R.id.ip_rb)

        // Obtenir et afficher l'adresse IP
        try {
            val ipAddress = getIPAddress(this)
            if (ipAddress != null) {
                ipTextView.text = "IP Address: $ipAddress"
            }
        } catch (e: Exception) {
            ipTextView.text = "Error getting IP Address"
            Log.e("MainActivity", "Exception in getIPAddress: ${e.message}")
        }

        // Initialiser le client ROSBridge
        rosbridgeClient = RosbridgeClient(rosbridgeURL, this)

        rosbridgeClient.connect()
        ipRBview.text = rosbridgeURL + " Connected: " + rosbridgeClient.getIsConnected()

        // Configurer les SurfaceView et MediaPlayer
        setupSurfaceView(R.id.ViewFL, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        setupSurfaceView(R.id.ViewFR, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        setupSurfaceView(R.id.ViewBL, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4")
        setupSurfaceView(R.id.ViewBR, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4")
    }

    private fun setupSurfaceView(viewId: Int, videoUri: String) {
        val surfaceView = findViewById<SurfaceView>(viewId)
        val surfaceHolder = surfaceView.holder
        val mediaPlayer = MediaPlayer() // Créer un MediaPlayer pour cette SurfaceView
        mediaPlayers.add(mediaPlayer) // Ajouter le MediaPlayer à la liste

        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaPlayer.setDisplay(holder)
                mediaPlayer.setDataSource(this@MainActivity, Uri.parse(videoUri))
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Gérer les changements de surface si nécessaire
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mediaPlayer.release()
            }
        })
    }

    // Mettre à jour l'interface utilisateur avec le message reçu
    fun updateUIWithMessage(message: String) {
        runOnUiThread {
            titleTextView.text = message
        }
    }

    // Fonction pour obtenir l'adresse IP de la tablette
    private fun getIPAddress(context: Context): String? {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            Formatter.formatIpAddress(ipAddress)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error getting IP address: ${e.message}")
            null
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        TODO("Not yet implemented")
    }
}
