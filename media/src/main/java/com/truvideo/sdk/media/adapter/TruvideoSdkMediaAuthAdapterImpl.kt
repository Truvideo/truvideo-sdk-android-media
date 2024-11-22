package com.truvideo.sdk.media.adapter

import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaAuthAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import truvideo.sdk.common.exceptions.TruvideoSdkAuthenticationRequiredException
import truvideo.sdk.common.exceptions.TruvideoSdkException
import truvideo.sdk.common.exceptions.TruvideoSdkNotInitializedException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import truvideo.sdk.common.sdk_common
import truvideo.sdk.common.util.TruvideoSdkCommonExceptionParser
import truvideo.sdk.common.util.parse

internal class TruvideoSdkMediaAuthAdapterImpl(
    private val versionPropertiesAdapter: TruvideoSdkMediaVersionPropertiesAdapterImpl,
    private val logAdapter: TruvideoSdkMediaLogAdapter
) : TruvideoSdkMediaAuthAdapter {
    private val validateAuthentication: Boolean = versionPropertiesAdapter.readProperty("validateAuthentication") != "false"

    private fun isAuthenticated(): Boolean {
        if (!validateAuthentication) return true

        try {
            return sdk_common.auth.isAuthenticated()
        } catch (exception: Exception) {
            val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
            parsedException.printStackTrace()

            logAdapter.addLog(
                eventName = "event_media_auth_validate_is_authenticated",
                message = "Validate is authenticated failed: ${parsedException.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw exception
        }
    }

    private fun isInitialized(): Boolean {
        if (!validateAuthentication) return true

        try {
            return sdk_common.auth.isInitialized
        } catch (exception: Exception) {
            val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
            parsedException.printStackTrace()

            logAdapter.addLog(
                eventName = "event_media_auth_validate_is_initialized",
                message = "Validate is initialized failed: ${parsedException.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw exception
        }
    }

    override fun validateAuthentication() {
        val isAuthenticated = isAuthenticated()
        if (!isAuthenticated) {
            logAdapter.addLog(
                eventName = "event_media_auth_validate",
                message = "Validate authentication failed: SDK not authenticated",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkAuthenticationRequiredException()
        }

        val isInitialized = isInitialized()
        if (!isInitialized) {
            logAdapter.addLog(
                eventName = "event_media_auth_validate",
                message = "Validate authentication failed: SDK not initialized",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkNotInitializedException()
        }
    }

    override suspend fun refresh() {
        try {
            sdk_common.auth.refresh()
        } catch (exception: Exception) {
            val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
            parsedException.printStackTrace()

            logAdapter.addLog(
                eventName = "event_media_auth_refresh",
                message = "Refresh failed: ${parsedException.localizedMessage}",
                severity = TruvideoSdkLogSeverity.ERROR
            )

            throw parsedException
        }
    }

    override val token: String
        get() {
            val accessToken = try {
                sdk_common.auth.getAuthentication()?.accessToken
            } catch (exception: Exception) {
                val parsedException = TruvideoSdkCommonExceptionParser().parse(exception)
                parsedException.printStackTrace()
                logAdapter.addLog(
                    eventName = "event_media_auth_get_token",
                    message = "Get token failed: ${parsedException.localizedMessage}",
                    severity = TruvideoSdkLogSeverity.ERROR
                )
                throw parsedException
            }

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