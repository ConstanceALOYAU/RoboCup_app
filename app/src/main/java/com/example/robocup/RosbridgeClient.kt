package com.example.robocup

import okhttp3.*
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.json.JSONObject

class RosbridgeClient(private val url: String, private val activity: MainActivity) {

    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket
    private var isConnected = false
    private val reconnectDelay = 5000L // 5 secondes
    private var messageReceivedListener: ((String) -> Unit)? = null
    private val listeners = mutableMapOf<String, (String) -> Unit>()


    fun addListener(topic: String, listener: (String) -> Unit) {
        listeners[topic] = listener
    }

    private fun handleMessage(message: String) {
        val jsonMessage = JSONObject(message)
        if (jsonMessage.has("topic")) {
            val topic = jsonMessage.getString("topic")
            listeners[topic]?.invoke(message)
        }
    }


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
                activity.updateRosBridgeConnexionStatus(isConnected)
                Log.d("RosbridgeClient", "Connected to ROSBRIDGE")
                // Aucun abonnement automatique ici, tout est géré par l'activité
            }

            /*override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d("RosbridgeClient", "Message received: $text")
                // Appel du listener pour traiter le message reçu

            }*/

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("RosbridgeClient", "Message received: $text")
                handleMessage(text)
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
            Log.d("RosbridgeClient", "Sent: $message")
        } else {
            Log.w("RosbridgeClient", "Cannot send message, not connected")
        }
    }

    fun close() {
        ws.close(1000, "Client closed connection")
        client.dispatcher.executorService.shutdown()
        isConnected = false
    }

    fun publish(topic: String, message: String) {
        val jsonMessage = """
            {
                "op": "publish",
                "topic": "$topic",
                "msg": $message
            }
        """.trimIndent()
        println("Debug publish message"+ jsonMessage.toString())
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

    fun getIsConnected(): Boolean {
        return isConnected
    }

    fun setOnMessageReceivedListener(listener: (String) -> Unit) {
        messageReceivedListener = listener
    }
}
