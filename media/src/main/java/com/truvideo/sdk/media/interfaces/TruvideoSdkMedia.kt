@file:Suppress("FunctionName")

package com.truvideo.sdk.media.interfaces

import androidx.lifecycle.LiveData
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags

interface TruvideoSdkMedia {

    fun FileUploadRequestBuilder(filePath: String): TruvideoSdkMediaFileUploadRequestBuilder

    fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null
    ): LiveData<List<TruvideoSdkMediaFileUploadRequest>>

    fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
        callback: TruvideoSdkMediaCallback<LiveData<List<TruvideoSdkMediaFileUploadRequest>>>
    )

    suspend fun getFileUploadRequestById(id: String): TruvideoSdkMediaFileUploadRequest?

    fun getFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest?>
    )

    fun streamFileUploadRequestById(id: String): LiveData<TruvideoSdkMediaFileUploadRequest?>

    fun streamFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<LiveData<TruvideoSdkMediaFileUploadRequest?>>
    )

    suspend fun getAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
    ): List<TruvideoSdkMediaFileUploadRequest>

    fun getAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
        callback: TruvideoSdkMediaCallback<List<TruvideoSdkMediaFileUploadRequest>>
    )

    suspend fun searchById(id: String): TruvideoSdkMediaResponse?

    fun searchById(id: String, callback: TruvideoSdkMediaCallback<TruvideoSdkMediaResponse?>)

    suspend fun search(
        tags: TruvideoSdkMediaTags = TruvideoSdkMediaTags(),
        type: TruvideoSdkMediaFileType = TruvideoSdkMediaFileType.All,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>

    fun search(
        tags: TruvideoSdkMediaTags = TruvideoSdkMediaTags(),
        type: TruvideoSdkMediaFileType = TruvideoSdkMediaFileType.All,
        pageNumber: Int = 0,
        pageSize: Int = 10,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>>
    )

    val environment: String
}