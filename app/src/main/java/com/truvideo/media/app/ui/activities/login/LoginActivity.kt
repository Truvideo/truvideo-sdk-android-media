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

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        var loading by remember { mutableStateOf(true) }

        fun auth() {
            scope.launch {

                try {
                    loading = true

                    TruvideoSdk.handleAuthentication(
                        apiKey = "",
                        signatureProvider = object : TruvideoSdkSignatureProvider {
                            override fun generateSignature(payload: String) = TruvideoSdkSignature.generate(payload, "")
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