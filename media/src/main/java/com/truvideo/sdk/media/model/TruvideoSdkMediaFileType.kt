package com.truvideo.sdk.media.model

enum class TruvideoSdkMediaFileType(internal val type: String?) {
    Video("VIDEO"),
    Picture("IMAGE"),
    PDF("DOCUMENT"),
    AUDIO("AUDIO"),
    All(null)
}