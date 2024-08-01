package com.truvideo.media.app.ui.activities.fetch_media

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
import com.truvideo.media.app.ui.theme.TruvideosdkmediaTheme
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Id", style = MaterialTheme.typography.titleSmall)
            Text(model.id, style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Title", style = MaterialTheme.typography.titleSmall)
            Text(model.title, style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Type", style = MaterialTheme.typography.titleSmall)
            Text(model.type.name, style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Created Date", style = MaterialTheme.typography.titleSmall)
            Text(model.createdDate.toString(), style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Url", style = MaterialTheme.typography.titleSmall)
            Text(model.url, style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Transcription Url", style = MaterialTheme.typography.titleSmall)
            Text(model.transcriptionUrl, style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Transcription Length", style = MaterialTheme.typography.titleSmall)
            Text(model.transcriptionLength?.toString() ?: "", style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Tags", style = MaterialTheme.typography.titleSmall)
            Text(model.tags.toJson(), style = MaterialTheme.typography.bodySmall)

            Box(modifier = Modifier.height(8.dp))

            Text("Metadata", style = MaterialTheme.typography.titleSmall)
            Text(model.metadata.toJson(), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Test() {
    TruvideosdkmediaTheme {
        MediaResponseListItem(
            model = TruvideoSdkMediaResponse(
                title = "title",
                id = "id",
                transcriptionLength = 0f,
                transcriptionUrl = "transcription url",
                tags = TruvideoSdkMediaTags.builder()
                    .set("key", "value")
                    .build(),
                type = TruvideoSdkMediaResponseType.Video,
                url = "url",
                createdDate = Date(),
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