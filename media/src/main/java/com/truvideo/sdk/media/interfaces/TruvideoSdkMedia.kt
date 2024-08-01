@file:Suppress("FunctionName")

package com.truvideo.sdk.media.interfaces

import androidx.lifecycle.LiveData
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse

interface TruvideoSdkMedia {

    fun FileUploadRequestBuilder(filePath: String): TruvideoSdkMediaFileUploadRequestBuilder

    fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
        callback: TruvideoSdkMediaCallback<LiveData<List<TruvideoSdkMediaFileUploadRequest>>>
    )

    fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null
    ): LiveData<List<TruvideoSdkMediaFileUploadRequest>>

    suspend fun getFileUploadRequestById(id: String): TruvideoSdkMediaFileUploadRequest?

    fun getFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest?>
    )

    fun streamFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<LiveData<TruvideoSdkMediaFileUploadRequest?>>
    )

    fun streamFileUploadRequestById(id: String): LiveData<TruvideoSdkMediaFileUploadRequest?>

    fun getAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
        callback: TruvideoSdkMediaCallback<List<TruvideoSdkMediaFileUploadRequest>>
    )

    suspend fun getAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus? = null,
    ): List<TruvideoSdkMediaFileUploadRequest>

    fun searchById(id: String, callback: TruvideoSdkMediaCallback<TruvideoSdkMediaResponse?>)

    suspend fun searchById(id: String): TruvideoSdkMediaResponse?

    fun search(
        tags: Map<String, String> = mapOf(),
        type: TruvideoSdkMediaFileType = TruvideoSdkMediaFileType.All,
        pageNumber: Int = 0,
        pageSize: Int = 10,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>>
    )

    suspend fun search(
        tags: Map<String, String> = mapOf(),
        type: TruvideoSdkMediaFileType = TruvideoSdkMediaFileType.All,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>

    val environment: String
}