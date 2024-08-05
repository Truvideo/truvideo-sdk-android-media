package com.truvideo.sdk.media.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.truvideo.sdk.media.data.DatabaseInstance
import com.truvideo.sdk.media.data.FileUploadRequestDAO
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadStatus
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import truvideo.sdk.common.exception.TruvideoSdkException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import java.util.Date

internal class TruvideoSdkMediaFileUploadRequestRepositoryImpl(
    private val context: Context,
    private val logAdapter: TruvideoSdkMediaLogAdapter,
) : TruvideoSdkMediaFileUploadRequestRepository {

    private val dao: FileUploadRequestDAO
        get() = DatabaseInstance.getDatabase(context).mediaDao()

    override suspend fun insert(media: TruvideoSdkMediaFileUploadRequest): TruvideoSdkMediaFileUploadRequest {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_insert",
            message = "Inserting file upload request: ${media.toJson()}",
            severity = TruvideoSdkLogSeverity.INFO
        )

        try {
            dao.insert(media)
            return media
        } catch (exception: Exception) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_insert",
                message = "Error inserting file upload request: ${media.toJson()}. ${exception.localizedMessage}",
                severity = TruvideoSdkLogSeverity.INFO
            )
            exception.printStackTrace()
            throw exception
        }
    }

    override suspend fun update(media: TruvideoSdkMediaFileUploadRequest): TruvideoSdkMediaFileUploadRequest {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_update",
            message = "Updating file upload request: ${media.toJson()}",
            severity = TruvideoSdkLogSeverity.INFO
        )

        try {
            media.updatedAt = Date()
            dao.update(media)
            return media
        } catch (exception: Exception) {

            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "Error updating file upload request: ${media.toJson()}. ${exception.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            exception.printStackTrace()
            throw exception
        }
    }

    override suspend fun updateToIdle(id: String): TruvideoSdkMediaFileUploadRequest {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("File upload request not found")
        }

        model.remoteId = null
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.uploadProgress = null
        model.errorMessage = null
        model.status = TruvideoSdkMediaFileUploadStatus.IDLE
        return update(model)
    }

    override suspend fun updateToUploading(
        id: String,
        poolId: String,
        region: String,
        bucketName: String,
        folder: String
    ): TruvideoSdkMediaFileUploadRequest {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.remoteId = null
        model.uploadProgress = null
        model.errorMessage = null
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.poolId = poolId
        model.region = region
        model.bucketName = bucketName
        model.folder = folder
        model.status = TruvideoSdkMediaFileUploadStatus.UPLOADING
        return update(model)
    }

    override suspend fun updateToSynchronizing(id: String) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.remoteId = null
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.uploadProgress = null
        model.errorMessage = null
        model.status = TruvideoSdkMediaFileUploadStatus.SYNCHRONIZING
        update(model)
    }

    override suspend fun updateToError(
        id: String,
        errorMessage: String
    ) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.uploadProgress = null
        model.errorMessage = errorMessage
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.status = TruvideoSdkMediaFileUploadStatus.ERROR
        update(model)
    }

    override suspend fun updateToPaused(id: String) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.errorMessage = null
        model.remoteId = null
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.status = TruvideoSdkMediaFileUploadStatus.PAUSED
        update(model)
    }

    override suspend fun updateToCanceled(id: String) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.uploadProgress = null
        model.errorMessage = null
        model.remoteUrl = null
        model.transcriptionUrl = null
        model.status = TruvideoSdkMediaFileUploadStatus.CANCELED
        update(model)
    }

    override suspend fun updateProgress(id: String, progress: Float) {
        val model = getById(id) ?: return
        model.uploadProgress = progress
        update(model)
    }

    override suspend fun updateToCompleted(id: String, media: TruvideoSdkMediaResponse) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_update",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        model.uploadProgress = null
        model.errorMessage = null
        model.remoteId = media.id
        model.remoteUrl = media.url
        model.tags = media.tags
        model.metadata = media.metadata
        model.status = TruvideoSdkMediaFileUploadStatus.COMPLETED
        model.transcriptionUrl = media.transcriptionUrl
        model.transcriptionLength = media.transcriptionLength
        update(model)
    }

    override suspend fun delete(id: String) {
        val model = getById(id)
        if (model == null) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_delete",
                message = "File upload request not found: $id",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw TruvideoSdkException("File upload request not found")
        }

        logAdapter.addLog(
            eventName = "event_media_file_upload_request_delete",
            message = "Deleting media request: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        try {
            dao.delete(model)
        } catch (exception: Exception) {
            logAdapter.addLog(
                eventName = "event_media_file_upload_request_delete",
                message = "Error deleting file upload request: $id. ${exception.localizedMessage}",
                severity = TruvideoSdkLogSeverity.INFO
            )

            exception.printStackTrace()
            throw exception
        }
    }

    override suspend fun cancelAllProcessing() {
        val items = mutableListOf<TruvideoSdkMediaFileUploadRequest>()
        items.addAll(getAll(TruvideoSdkMediaFileUploadStatus.UPLOADING))
        items.addAll(getAll(TruvideoSdkMediaFileUploadStatus.SYNCHRONIZING))
        items.addAll(getAll(TruvideoSdkMediaFileUploadStatus.PAUSED))

        items.forEach {
            it.status = TruvideoSdkMediaFileUploadStatus.CANCELED
            it.externalId = null
            it.uploadProgress = null
            it.errorMessage = null
            it.remoteId = null
            it.remoteUrl = null
            it.transcriptionUrl = null
            it.transcriptionLength = null
            update(it)
        }
    }

    override suspend fun getById(id: String): TruvideoSdkMediaFileUploadRequest? {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_get_by_id",
            message = "Getting file upload request by ID: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        return dao.getById(id)
    }

    override suspend fun getAll(status: TruvideoSdkMediaFileUploadStatus?): List<TruvideoSdkMediaFileUploadRequest> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_get_all",
            message = "Getting all file upload requests with status: $status",
            severity = TruvideoSdkLogSeverity.INFO
        )

        return if (status != null) {
            dao.getAllByStatus(status)
        } else {
            dao.getAll()
        }
    }

    override fun streamById(id: String): LiveData<TruvideoSdkMediaFileUploadRequest?> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_request_stream_by_id",
            message = "Streaming file upload request by ID: $id",
            severity = TruvideoSdkLogSeverity.INFO
        )

        return dao.streamById(id)
    }

    override fun streamAll(status: TruvideoSdkMediaFileUploadStatus?): LiveData<List<TruvideoSdkMediaFileUploadRequest>> {
        logAdapter.addLog(
            eventName = "event_media_file_upload_requests_stream_all",
            message = "Streaming all file upload requests with status: $status",
            severity = TruvideoSdkLogSeverity.INFO,
        )

        return if (status != null) {
            dao.streamAllByStatus(status)
        } else {
            dao.streamAll()
        }
    }
}