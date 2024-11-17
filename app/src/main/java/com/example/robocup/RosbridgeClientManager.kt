package com.example.robocup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity


object RosbridgeClientManager {
    private lateinit var appConfig: AppConfig
    private var rosbridgeClient: RosbridgeClient? = null

    fun getInstance(context: Context): RosbridgeClient {
        if (rosbridgeClient == null) {
            rosbridgeClient = RosbridgeClient(appConfig.rosbridgeUrl, context as AppCompatActivity)
            rosbridgeClient!!.connect()
        }
        return rosbridgeClient!!
    }

    fun closeConnection() {
        rosbridgeClient?.close()
        rosbridgeClient = null
    }
}
