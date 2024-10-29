package com.truvideo.sdk.media.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TruVideoSdkMediaFileUploadResponse(
    val id: String = "",
    var url: String = "",
    var transcriptionUrl: String? = null,
    var transcriptionLength: Float? = null,
    var tags: Map<String, String> = mapOf(),
    var metadata: String = "",
    var includeInReport: Boolean? = null,
){
    fun toJson(): String = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String): TruVideoSdkMediaFileUploadResponse {
            val jsonConfig = Json {
                ignoreUnknownKeys = true
            }
            return jsonConfig.decodeFromString(json)
        }
    }
}