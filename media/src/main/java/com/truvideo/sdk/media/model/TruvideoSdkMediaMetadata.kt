package com.truvideo.sdk.media.model

import MetadataMapSerializer
import com.truvideo.sdk.media.util.MetadataConverterUtil
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TruvideoSdkMediaMetadata(
    @Serializable(with = MetadataMapSerializer::class)
    val map: Map<String, @Contextual Any> = emptyMap()
) {
    fun toJson(): String = MetadataConverterUtil.toJson(map)

    companion object {

        @JvmStatic
        fun builder(map: Map<String, @Contextual Any> = emptyMap()): TruvideoSdkMediaMetadataBuilder {
            return TruvideoSdkMediaMetadataBuilder(
                map = map.toMutableMap()
            )
        }

        @JvmStatic
        fun builder(): TruvideoSdkMediaMetadataBuilder {
            return TruvideoSdkMediaMetadataBuilder(
                map = mutableMapOf()
            )
        }

        fun fromJson(json: String): TruvideoSdkMediaMetadata {
            val map = MetadataConverterUtil.toMap(json)
            return TruvideoSdkMediaMetadata(map)
        }
    }
}