package com.truvideo.sdk.media.example

import com.truvideo.sdk.media.TruvideoSdkMedia
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import truvideo.sdk.common.exceptions.TruvideoSdkException

private class TestKotlin {
    suspend fun uploadFile(filePath: String) {
        // Create a file upload request builder
        val builder = TruvideoSdkMedia.FileUploadRequestBuilder(filePath)

        // --------------------------
        // TAGS
        // --------------------------

        // Option 1: use the file upload request builder directly
        builder.addTag("key", "value")
        builder.addTag("color", "red")
        builder.addTag("order-number", "123")

        // Option 2: use the tag builder
        val tags = TruvideoSdkMediaTags
            .builder()
            .set("key", "value")
            .set("color", "red")
            .set("order-number", "123")
            .build()
        builder.setTags(tags)

        // --------------------------
        // METADATA
        // --------------------------

        // Option 1: use the file upload request builder directly
        builder.addMetadata("key", "value")
        builder.addMetadata("list", listOf("value1", "value2"))
        builder.addMetadata(
            "nested", TruvideoSdkMediaMetadata.builder()
                .set("key", "value")
                .set("list", listOf("value1", "value2"))
                .build()
        )

        // Options2: use the metadata builder
        val metadata = TruvideoSdkMediaMetadata
            .builder()
            .set("key", "value")
            .set("list", listOf("value1", "value2"))
            .set(
                "nested", TruvideoSdkMediaMetadata.builder()
                    .set("key", "value")
                    .set("list", listOf("value1", "value2"))
                    .build()
            )
            .build()
        builder.setMetadata(metadata)

        // Build the request
        val request = try {
            builder.build()
        } catch (exception: Exception) {
            // handle creating request error
            throw Exception("Error creating the file upload request")
        }

        // Upload the file
        try {
            request.upload(object : TruvideoSdkMediaFileUploadCallback {
                override fun onComplete(id: String, response: TruvideoSdkMediaFileUploadRequest) {
                    // File uploaded successfully
                }

                override fun onProgressChanged(id: String, progress: Float) {
                    // Handle uploading progress
                }

                override fun onError(id: String, ex: TruvideoSdkException) {
                    // Handle file uploading error
                }
            })
        } catch (exception: Exception) {
            // Handle start to upload error
            throw Exception("Error starting to upload the file")
        }
    }
}