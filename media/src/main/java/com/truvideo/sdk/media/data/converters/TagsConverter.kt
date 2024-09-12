package com.truvideo.sdk.media.data.converters

import androidx.room.TypeConverter
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import com.truvideo.sdk.media.util.TagsConverterUtil

internal class TagsConverter {

    @TypeConverter
    fun fromEntity(entity: TruvideoSdkMediaTags?): String? {
        if (entity == null) return null
        return TagsConverterUtil.toJson(entity.map)
    }

    @TypeConverter
    fun toEntity(json: String?): TruvideoSdkMediaTags? {
        if (json == null) return null
        return TruvideoSdkMediaTags(
            map = TagsConverterUtil.toMap(json)
        )
    }
}