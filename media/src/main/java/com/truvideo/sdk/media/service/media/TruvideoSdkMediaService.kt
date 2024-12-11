package com.truvideo.sdk.media.service.media

import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequestMediaType
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags

internal interface TruvideoSdkMediaService {
    suspend fun createMedia(
        title: String,
        url: String,
        size: Long,
        duration: Long? = null,
        type: TruvideoSdkMediaFileUploadRequestMediaType,
        tags: TruvideoSdkMediaTags,
        metadata: TruvideoSdkMediaMetadata,
        includeInReport: Boolean?
    ): TruvideoSdkMediaResponse

    suspend fun fetchAll(
        tags: TruvideoSdkMediaTags = TruvideoSdkMediaTags(),
        idList: List<String>,
        type: TruvideoSdkMediaFileType = TruvideoSdkMediaFileType.All,
        pageNumber: Int,
        pageSize: Int
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>
}