// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
}

import java.util.Base64
import java.io.File

// Robust self-healing decode for debug keystore
val keystoreFile = File(rootDir, "debug.keystore")
val base64File = File(rootDir, "debug.keystore.base64")
if (!keystoreFile.exists() && base64File.exists()) {
    try {
        val base64Content = base64File.readText().trim()
        val decodedBytes = Base64.getDecoder().decode(base64Content)
        keystoreFile.writeBytes(decodedBytes)
        println("Successfully decoded debug.keystore from base64!")
    } catch (e: Exception) {
        System.err.println("Failed to decode debug.keystore: ${e.message}")
    }
}

