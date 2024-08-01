package com.truvideo.sdk.media.model

import TagsMapSerializer
import com.truvideo.sdk.media.util.TagsConverterUtil
import kotlinx.serialization.Serializable

@Serializable
data class TruvideoSdkMediaTags(
    @Serializable(with = TagsMapSerializer::class)
    val map: Map<String, String> = emptyMap()
) {

    fun toJson(): String = TagsConverterUtil.toJson(map)

    companion object {

        fun builder(map: Map<String, String> = emptyMap()): TruvideoSdkMediaTagsBuilder {
            return TruvideoSdkMediaTagsBuilder(map = map.toMutableMap())
        }

        fun fromJson(json: String): TruvideoSdkMediaTags {
            val map = TagsConverterUtil.toMap(json)
            return TruvideoSdkMediaTags(map)
        }
    }
}