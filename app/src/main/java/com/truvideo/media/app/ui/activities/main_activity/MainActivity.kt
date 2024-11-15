package com.truvideo.media.app.ui.activities.main_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.media.app.ui.activities.fetch_media.FetchMediaActivity
import com.truvideo.media.app.ui.theme.TruvideosdkmediaTheme
import com.truvideo.sdk.media.TruvideoSdkMedia
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TruvideosdkmediaTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content() {
        val view = LocalView.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()
        var requests: List<TruvideoSdkMediaFileUploadRequest> by remember { mutableStateOf(listOf()) }
        var deleteOnComplete by remember { mutableStateOf(false) }

//        val launcher = rememberLauncherForActivityResult(TruvideoSdkFilePickerContract()) { path: String? ->
//            Log.d("TruvideoSdkMedia", "data: $path")
//            if (path == null) return@rememberLauncherForActivityResult
//
//            val file = File(path)
//            if (!file.exists()) return@rememberLauncherForActivityResult
//
//            val builder = TruvideoSdkMedia.FileUploadRequestBuilder(path)
//
//            // Tags
//            builder.addTag("key", "value")
//            builder.addTag("color", "red")
//            builder.addTag("order-id", "123")
//
//            // Metadata
//            builder.addMetadata("key", "value")
//            builder.addMetadata("list", listOf("value1", "value2"))
//            builder.addMetadata(
//                "nested", TruvideoSdkMediaMetadata.builder()
//                    .set("key", "value")
//                    .set("list", listOf("value1", "value2"))
//                    .build()
//            )
//
//            builder.setIncludeInReport(false)
//
//            builder.deleteOnComplete(deleteOnComplete)
//
//            scope.launch {
//                builder.build()
//            }
//        }

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

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
//                        launcher.launch(TruvideoSdkFilePickerType.VideoAndPicture)
                    }
                ) {
                    Text("Pick file")
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
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(requests, key = { it.id }) {
                        ListItem(request = it)
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun Test() {
        TruvideosdkmediaTheme {
            Content()
        }
    }
}
