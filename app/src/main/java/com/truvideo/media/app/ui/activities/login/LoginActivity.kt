package com.truvideo.media.app.ui.activities.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.truvideo.media.app.ui.activities.main_activity.MainActivity
import com.truvideo.media.app.ui.theme.TruvideosdkmediaTheme
import com.truvideo.media.app.util.TruvideoSdkSignature
import com.truvideo.sdk.core.TruvideoSdk
import com.truvideo.sdk.core.interfaces.TruvideoSdkSignatureProvider
import kotlinx.coroutines.launch
import truvideo.sdk.common.model.TruvideoSdkEnvironment
import truvideo.sdk.common.sdk_common

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sdk_common.configuration.environment = TruvideoSdkEnvironment.DEV

        setContent {
            TruvideosdkmediaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }

    @Composable
    fun Content() {
        val scope = rememberCoroutineScope()
        val apiKey = when (sdk_common.configuration.environment) {
            // Dev
            TruvideoSdkEnvironment.DEV -> "KOIYjxw9A6" // Ours

            // Beta
            TruvideoSdkEnvironment.BETA -> "VS2SG9WK"

            // RC
//            TruvideoSdkEnvironment.RC -> "VS2SG9WK" // Ours
            TruvideoSdkEnvironment.RC -> "0EeGlpbESu" // Reynolds
            // PROD
//            TruvideoSdkEnvironment.PROD -> "EPhPPsbv7e" // ours
            TruvideoSdkEnvironment.PROD -> "5esxyUUl0t" // Reynolds
            else -> ""
        }

        val secret = when (sdk_common.configuration.environment) {
            // DEV
            TruvideoSdkEnvironment.DEV -> "s1iay0utUl"

            // BETA
            TruvideoSdkEnvironment.BETA -> "ST2K33GR"

            // RC
//            TruvideoSdkEnvironment.RC -> "ST2K33GR" // Ours
            TruvideoSdkEnvironment.RC -> "QDjx0T9RyD" // Reynolds

            // PROD
//            TruvideoSdkEnvironment.PROD -> "9lHCnkfeLl" // Ours
            TruvideoSdkEnvironment.PROD -> "PCRE0bdAce" // Reynolds
            else -> ""
        }


        var loading by remember { mutableStateOf(true) }

        fun auth() {
            scope.launch {

                try {
                    loading = true

                    TruvideoSdk.handleAuthentication(
                        apiKey = apiKey,
                        signatureProvider = object : TruvideoSdkSignatureProvider {
                            override fun generateSignature(payload: String) = TruvideoSdkSignature.generate(payload, secret)
                        }
                    )

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (exception: Exception) {
                    exception.printStackTrace()

                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Error authenticating")
                        .setMessage(exception.localizedMessage ?: "Unknown error")
                        .setPositiveButton("Retry") { _, _ -> auth() }
                        .show()
                } finally {
                    loading = false
                }
            }
        }

        LaunchedEffect(Unit) { auth() }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}