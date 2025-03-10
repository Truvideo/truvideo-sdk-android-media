package com.truvideo.media.app.ui.activities.main_activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.truvideo.media.app.ui.activities.fetch_media.FetchMediaActivity
import com.truvideo.media.app.ui.theme.TruvideoSdkMediaTheme
import com.truvideo.sdk.media.TruvideoSdkMedia
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TruvideoSdkMediaTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content() {
        val view = LocalView.current
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()
        var requests: List<TruvideoSdkMediaFileUploadRequest> by remember { mutableStateOf(listOf()) }
        var deleteOnComplete by remember { mutableStateOf(false) }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) {

        }

        fun readAssetFile(fileName: String, outputFile: File) {
            val assetManager = context.assets
            assetManager.open(fileName).use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        fun pickPdf() {
            val outputFile = File(context.cacheDir, "pdf.pdf")
            readAssetFile("pdf.pdf", outputFile)

            scope.launch {
                val builder = TruvideoSdkMedia.FileUploadRequestBuilder(outputFile.path)
                builder.deleteOnComplete(deleteOnComplete)
                builder.addTag("type", "document")
                builder.build()
            }
        }

        fun pickAudio() {
            val outputFile = File(context.cacheDir, "audio.aac")
            readAssetFile("audio.aac", outputFile)

            scope.launch {
                val builder = TruvideoSdkMedia.FileUploadRequestBuilder(outputFile.path)
                builder.deleteOnComplete(deleteOnComplete)
                builder.addTag("type", "audio")
                builder.build()
            }
        }

        fun pickVideo() {
            val outputFile = File(context.cacheDir, "video.mp4")
            readAssetFile("video.mp4", outputFile)

            scope.launch {
                val builder = TruvideoSdkMedia.FileUploadRequestBuilder(outputFile.path)
                builder.deleteOnComplete(deleteOnComplete)
                builder.addTag("type", "video")
                builder.build()
            }
        }

        fun pickImage() {
            val outputFile = File(context.cacheDir, "image.jpg")
            readAssetFile("image.jpg", outputFile)

            scope.launch {
                val builder = TruvideoSdkMedia.FileUploadRequestBuilder(outputFile.path)
                builder.deleteOnComplete(deleteOnComplete)
                builder.addTag("type", "image")
                builder.build()
            }
        }

        LaunchedEffect(Unit) {
            if (!view.isInEditMode) {
                TruvideoSdkMedia.streamAllFileUploadRequests().observe(lifecycleOwner) {
                    requests = it
                }
            }
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text("Media Module") },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(this@MainActivity, FetchMediaActivity::class.java)
                            startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Outlined.List, contentDescription = "")
                        }
                    }
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { pickPdf() }
                    ) {
                        Text("Pick PDF")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { pickAudio() }
                    ) {
                        Text("Pick Audio")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { pickVideo() }
                    ) {
                        Text("Pick Video")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { pickImage() }
                    ) {
                        Text("Pick Image")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                        }
                    ) {
                        Text("Pick file")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { deleteOnComplete = !deleteOnComplete }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (deleteOnComplete) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                        contentDescription = ""
                    )
                    Box(Modifier.width(8.dp))
                    Text("Delete on complete", modifier = Modifier.weight(1f))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(requests, key = { it.id }) {
                        Box(Modifier.animateItem()) {
                            ListItem(request = it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun Test() {
        TruvideoSdkMediaTheme {
            Content()
        }
    }
}
