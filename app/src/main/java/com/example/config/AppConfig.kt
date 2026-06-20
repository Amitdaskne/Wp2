package com.example.config

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.InputStream

class AppConfig private constructor() {
    var apiKey: String = ""
        private set
    var projectId: String = ""
        private set
    var databaseURL: String = ""
        private set
    var storageBucket: String = ""
        private set
    var appId: String = ""
        private set
    var cloudName: String = ""
        private set
    var uploadPreset: String = ""
        private set
    var isLoadedSuccessfully: Boolean = false
        private set

    companion object {
        private var instance: AppConfig? = null

        fun getInstance(): AppConfig {
            if (instance == null) {
                instance = AppConfig()
            }
            return instance!!
        }
    }

    fun load(context: Context): Boolean {
        return try {
            val inputStream: InputStream = context.assets.open("config.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonRoot = JSONObject(jsonString)

            val firebase = jsonRoot.getJSONObject("firebase")
            apiKey = firebase.getString("apiKey")
            projectId = firebase.getString("projectId")
            databaseURL = firebase.getString("databaseURL")
            storageBucket = firebase.getString("storageBucket")
            appId = firebase.getString("appId")

            val cloudinary = jsonRoot.getJSONObject("cloudinary")
            cloudName = cloudinary.getString("cloudName")
            uploadPreset = cloudinary.getString("uploadPreset")

            isLoadedSuccessfully = apiKey.isNotEmpty() && cloudName.isNotEmpty()
            isLoadedSuccessfully
        } catch (e: Exception) {
            Log.e("AppConfig", "Error loading config.json from assets", e)
            isLoadedSuccessfully = false
            false
        }
    }
}
