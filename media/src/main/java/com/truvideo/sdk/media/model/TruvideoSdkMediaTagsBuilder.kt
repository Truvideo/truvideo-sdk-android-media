package com.truvideo.sdk.media.model

class TruvideoSdkMediaTagsBuilder(
    val map: MutableMap<String, String> = mutableMapOf()
) {
    operator fun set(key: String, value: String): TruvideoSdkMediaTagsBuilder {
        map[key] = value
        return this
    }

    fun remove(key: String): TruvideoSdkMediaTagsBuilder {
        map.remove(key)
        return this
    }

    fun clear(): TruvideoSdkMediaTagsBuilder {
        map.clear()
        return this
    }

    fun build(): TruvideoSdkMediaTags {
        return TruvideoSdkMediaTags(map)
    }
}