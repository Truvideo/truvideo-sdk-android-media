package com.truvideo.sdk.media.model

class TruvideoSdkMediaMetadataBuilder(
    val map: MutableMap<String, Any> = mutableMapOf()
) {

    operator fun set(key: String, value: String): TruvideoSdkMediaMetadataBuilder {
        map[key] = value
        return this
    }

    operator fun set(key: String, value: List<String>): TruvideoSdkMediaMetadataBuilder {
        map[key] = value
        return this
    }

    operator fun set(key: String, value: Array<String>): TruvideoSdkMediaMetadataBuilder {
        map[key] = value.toList()
        return this
    }

    operator fun set(key: String, value: TruvideoSdkMediaMetadata): TruvideoSdkMediaMetadataBuilder {
        map[key] = value.map
        return this
    }

    fun remove(key: String): TruvideoSdkMediaMetadataBuilder {
        map.remove(key)
        return this
    }

    fun clear(): TruvideoSdkMediaMetadataBuilder {
        map.clear()
        return this
    }

    fun build(): TruvideoSdkMediaMetadata {
        return TruvideoSdkMediaMetadata(map.toMap())
    }
}