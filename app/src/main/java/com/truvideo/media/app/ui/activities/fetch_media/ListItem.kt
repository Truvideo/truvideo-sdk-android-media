package com.truvideo.media.app.ui.activities.fetch_media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.media.app.ui.theme.TruvideoSdkMediaTheme
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponseType
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import java.util.Date

@Composable
fun MediaResponseListItem(
    model: TruvideoSdkMediaResponse
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Id", style = MaterialTheme.typography.bodySmall)
            Text(model.id)

            Box(modifier = Modifier.height(8.dp))
            Text("Title", style = MaterialTheme.typography.bodySmall)
            Text(model.title)

            Box(modifier = Modifier.height(8.dp))
            Text("Type", style = MaterialTheme.typography.bodySmall)
            Text(model.type.name)

            if (model.duration != null) {
                Box(modifier = Modifier.height(8.dp))
                Text("Duration", style = MaterialTheme.typography.bodySmall)
                Text("${(model.duration ?: 0L)}secs")
            }

            Box(modifier = Modifier.height(8.dp))
            Text("Created Date", style = MaterialTheme.typography.bodySmall)
            Text(model.createdDate.toString())

            Box(modifier = Modifier.height(8.dp))
            Text("Url", style = MaterialTheme.typography.bodySmall)
            Text(model.url)

            if (model.transcriptionUrl.trim().isNotEmpty()) {
                Box(modifier = Modifier.height(8.dp))
                Text("Transcription Url", style = MaterialTheme.typography.bodySmall)
                Text(model.transcriptionUrl)
            }

            if(model.transcriptionLength!=null){
                Box(modifier = Modifier.height(8.dp))
                Text("Transcription Length", style = MaterialTheme.typography.bodySmall)
                Text(model.transcriptionLength?.toString() ?: "")
            }

            Box(modifier = Modifier.height(8.dp))
            Text("Tags", style = MaterialTheme.typography.bodySmall)
            Text(model.tags.toJson())

            Box(modifier = Modifier.height(8.dp))
            Text("Metadata", style = MaterialTheme.typography.bodySmall)
            Text(model.metadata.toJson())

            Box(modifier = Modifier.height(8.dp))
            Text("Include In Report", style = MaterialTheme.typography.bodySmall)
            Text(model.includeInReport.toString())
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruvideoSdkMediaTheme {
        MediaResponseListItem(
            model = TruvideoSdkMediaResponse(
                title = "title",
                id = "id",
                transcriptionLength = 0f,
                transcriptionUrl = "transcription url",
                tags = TruvideoSdkMediaTags.builder()
                    .set("key", "value")
                    .build(),
                type = TruvideoSdkMediaResponseType.VIDEO,
                url = "url",
                createdDate = Date(),
                includeInReport = false,
                metadata = TruvideoSdkMediaMetadata.builder()
                    .set("key", "value")
                    .set("list", listOf("value1", "value2"))
                    .set(
                        "nested", TruvideoSdkMediaMetadata.builder()
                            .set("key", "value")
                            .set("list", listOf("value1", "value2")).build()
                    )
                    .build()
            )
        )
    }
}