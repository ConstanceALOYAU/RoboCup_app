package com.example.robocup

import android.content.Context
import android.content.SharedPreferences

class AppConfig(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_config_prefs"

        // Default values for ROSBridge and topics
        private const val DEFAULT_ROSBRIDGE_URL = "ws://192.168.1.50:9090"
        private const val DEFAULT_TOPIC_JOY = "RCR/joy/cmd_vel"
        private const val DEFAULT_TOPIC_CAM1 = "RCR/cam1/image_raw"
        private const val DEFAULT_TOPIC_CAM2 = "RCR/cam2/image_raw"
        private const val DEFAULT_TOPIC_CAM3 = "RCR/cam3/image_raw"
        private const val DEFAULT_TOPIC_CAM4 = "RCR/cam4/image_raw"
        private const val DEFAULT_TOPIC_FLIPPER_FRONT_LEFT = "RCR/FLF/flipper_control"
        private const val DEFAULT_TOPIC_FLIPPER_FRONT_RIGHT = "RCR/FRF/flipper_control"
        private const val DEFAULT_TOPIC_FLIPPER_BACK = "RCR/BF/flipper_control"

        // Keys for SharedPreferences
        private const val KEY_ROSBRIDGE_URL = "rosbridge_url"
        private const val KEY_TOPIC_JOY = "topic_joy"
        private const val KEY_TOPIC_CAM1 = "topic_cam1"
        private const val KEY_TOPIC_CAM2 = "topic_cam2"
        private const val KEY_TOPIC_CAM3 = "topic_cam3"
        private const val KEY_TOPIC_CAM4 = "topic_cam4"
        private const val KEY_TOPIC_FLIPPER_FRONT_LEFT = "topic_flipper_front_left"
        private const val KEY_TOPIC_FLIPPER_FRONT_RIGHT = "topic_flipper_front_right"
        private const val KEY_TOPIC_FLIPPER_BACK = "topic_flipper_back"
    }

    var rosbridgeUrl: String
        get() = preferences.getString(KEY_ROSBRIDGE_URL, DEFAULT_ROSBRIDGE_URL) ?: DEFAULT_ROSBRIDGE_URL
        set(value) {
            preferences.edit().putString(KEY_ROSBRIDGE_URL, value).apply()
        }

    var topicJoy: String
        get() = preferences.getString(KEY_TOPIC_JOY, DEFAULT_TOPIC_JOY) ?: DEFAULT_TOPIC_JOY
        set(value) {
            preferences.edit().putString(KEY_TOPIC_JOY, value).apply()
        }

    var topicCam1: String
        get() = preferences.getString(KEY_TOPIC_CAM1, DEFAULT_TOPIC_CAM1) ?: DEFAULT_TOPIC_CAM1
        set(value) {
            preferences.edit().putString(KEY_TOPIC_CAM1, value).apply()
        }

    var topicCam2: String
        get() = preferences.getString(KEY_TOPIC_CAM2, DEFAULT_TOPIC_CAM2) ?: DEFAULT_TOPIC_CAM2
        set(value) {
            preferences.edit().putString(KEY_TOPIC_CAM2, value).apply()
        }

    var topicCam3: String
        get() = preferences.getString(KEY_TOPIC_CAM3, DEFAULT_TOPIC_CAM3) ?: DEFAULT_TOPIC_CAM3
        set(value) {
            preferences.edit().putString(KEY_TOPIC_CAM3, value).apply()
        }

    var topicCam4: String
        get() = preferences.getString(KEY_TOPIC_CAM4, DEFAULT_TOPIC_CAM4) ?: DEFAULT_TOPIC_CAM4
        set(value) {
            preferences.edit().putString(KEY_TOPIC_CAM4, value).apply()
        }

    var topicFlipperFrontLeft: String
        get() = preferences.getString(KEY_TOPIC_FLIPPER_FRONT_LEFT, DEFAULT_TOPIC_FLIPPER_FRONT_LEFT) ?: DEFAULT_TOPIC_FLIPPER_FRONT_LEFT
        set(value) {
            preferences.edit().putString(KEY_TOPIC_FLIPPER_FRONT_LEFT, value).apply()
        }

    var topicFlipperFrontRight: String
        get() = preferences.getString(KEY_TOPIC_FLIPPER_FRONT_RIGHT, DEFAULT_TOPIC_FLIPPER_FRONT_RIGHT) ?: DEFAULT_TOPIC_FLIPPER_FRONT_RIGHT
        set(value) {
            preferences.edit().putString(KEY_TOPIC_FLIPPER_FRONT_RIGHT, value).apply()
        }

    var topicFlipperBack: String
        get() = preferences.getString(KEY_TOPIC_FLIPPER_BACK, DEFAULT_TOPIC_FLIPPER_BACK) ?: DEFAULT_TOPIC_FLIPPER_BACK
        set(value) {
            preferences.edit().putString(KEY_TOPIC_FLIPPER_BACK, value).apply()
        }

    fun clearConfig() {
        preferences.edit().clear().apply()
    }
}
