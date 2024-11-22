package com.truvideo.media.app.ui.activities.main_activity

import android.app.AlertDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.media.app.ui.theme.TruvideoSdkMediaTheme
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import kotlinx.coroutines.launch
import truvideo.sdk.common.exceptions.TruvideoSdkException

@Composable
fun ListItem(request: TruvideoSdkMediaFileUploadRequest) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var infoVisible by remember { mutableStateOf(true) }

    fun showError(error: String) {
        AlertDialog.Builder(context)
            .setMessage(error)
            .show()
    }

    fun upload() {
        scope.launch {
            try {
                request.upload(
                    object : TruvideoSdkMediaFileUploadCallback {
                        override fun onComplete(id: String, response: TruvideoSdkMediaFileUploadRequest) {
                        }

                        override fun onProgressChanged(id: String, progress: Float) {
                        }

                        override fun onError(id: String, ex: TruvideoSdkException) {
                        }
                    }
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                showError(exception.localizedMessage ?: "Uknown error")
            }
        }
    }

    fun pause() {
        scope.launch {
            try {
                request.pause()
            } catch (exception: Exception) {
                exception.printStackTrace()
                showError(exception.localizedMessage ?: "Uknown error")
            }
        }
    }

    fun resume() {
        scope.launch {
            try {
                request.resume()
            } catch (exception: Exception) {
                exception.printStackTrace()
                showError(exception.localizedMessage ?: "Uknown error")
            }
        }
    }

    fun cancel() {
        scope.launch {
            try {
                request.cancel()
            } catch (exception: Exception) {
                exception.printStackTrace()
                showError(exception.localizedMessage ?: "Uknown error")
            }
        }
    }

    fun delete() {
        scope.launch {
            try {
                request.delete()
            } catch (exception: Exception) {
                exception.printStackTrace()
                showError(exception.localizedMessage ?: "Uknown error")
            }
        }
    }


    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable { infoVisible = !infoVisible }
                .animateContentSize { _, _ -> }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(request.type.name)
                }

                Spacer(Modifier.weight(1f))

                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(request.status.name)
                }

                Icon(
                    imageVector = if (infoVisible) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (infoVisible) {
                HorizontalDivider()

                Box(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text("Created At", style = MaterialTheme.typography.bodySmall)
                        Text("${request.createdAt}")

                        Spacer(Modifier.height(8.dp))

                        Text("Updated At", style = MaterialTheme.typography.bodySmall)
                        Text("${request.updatedAt}")

                        Spacer(Modifier.height(8.dp))

                        Text("File path", style = MaterialTheme.typography.bodySmall)
                        Text(request.filePath)

                        if (request.durationMilliseconds != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Duration", style = MaterialTheme.typography.bodySmall)
                            Text("${request.durationMilliseconds}ms")
                        }

                        if (request.uploadProgress != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Progress", style = MaterialTheme.typography.bodySmall)
                            Text("${(request.uploadProgress!! * 100)}%")
                        }

                        if (request.remoteId != null && (request.remoteId ?: "").trim().isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Remote ID", style = MaterialTheme.typography.bodySmall)
                            Text("${request.remoteId}")
                        }

                        if (request.remoteUrl != null && (request.remoteUrl?:"").trim().isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Remote URL", style = MaterialTheme.typography.bodySmall)
                            Text("${request.remoteUrl}")
                        }

                        if (request.transcriptionUrl != null&& (request.transcriptionUrl?:"").trim().isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Transcription URL", style = MaterialTheme.typography.bodySmall)
                            Text("${request.transcriptionUrl}")
                        }

                        if (request.tags.map.entries.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Tags", style = MaterialTheme.typography.bodySmall)
                            Text(request.tags.toJson())
                        }

                        if (request.metadata.map.entries.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Metadata", style = MaterialTheme.typography.bodySmall)
                            Text(request.metadata.toJson())
                        }

                        if (request.includeInReport != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Include In Report", style = MaterialTheme.typography.bodySmall)
                            Text("${request.includeInReport}")
                        }

                        Box(Modifier.height(16.dp))

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(onClick = { upload() }) {
                                Text("Upload")
                            }

                            FilledTonalButton(onClick = { pause() }) {
                                Text("Pause")
                            }

                            FilledTonalButton(onClick = { resume() }) {
                                Text("Resume")
                            }

                            FilledTonalButton(onClick = { cancel() }) {
                                Text("Cancel")
                            }

                            FilledTonalButton(onClick = { delete() }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    val request = remember {
        TruvideoSdkMediaFileUploadRequest(
            id = "id",
            filePath = "file-path",
            tags = TruvideoSdkMediaTags.builder()
                .set("key", "value")
                .set("color", "red")
                .set("order-id", "123")
                .build(),
            includeInReport = true,
            metadata = TruvideoSdkMediaMetadata.builder()
                .set("key", "value")
                .set("list", listOf("value1", "value2"))
                .set(
                    "nested", TruvideoSdkMediaMetadata.builder()
                        .set("key", "value")
                        .set("list", listOf("value1", "value2"))
                        .build()
                )
                .build(),
        )
    }

    TruvideoSdkMediaTheme {
        ListItem(request)
    }
}