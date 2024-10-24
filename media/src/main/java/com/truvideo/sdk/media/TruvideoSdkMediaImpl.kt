package com.truvideo.sdk.media

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.truvideo.sdk.media.adapter.TruvideoSdkMediaVersionPropertiesAdapterImpl
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder
import com.truvideo.sdk.media.engines.TruvideoSdkMediaFileUploadEngine
import com.truvideo.sdk.media.interfaces.TruvideoSdkMedia
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaAuthAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import com.truvideo.sdk.media.repository.TruvideoSdkMediaFileUploadRequestRepository
import com.truvideo.sdk.media.service.media.TruvideoSdkMediaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.TruvideoSdkContextProvider
import truvideo.sdk.common.exceptions.TruvideoSdkException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity

internal class TruvideoSdkMediaImpl(
    context: Context,
    private val authAdapter: TruvideoSdkMediaAuthAdapter,
    private val mediaFileUploadRequestRepository: TruvideoSdkMediaFileUploadRequestRepository,
    private val mediaService: TruvideoSdkMediaService,
    private val fileUploadEngine: TruvideoSdkMediaFileUploadEngine,
    private val logAdapter: TruvideoSdkMediaLogAdapter,
    versionPropertiesAdapter: TruvideoSdkMediaVersionPropertiesAdapterImpl
) : TruvideoSdkMedia {

    private var scope = CoroutineScope(Dispatchers.IO)
    private val moduleVersion = versionPropertiesAdapter.readProperty("versionName") ?: "Unknown"

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
        status: TruvideoSdkMediaFileUploadStatus?
    ): LiveData<List<TruvideoSdkMediaFileUploadRequest>> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_stream_all",
            message = "Streaming all file upload requests with status: $status",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        try {
            val stream = mediaFileUploadRequestRepository.streamAll(status)
            return stream.map { list ->
                list.onEach { item ->
                    item.engine = fileUploadEngine
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()

            val resultException = if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_stream_all",
                message = "Fail to stream all file upload requests with status: $status",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw resultException
        }
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
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
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

        try {
            val request = mediaFileUploadRequestRepository.getById(id) ?: return null
            request.engine = fileUploadEngine
            return request
        } catch (exception: Exception) {
            exception.printStackTrace()

            val resultException = if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_get_by_id",
                message = "Fail to get file upload request by ID: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw resultException
        }
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
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
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

        try {
            val stream = mediaFileUploadRequestRepository.streamById(id)
            return stream.map {
                it?.apply {
                    engine = fileUploadEngine
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()

            val resultException = if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_stream_by_id",
                message = "Fail to stream file upload request by ID: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw resultException
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
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
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

        try {
            val items = mediaFileUploadRequestRepository.getAll(status)
            items.forEach { it.engine = fileUploadEngine }
            return items
        } catch (exception: Exception) {
            exception.printStackTrace()

            val resultException = if (exception is TruvideoSdkException) {
                exception
            } else {
                TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_get_all",
                message = "Fail to get all file upload requests with status: $status",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw resultException
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
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
            }
        }
    }

    override suspend fun searchById(id: String): TruvideoSdkMediaResponse? {
        logAdapter.addLog(
            eventName = "event_media_search_by_id",
            message = "Search by id: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        try {
            val result = mediaService.fetchAll(
                tags = TruvideoSdkMediaTags(),
                idList = listOf(),
                type = TruvideoSdkMediaFileType.All,
                pageNumber = 0,
                pageSize = 1,
            )
            return result.data.firstOrNull()
        } catch (exception: Exception) {
            logAdapter.addLog(
                eventName = "event_media_search_by_id",
                message = "Search by id error: ${exception.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            exception.printStackTrace()

            if (exception is TruvideoSdkException) {
                throw exception
            } else {
                throw TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }
        }
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
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
            }
        }
    }

    override suspend fun search(
        tags: TruvideoSdkMediaTags,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        pageSize: Int,
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse> {
        logAdapter.addLog(
            eventName = "event_media_search",
            message = "Tags: ${tags.map.entries.joinToString(",") { "${it.key}=${it.value}" }}. Type: ${type.name}. Page: $pageNumber. Size: $pageSize",
            severity = TruvideoSdkLogSeverity.INFO
        )

        authAdapter.validateAuthentication()

        try {
            return mediaService.fetchAll(
                tags = tags,
                idList = listOf(),
                type = type,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        } catch (exception: Exception) {
            logAdapter.addLog(
                eventName = "event_media_search",
                message = "Error searching media: ${exception.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            exception.printStackTrace()

            if (exception is TruvideoSdkException) {
                throw exception
            } else {
                throw TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
            }
        }
    }

    override fun search(
        tags: TruvideoSdkMediaTags,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        pageSize: Int,
        callback: TruvideoSdkMediaCallback<TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse>>
    ) {
        scope.launch {
            try {
                val result = search(
                    tags = tags,
                    type = type,
                    pageNumber = pageNumber,
                    pageSize = pageSize
                )
                callback.onComplete(result)
            } catch (exception: Exception) {
                val resultException = if (exception is TruvideoSdkException) {
                    exception
                } else {
                    TruvideoSdkException(exception.localizedMessage ?: "Unknown error")
                }
                callback.onError(resultException)
            }
        }
    }

    override val environment: String
        get() = BuildConfig.FLAVOR

    override val version: String
        get() = moduleVersion
}
