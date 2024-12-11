package com.truvideo.sdk.media.interfaces

import truvideo.sdk.common.model.TruvideoSdkLogSeverity

internal interface TruvideoSdkMediaLogAdapter {
    fun addLog(
        eventName: String,
        message: String,
        severity: TruvideoSdkLogSeverity
    )
}