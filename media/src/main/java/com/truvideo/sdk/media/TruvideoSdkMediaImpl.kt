package com.truvideo.sdk.media

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder
import com.truvideo.sdk.media.engines.TruvideoSdkMediaFileUploadEngine
import com.truvideo.sdk.media.exception.TruvideoSdkMediaException
import com.truvideo.sdk.media.interfaces.TruvideoSdkMedia
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaAuthAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.repository.TruvideoSdkMediaFileUploadRequestRepository
import com.truvideo.sdk.media.service.media.TruvideoSdkMediaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.TruvideoSdkContextProvider
import truvideo.sdk.common.model.TruvideoSdkLogSeverity

internal class TruvideoSdkMediaImpl(
    context: Context,
    private val authAdapter: TruvideoSdkMediaAuthAdapter,
    private val mediaFileUploadRequestRepository: TruvideoSdkMediaFileUploadRequestRepository,
    private val mediaService: TruvideoSdkMediaService,
    private val fileUploadEngine: TruvideoSdkMediaFileUploadEngine,
    private val logAdapter: TruvideoSdkMediaLogAdapter
) : TruvideoSdkMedia {

    private var scope = CoroutineScope(Dispatchers.IO)

    init {
        TruvideoSdkContextProvider.instance.init(context)

        logAdapter.addLog(
            eventName = "event_media_init",
            message = "Init media module",
            severity = TruvideoSdkLogSeverity.INFO
        )

        scope.launch { mediaFileUploadRequestRepository.cancelAllProcessing() }
    }

    override fun FileUploadRequestBuilder(filePath: String): TruvideoSdkMediaFileUploadRequestBuilder {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_build_create",
            message = "Building file upload request for path: $filePath",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        return TruvideoSdkMediaFileUploadRequestBuilder(
            filePath = filePath,
            mediaRepository = mediaFileUploadRequestRepository,
            engine = fileUploadEngine
        )
    }

    override fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus?,
        callback: TruvideoSdkMediaCallback<LiveData<List<TruvideoSdkMediaFileUploadRequest>>>
    ) {
        scope.launch {
            try {
                val data = streamAllFileUploadRequests(status)
                callback.onComplete(data)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkMediaException(exception.message ?: "Unknown message"))
            }
        }
    }

    override fun streamAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus?
    ): LiveData<List<TruvideoSdkMediaFileUploadRequest>> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_stream_all",
            message = "Streaming all file upload requests with status: $status",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        val stream = mediaFileUploadRequestRepository.streamAll(status)
        return stream.map { list ->
            list.onEach { item ->
                item.engine = fileUploadEngine
            }
        }
    }

    override suspend fun getFileUploadRequestById(id: String): TruvideoSdkMediaFileUploadRequest? {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_get_by_id",
            message = "Getting file upload request by ID: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        val request = mediaFileUploadRequestRepository.getById(id) ?: return null
        request.engine = fileUploadEngine
        return request
    }

    override fun getFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest?>
    ) {
        scope.launch {
            try {
                val request = getFileUploadRequestById(id)
                callback.onComplete(request)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkMediaException(exception.message ?: "Unknown message"))
            }
        }
    }

    override fun streamFileUploadRequestById(
        id: String,
        callback: TruvideoSdkMediaCallback<LiveData<TruvideoSdkMediaFileUploadRequest?>>
    ) {
        scope.launch {
            try {
                val data = streamFileUploadRequestById(id)
                callback.onComplete(data)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkMediaException(exception.message ?: "Unknown message"))
            }
        }
    }

    override fun streamFileUploadRequestById(id: String): LiveData<TruvideoSdkMediaFileUploadRequest?> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_stream_by_id",
            message = "Streaming file upload request by ID: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        val stream = mediaFileUploadRequestRepository.streamById(id)
        return stream.map {
            it?.apply {
                engine = fileUploadEngine
            }
        }
    }

    override fun getAllFileUploadRequests(
        status: TruvideoSdkMediaFileUploadStatus?,
        callback: TruvideoSdkMediaCallback<List<TruvideoSdkMediaFileUploadRequest>>
    ) {
        scope.launch {
            try {
                val data = getAllFileUploadRequests(status)
                callback.onComplete(data)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkMediaException(exception.message ?: "Unknown message"))
            }
        }
    }

    override suspend fun getAllFileUploadRequests(status: TruvideoSdkMediaFileUploadStatus?): List<TruvideoSdkMediaFileUploadRequest> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_get_all",
            message = "Getting all file upload requests with status: $status",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        val items = mediaFileUploadRequestRepository.getAll(status)
        items.forEach { it.engine = fileUploadEngine }
        return items
    }

    override fun searchById(
        id: String,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaResponse?>
    ) {
        scope.launch {
            try {
                val result = searchById(id)
                callback.onComplete(result)
            } catch (exception: Exception) {
                Log.d("TruvideoSdkMedia", "Error searching media by id ${exception.localizedMessage}")
                exception.printStackTrace()

                if (exception is TruvideoSdkMediaException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkMediaException("Unknown error"))
                }
            }
        }
    }

    override suspend fun searchById(id: String) = fetchAllMedia(
        tags = mapOf(),
        idList = listOf(id),
        type = TruvideoSdkMediaFileType.All,
        pageNumber = 0,
        size = 1
    ).data.firstOrNull()

    override fun search(
        tags: Map<String, String>,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        pageSize: Int,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>>
    ) {
        scope.launch {
            try {
                val result = fetchAllMedia(
                    tags = tags,
                    idList = listOf(),
                    type = type,
                    pageNumber = pageNumber,
                    size = pageSize,
                )
                callback.onComplete(result)
            } catch (exception: Exception) {
                Log.d("TruvideoSdkMedia", "Error searching media ${exception.localizedMessage}")
                exception.printStackTrace()

                if (exception is TruvideoSdkMediaException) {
                    callback.onError(exception)
                } else {
                    callback.onError(TruvideoSdkMediaException("Unknown error"))
                }
            }
        }
    }

    override suspend fun search(
        tags: Map<String, String>,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        pageSize: Int,
    ) = fetchAllMedia(
        tags = tags,
        idList = listOf(),
        type = type,
        pageNumber = pageNumber,
        size = pageSize
    )

    private suspend fun fetchAllMedia(
        tags: Map<String, String>,
        idList: List<String>,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        size: Int
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse> {
        authAdapter.validateAuthentication()
        return mediaService.fetchAll(
            tags = tags,
            idList = idList,
            type = type,
            pageNumber = pageNumber,
            pageSize = size
        )
    }

    override val environment: String
        get() = BuildConfig.FLAVOR
}
