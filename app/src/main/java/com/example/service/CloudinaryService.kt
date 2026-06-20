package com.example.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.config.AppConfig
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object CloudinaryService {
    private var isInitialized = false

    fun init(context: Context) {
        val appConfig = AppConfig.getInstance()
        if (!appConfig.isLoadedSuccessfully) {
            Log.w("CloudinaryService", "Cannot initialize Cloudinary without config")
            return
        }
        if (isInitialized) return
        try {
            val config = mapOf(
                "cloud_name" to appConfig.cloudName,
                "callback_url" to null,
                "secure" to true
            )
            MediaManager.init(context, config)
            isInitialized = true
            Log.d("CloudinaryService", "Cloudinary successfully initialized")
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Error initializing Cloudinary", e)
        }
    }

    suspend fun uploadFile(context: Context, uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        val appConfig = AppConfig.getInstance()
        if (!isInitialized || !appConfig.isLoadedSuccessfully) {
            Log.e("CloudinaryService", "Cloudinary not ready")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        try {
            MediaManager.get().upload(uri)
                .unsigned(appConfig.uploadPreset)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d("CloudinaryService", "Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Progress
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String ?: resultData["url"] as? String
                        Log.d("CloudinaryService", "Upload success: $secureUrl")
                        if (continuation.isActive) {
                            continuation.resume(secureUrl)
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("CloudinaryService", "Upload failed: ${error.description}")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }).dispatch(context)
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Error starting upload", e)
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }
}
