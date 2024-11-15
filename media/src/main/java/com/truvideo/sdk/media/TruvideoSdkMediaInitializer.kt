package com.truvideo.sdk.media

import android.content.Context
import androidx.startup.Initializer
import com.truvideo.sdk.media.adapter.TruvideoSdkMediaAuthAdapterImpl
import com.truvideo.sdk.media.adapter.TruvideoSdkMediaLogAdapterImpl
import com.truvideo.sdk.media.adapter.TruvideoSdkMediaVersionPropertiesAdapterImpl
import com.truvideo.sdk.media.engines.TruvideoSdkMediaFileUploadEngine
import com.truvideo.sdk.media.repository.TruvideoSdkMediaFileUploadRequestRepositoryImpl
import com.truvideo.sdk.media.service.media.TruvideoSdkMediaServiceImpl
import com.truvideo.sdk.media.usecases.S3ClientUseCase
import com.truvideo.sdk.media.usecases.UploadFileUseCase

@Suppress("unused")
class TruvideoSdkMediaInitializer : Initializer<Unit> {

    companion object {
        fun init(context: Context) {
            val versionPropertiesAdapter = TruvideoSdkMediaVersionPropertiesAdapterImpl(
                context = context
            )
            
            val logAdapter = TruvideoSdkMediaLogAdapterImpl(
                versionPropertiesAdapter = versionPropertiesAdapter
            )

            val authAdapter = TruvideoSdkMediaAuthAdapterImpl(
                versionPropertiesAdapter = versionPropertiesAdapter,
                logAdapter = logAdapter
            )

            val mediaFileUploadRequestRepository = TruvideoSdkMediaFileUploadRequestRepositoryImpl(
                context = context,
                logAdapter = logAdapter
            )

            val s3ClientUseCase = S3ClientUseCase(
                context = context
            )

            val mediaService = TruvideoSdkMediaServiceImpl(
                authAdapter = authAdapter,
                logAdapter = logAdapter
            )

            val uploadFileUseCase = UploadFileUseCase(
                context = context, s3ClientUseCase = s3ClientUseCase
            )

            val fileUploadEngine = TruvideoSdkMediaFileUploadEngine(
                context = context,
                uploadFileUseCase = uploadFileUseCase,
                repository = mediaFileUploadRequestRepository,
                mediaService = mediaService,
                logAdapter = logAdapter
            )

            TruvideoSdkMedia = TruvideoSdkMediaImpl(
                context = context,
                authAdapter = authAdapter,
                mediaFileUploadRequestRepository = mediaFileUploadRequestRepository,
                fileUploadEngine = fileUploadEngine,
                logAdapter = logAdapter,
                mediaService = mediaService,
                versionPropertiesAdapter = versionPropertiesAdapter
            )
        }
    }

    override fun create(context: Context) {
        init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}