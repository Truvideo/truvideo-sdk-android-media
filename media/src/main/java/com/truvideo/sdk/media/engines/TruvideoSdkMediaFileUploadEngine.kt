package com.truvideo.sdk.media.engines

import android.content.Context
import android.net.Uri
import com.truvideo.sdk.media.interfaces.FileUploadCallback
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.repository.TruvideoSdkMediaFileUploadRequestRepository
import com.truvideo.sdk.media.service.media.TruvideoSdkMediaServiceImpl
import com.truvideo.sdk.media.usecases.UploadFileUseCase
import com.truvideo.sdk.media.util.FileUriUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import truvideo.sdk.common.exception.TruvideoSdkException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import truvideo.sdk.common.sdk_common
import java.io.File
import java.util.Locale

internal class TruvideoSdkMediaFileUploadEngine(
    private val context: Context,
    private val uploadFileUseCase: UploadFileUseCase,
    private val repository: TruvideoSdkMediaFileUploadRequestRepository,
    private val mediaService: TruvideoSdkMediaServiceImpl,
    private val logAdapter: TruvideoSdkMediaLogAdapter
) : TruvideoSdkMediaEngine {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun upload(id: String, callback: TruvideoSdkMediaFileUploadCallback) {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_start",
            message = "Starting file upload request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        // Get credentials
        val credentials = sdk_common.auth.settings.value?.credentials
        if (credentials == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_start",
                message = "Invalid credentials",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Invalid credentials")
        }

        // Get entity
        var entity = repository.getById(id)
        if (entity == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_start",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("File upload request not found")
        }

        // Validate state
        val validStatus = listOf(
            TruvideoSdkMediaFileUploadStatus.IDLE,
            TruvideoSdkMediaFileUploadStatus.ERROR,
            TruvideoSdkMediaFileUploadStatus.CANCELED,
        )
        if (!validStatus.contains(entity.status)) {
            val statusString = validStatus.joinToString(", ") { it.name }
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_start",
                message = "Invalid state. Must be on: $statusString",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("Invalid state. Must be on: $statusString")
        }

        // Update to uploading
        entity = repository.updateToUploading(
            entity.id,
            poolId = credentials.identityPoolID,
            region = credentials.region,
            bucketName = credentials.bucketName,
            folder = credentials.bucketFolderMedia
        )

        val mutex = Mutex()

        try {
            val s3Id = uploadFileUseCase(
                filePath = entity.filePath,
                bucketName = entity.bucketName,
                region = entity.region,
                poolId = entity.poolId,
                folder = entity.folder,
                callback = object : FileUploadCallback {
                    override fun onStateChanged(
                        uploadId: Int,
                        state: TruvideoSdkMediaFileUploadStatus,
                        ex: TruvideoSdkException?
                    ) {
                        scope.launch {
                            mutex.withLock {
                                if (state == TruvideoSdkMediaFileUploadStatus.ERROR) {
                                    val errorMessage = ex?.localizedMessage ?: "Unknown error"
                                    logAdapter.addLog(
                                        eventName = "event_media_file_upload_request_start",
                                        message = "Error uploading: $id. $errorMessage",
                                        severity = TruvideoSdkLogSeverity.ERROR
                                    )

                                    repository.updateToError(id, errorMessage)
                                    callback.onError(id, TruvideoSdkException(errorMessage))
                                }
                            }
                        }
                    }


                    override fun onComplete(uploadId: Int, url: String) {
                        scope.launch {
                            mutex.withLock {
                                try {
                                    // Move to synchronizing
                                    repository.updateToSynchronizing(id)

                                    // Create truvideo entity
                                    val file = File(entity.filePath)
                                    val mimeType = FileUriUtil.getMimeType(context, Uri.fromFile(file))
                                    val type = mimeType.split("/")[0].uppercase(Locale.ROOT)
                                    val media = mediaService.createMedia(
                                        title = file.name,
                                        url = url,
                                        size = file.length(),
                                        type = type,
                                        tags = entity.tags,
                                        metadata = entity.metadata
                                    )

                                    // Move to completed
                                    repository.updateToCompleted(id, media)
                                    val response = repository.getById(id)
                                    if (response != null) {
                                        callback.onComplete(entity.id, response)
                                    }

                                    if (entity.deleteOnComplete) {
                                        repository.delete(id)
                                    }

                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                    val message = exception.localizedMessage ?: "Unknown error"

                                    logAdapter.addLog(
                                        eventName = "event_media_file_upload_request_start",
                                        message = "Error creating media entity: $id. $message",
                                        severity = TruvideoSdkLogSeverity.ERROR
                                    )

                                    // Move to error
                                    repository.updateToError(id, message)
                                    callback.onError(entity.id, TruvideoSdkException(message))
                                }
                            }
                        }
                    }

                    override fun onProgressChanged(uploadId: Int, progress: Float) {
                        scope.launch {
                            mutex.withLock {
                                repository.updateProgress(id, progress)
                                callback.onProgressChanged(entity.id, progress)
                            }
                        }
                    }
                }
            )

            // Update external id
            repository.update(entity.apply { externalId = s3Id })
        } catch (exception: Exception) {
            exception.printStackTrace()
            val message = exception.localizedMessage ?: "Unknown error"

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_start",
                message = "Unknown error: $id. $message",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException(message)
        }
    }

    suspend fun cancel(id: String) {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_cancel",
            message = "Canceling file upload request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        val entity = repository.getById(id)
        if (entity == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_cancel",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("File upload request not found")
        }

        // Validate status
        val validStatus = listOf(
            TruvideoSdkMediaFileUploadStatus.IDLE,
            TruvideoSdkMediaFileUploadStatus.PAUSED,
            TruvideoSdkMediaFileUploadStatus.UPLOADING,
            TruvideoSdkMediaFileUploadStatus.ERROR
        )

        if (!validStatus.contains(entity.status)) {
            val statusString = validStatus.joinToString(", ") { it.name }
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_cancel",
                message = "Invalid state. Must be on: $statusString",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("Invalid state. Must be on: $statusString")
        }

        // Cancel s3
        val externalId = entity.externalId
        if (externalId != null) {
            uploadFileUseCase.cancel(
                region = entity.region, poolId = entity.poolId, id = externalId
            )
        }

        // Update to cancel
        repository.updateToCanceled(entity.id)
    }

    suspend fun delete(id: String) {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_delete",
            message = "Deleting file upload request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        val entity = repository.getById(id)

        // IF exists, cancel upload from s3
        if (entity != null) {
            val externalId = entity.externalId
            if (externalId != null) {
                uploadFileUseCase.cancel(
                    region = entity.region, poolId = entity.poolId, id = externalId
                )
            }
        }

        // Delete entity
        repository.delete(id)
    }

    suspend fun pause(id: String) {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_pause",
            message = "Pausing file upload request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        val entity = repository.getById(id)
        if (entity == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_pause",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("File upload request not found")
        }

        // Validate status
        val validStatus = listOf(TruvideoSdkMediaFileUploadStatus.UPLOADING)
        if (!validStatus.contains(entity.status)) {
            val statusString = validStatus.joinToString(", ") { it.name }
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_pause",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Invalid state. Must be on: $statusString")
        }

        // Pause s3
        val externalId = entity.externalId
        if (externalId == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_pause",
                message = "External id not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Upload request not found")
        }

        uploadFileUseCase.pause(
            region = entity.region, poolId = entity.poolId, id = externalId
        )

        // Update to paused
        entity.status = TruvideoSdkMediaFileUploadStatus.PAUSED
        repository.update(entity)
    }

    suspend fun resume(id: String) {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_resume",
            message = "Resuming file upload request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        val entity = repository.getById(id)
        if (entity == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_resume",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("File upload request not found")
        }

        // Valid status
        val validStatus = listOf(TruvideoSdkMediaFileUploadStatus.PAUSED)
        if (!validStatus.contains(entity.status)) {
            val statusString = validStatus.joinToString(", ") { it.name }
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_resume",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Invalid state. Must be on $statusString")
        }

        // Resume s3
        val externalId = entity.externalId
        if (externalId == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_resume",
                message = "External id not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Upload request not found")
        }

        uploadFileUseCase.resume(
            region = entity.region, poolId = entity.poolId, id = externalId
        )

        // Update to uploading
        entity.status = TruvideoSdkMediaFileUploadStatus.UPLOADING
        repository.update(entity)
    }
}