package com.truvideo.sdk.media.model

enum class TruvideoSdkMediaFileUploadRequestMediaType(internal val type: String) {
    VIDEO("VIDEO"),
    IMAGE("IMAGE"),
    AUDIO("AUDIO"),
    DOCUMENT("DOCUMENT"),
    UNKNOWN("UNKNOWN");

    val withDuration: Boolean
        get() {
            return this == VIDEO || this == AUDIO
        }
}