package com.truvideo.sdk.media.data.converters

import android.util.Log
import androidx.room.TypeConverter
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.util.MetadataConverterUtil

internal class MetadataConverter {

    @TypeConverter
    fun fromEntity(entity: TruvideoSdkMediaMetadata?): String? {
        if (entity == null) return null
        return MetadataConverterUtil.toJson(entity.map)
    }

    @TypeConverter
    fun toEntity(json: String?): TruvideoSdkMediaMetadata? {
        if (json == null) return null
        return TruvideoSdkMediaMetadata(
            map = MetadataConverterUtil.toMap(json)
        )
    }
}