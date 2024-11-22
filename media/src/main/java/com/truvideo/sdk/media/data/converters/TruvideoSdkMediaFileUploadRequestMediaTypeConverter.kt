package com.truvideo.sdk.media.data.converters

import androidx.room.TypeConverter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequestMediaType

internal class TruvideoSdkMediaFileUploadRequestMediaTypeConverter {
    @TypeConverter
    fun fromStatus(status: TruvideoSdkMediaFileUploadRequestMediaType): String {
        return status.type
    }

    @TypeConverter
    fun toStatus(status: String): TruvideoSdkMediaFileUploadRequestMediaType {
        try {
            return TruvideoSdkMediaFileUploadRequestMediaType.entries.first { it.type == status }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return TruvideoSdkMediaFileUploadRequestMediaType.UNKNOWN
        }
    }
}