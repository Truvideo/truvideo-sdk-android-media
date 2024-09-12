package com.truvideo.sdk.media.model

import MetadataSerializer
import TagsSerializer
import com.truvideo.sdk.media.model.serializers.IsoDateSerializer
import com.truvideo.sdk.media.model.serializers.TruvideoSdkMediaResponseTypeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

@Serializable
data class TruvideoSdkMediaResponse(
    val id: String,

    @Serializable(with = TruvideoSdkMediaResponseTypeSerializer::class)
    val type: TruvideoSdkMediaResponseType = TruvideoSdkMediaResponseType.Unknown,

    val title: String = "",
    val url: String,
    val transcriptionUrl: String = "",
    val transcriptionLength: Float?,
    val sendEOM: Boolean,

    @Serializable(with = IsoDateSerializer::class)
    val createdDate: Date,

    @Serializable(with = TagsSerializer::class)
    val tags: TruvideoSdkMediaTags = TruvideoSdkMediaTags(),

    @Serializable(with = MetadataSerializer::class)
    val metadata: TruvideoSdkMediaMetadata = TruvideoSdkMediaMetadata()
) {
    fun toJson(): String = jsonConfig.encodeToString(this)

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val jsonConfig = Json {
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
            prettyPrint = true
            useArrayPolymorphism = true
            explicitNulls = false
        }

        fun fromJson(json: String): TruvideoSdkMediaResponse {
            return jsonConfig.decodeFromString(json)
        }
    }
}