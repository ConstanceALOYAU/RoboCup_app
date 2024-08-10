package com.example.robocup
import okhttp3.*
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class RosbridgeClient(private val url: String, private val activity: MainActivity) {

    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket
    private var isConnected = false
    private val reconnectDelay = 5000L // 5 secondes

    fun connect() {
        client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        ws = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                isConnected = true
                Log.d("RosbridgeClient", "Connected to ROSBRIDGE")

                // Exemple d'abonnement à un topic
                subscribe("/chatter")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d("RosbridgeClient", "Message received: $text")
                // Mettre à jour l'interface utilisateur
                activity.updateUIWithMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                isConnected = false
                Log.e("RosbridgeClient", "Connection failed: ${t.message}")
                // Tentative de reconnexion
                retryConnection()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnected = false
                Log.d("RosbridgeClient", "Connection closed: $reason")
                // Tentative de reconnexion
                retryConnection()
            }
        })
    }

    private fun retryConnection() {
        if (!isConnected) {
            Log.d("RosbridgeClient", "Attempting to reconnect in $reconnectDelay ms")
            activity.lifecycleScope.launch(Dispatchers.IO) {
                delay(reconnectDelay)
                connect()
            }
        }
    }

    fun sendMessage(message: String) {
        if (isConnected) {
            ws.send(message)
            // Optionnellement, mettre à jour l'UI avec le message envoyé
            activity.updateUIWithMessage("Sent: $message")
        } else {
            Log.w("RosbridgeClient", "Cannot send message, not connected")
        }
    }

    fun close() {
        ws.close(1000, "Client closed connection")
        client.dispatcher.executorService.shutdown()
        isConnected = false
    }

    // Fonctions pour interagir avec ROSBridge
    fun publish(topic: String, message: String) {
        val jsonMessage = """
            {
                "op": "publish",
                "topic": "$topic",
                "msg": $message
            }
        """.trimIndent()

        sendMessage(jsonMessage)
    }

    fun subscribe(topic: String) {
        val jsonMessage = """
            {
                "op": "subscribe",
                "topic": "$topic"
            }
        """.trimIndent()

        sendMessage(jsonMessage)
    }

    fun unsubscribe(topic: String) {
        val jsonMessage = """
            {
                "op": "unsubscribe",
                "topic": "$topic"
            }
        """.trimIndent()

        sendMessage(jsonMessage)
    }
}

