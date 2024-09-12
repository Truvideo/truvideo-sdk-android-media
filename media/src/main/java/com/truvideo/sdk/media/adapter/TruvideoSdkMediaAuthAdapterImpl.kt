package com.truvideo.sdk.media.adapter

import android.util.Log
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaAuthAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import truvideo.sdk.common.exception.TruvideoSdkAuthenticationRequiredException
import truvideo.sdk.common.exception.TruvideoSdkException
import truvideo.sdk.common.exception.TruvideoSdkNotInitializedException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import truvideo.sdk.common.sdk_common

internal class TruvideoSdkMediaAuthAdapterImpl(
    private val versionPropertiesAdapter: TruvideoSdkMediaVersionPropertiesAdapterImpl,
    private val logAdapter: TruvideoSdkMediaLogAdapter
) : TruvideoSdkMediaAuthAdapter {
    private val validateAuthentication: Boolean = versionPropertiesAdapter.readProperty("validateAuthentication") != "false"

    override fun validateAuthentication() {
        if (!validateAuthentication) {
            Log.d("TruvideoSdkMedia", "skipping auth validation ${versionPropertiesAdapter.readProperty("validateAuthentication")}")
            return
        }

        val isAuthenticated = sdk_common.auth.isAuthenticated
        if (!isAuthenticated) {
            logAdapter.addLog(
                eventName = "event_media_auth_validate",
                message = "Validate authentication failed: SDK not authenticated",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkAuthenticationRequiredException()
        }

        val isInitialized = sdk_common.auth.isInitialized
        if (!isInitialized) {
            logAdapter.addLog(
                eventName = "event_media_auth_validate",
                message = "Validate authentication failed: SDK not initialized",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkNotInitializedException()
        }
    }

    override suspend fun refresh() = sdk_common.auth.refresh()

    override val token: String
        get() {
            val accessToken = sdk_common.auth.authentication?.accessToken
            if (accessToken == null) {
                logAdapter.addLog(
                    eventName = "event_media_auth_validate",
                    message = "No access token found",
                    severity = TruvideoSdkLogSeverity.ERROR
                )
                throw TruvideoSdkException("No access token found")
            }
            return accessToken
        }
}