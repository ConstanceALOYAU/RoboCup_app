package com.example.robocup

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), JoystickView.JoystickListener {

    private lateinit var appConfig: AppConfig
    private lateinit var ipTextView: TextView
    private lateinit var rosbridgeClient: RosbridgeClient
    private lateinit var imageViews: List<ImageView>
    private lateinit var joystickView: JoystickView
    private lateinit var cameraTopics: List<String>

    // Ajouter des TextView pour afficher les valeurs des sliders
    private lateinit var textSliderAvantGauche: TextView
    private lateinit var textSliderAvantDroit: TextView
    private lateinit var textSliderArriere: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurer la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Supprimer le titre par défaut dans la Toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Boutons de la Toolbar
        val buttonOne = findViewById<ImageView>(R.id.button_one)
        val buttonTwo = findViewById<ImageView>(R.id.button_two)
        val buttonTitle = findViewById<Button>(R.id.button_title)

        // Configurer les clics des boutons
        buttonOne.setOnClickListener {
            // Lancer "ControlArmActivity"
            val intent = Intent(this, ControlArmActivity::class.java)
            startActivity(intent)
        }

        buttonTwo.setOnClickListener {
            // Lancer "SettingsActivity"
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        buttonTitle.setOnClickListener {
            // Rester sur "MainActivity"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
        joystickView.setJoystickListener(this)

        // Associer les sliders et leurs TextView correspondants
        val sliderAvantGauche = findViewById<SeekBar>(R.id.sliderFrontLeft)
        val sliderAvantDroit = findViewById<SeekBar>(R.id.sliderFrontRight)
        val sliderArriere = findViewById<SeekBar>(R.id.sliderBack)

        textSliderAvantGauche = findViewById(R.id.text_slider_front_left)
        textSliderAvantDroit = findViewById(R.id.text_slider_front_right)
        textSliderArriere = findViewById(R.id.text_slider_back)

        // Initialiser le client ROSBridge
        rosbridgeClient = RosbridgeClient(appConfig.rosbridgeUrl, this)
        rosbridgeClient.connect()
        val textString = "RosBridge URL: ${appConfig.rosbridgeUrl} Connected: ${rosbridgeClient.getIsConnected()}"
        ipTextView.text = textString

        // Gérer les événements des sliders
        sliderAvantGauche.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSliderAvantGauche.text = progress.toString() // Mettre à jour le TextView
                sendFlipperCommand(appConfig.topicFlipperFrontLeft,"FLF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sliderAvantDroit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSliderAvantDroit.text = progress.toString() // Mettre à jour le TextView
                sendFlipperCommand(appConfig.topicFlipperFrontRight,"FRF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sliderArriere.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSliderArriere.text = progress.toString() // Mettre à jour le TextView
                sendFlipperCommand(appConfig.topicFlipperBack,"BF", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Souscrire aux topics des caméras
        Handler(Looper.getMainLooper()).postDelayed({
            subscribeToCameraTopics()
        }, 1000)
    }

    override fun onJoystickMoved(xPercent: Float, yPercent: Float) {
        // Appeler la méthode pour envoyer les valeurs du joystick via ROSBridge
        sendJoystickDirection(xPercent, yPercent)
    }

    private fun sendJoystickDirection(x: Float, y: Float) {
        // Créer le message JSON à envoyer via ROSBridge
        val message = JSONObject().apply {
            put("x", x)
            put("y", y)
            put("z", 0)
        }

        // Envoyer le message au topic ROS souhaité
        rosbridgeClient.publish(appConfig.topicJoy, message.toString())
    }

    private fun subscribeToCameraTopics() {
        for (i in cameraTopics.indices) {
            subscribeToCameraTopic(cameraTopics[i], imageViews[i])
        }
    }

    private fun saveAndDisplayImage(imageString: String, imageView: ImageView) {
        try {
            val imageData = Base64.decode(imageString, Base64.DEFAULT)
            val file = File(getExternalFilesDir(null), "image.jpg")

            // Sauvegarder l'image en tant que fichier JPEG
            FileOutputStream(file).use { it.write(imageData) }

            // Charger l'image sauvegardée et l'afficher
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            runOnUiThread {
                imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving or displaying image: ${e.message}")
        }
    }

    private fun subscribeToCameraTopic(topic: String, imageView: ImageView) {
        val subscribeMessage = JSONObject().apply {
            put("op", "subscribe")
            put("topic", topic)
        }

        rosbridgeClient.sendMessage(subscribeMessage.toString())

        rosbridgeClient.setOnMessageReceivedListener { message: String ->
            try {
                val jsonMessage = JSONObject(message)
                if (jsonMessage.has("msg")) {
                    val msg = jsonMessage.getJSONObject("msg")
                    if (msg.has("data")) {
                        val imageString = msg.getString("data")
                        saveAndDisplayImage(imageString, imageView)
                    } else {
                        Log.d("Rosbridge", "Le message reçu ne contient pas de clé 'data'")
                    }
                } else {
                    Log.d("Rosbridge", "Le message reçu ne contient pas de clé 'msg'")
                }
            } catch (e: Exception) {
                Log.e("Rosbridge", "Erreur lors du traitement du message: ${e.message}")
            }
        }
    }

    private fun sendFlipperCommand(topic: String, flipper: String, angle: Int) {
        // Créer le message JSON à envoyer via ROSBridge
        val message = JSONObject().apply {
            put("data", angle.toString())
        }

        // Envoyer le message au topic ROSBridge
        rosbridgeClient.publish(topic, message.toString())
    }

    fun updateRosBridgeConnexionStatus(status: Boolean) {
        val textString = "RosBridge URL: ${appConfig.rosbridgeUrl} Connected: $status"
        ipTextView.text = textString
    }

    override fun onDestroy() {
        super.onDestroy()
        rosbridgeClient.close()
    }
}
