package com.truvideo.sdk.media.adapter

import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaVersionPropertiesAdapter
import truvideo.sdk.common.model.TruvideoSdkLog
import truvideo.sdk.common.model.TruvideoSdkLogModule
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import truvideo.sdk.common.sdk_common

internal class TruvideoSdkMediaLogAdapterImpl(
    versionPropertiesAdapter: TruvideoSdkMediaVersionPropertiesAdapter
) : TruvideoSdkMediaLogAdapter {

    private val moduleVersion = versionPropertiesAdapter.readProperty("versionName") ?: "Unknown"

    override fun addLog(eventName: String, message: String, severity: TruvideoSdkLogSeverity) {
        sdk_common.log.add(
            TruvideoSdkLog(
                tag = eventName,
                message = message,
                severity = severity,
                module = TruvideoSdkLogModule.MEDIA,
                moduleVersion = moduleVersion,
            )
        )
    }
}