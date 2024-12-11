package com.truvideo.sdk.media.model

enum class TruvideoSdkMediaResponseType(internal val value: String) {
    VIDEO("VIDEO"),
    AUDIO("AUDIO"),
    IMAGE("IMAGE"),
    DOCUMENT("DOCUMENT"),
    UNKNOWN("UNKNOWN")
}