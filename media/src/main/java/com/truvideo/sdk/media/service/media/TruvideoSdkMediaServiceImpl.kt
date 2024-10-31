package com.truvideo.sdk.media.service.media

import android.util.Log
import com.truvideo.sdk.media.BuildConfig
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaAuthAdapter
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaLogAdapter
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileType
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaPaginatedResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import org.json.JSONArray
import org.json.JSONObject
import truvideo.sdk.common.exceptions.TruvideoSdkException
import truvideo.sdk.common.model.TruvideoSdkLogSeverity
import truvideo.sdk.common.sdk_common
import kotlin.math.min

internal class TruvideoSdkMediaServiceImpl(
    private val authAdapter: TruvideoSdkMediaAuthAdapter,
    private val logAdapter: TruvideoSdkMediaLogAdapter
) : TruvideoSdkMediaService {


    private fun getBaseUrl(): String {
        val flavor = BuildConfig.FLAVOR

        @Suppress("KotlinConstantConditions") return when (flavor) {
            "dev" -> "https://sdk-mobile-api-dev.truvideo.com"
            "beta" -> "https://sdk-mobile-api-beta.truvideo.com"
            "rc" -> "https://sdk-mobile-api-rc.truvideo.com"
            "prod" -> "https://sdk-mobile-api.truvideo.com"
            else -> "https://sdk-mobile-api.truvideo.com"
        }

    }

    override suspend fun createMedia(
        title: String,
        url: String,
        size: Long,
        type: String,
        tags: TruvideoSdkMediaTags,
        metadata: TruvideoSdkMediaMetadata,
        includeInReport: Boolean?
    ): TruvideoSdkMediaResponse {
        authAdapter.validateAuthentication()
        authAdapter.refresh()

        val token = authAdapter.token

        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
        )

        val body = JSONObject().apply {
            put("title", title)
            put("type", type)
            put("url", url)
            put("resolution", "LOW")
            put("size", size)
            put("tags",
                JSONObject().apply { for (tag in tags.map.entries) put(tag.key, tag.value) })
            put("metadata", metadata.toJson())
            if (includeInReport != null) put("includeInReport", includeInReport)
        }

        val baseUrl = getBaseUrl()
        val response = sdk_common.http.post(
            url = "$baseUrl/api/media", headers = headers, body = body.toString(), retry = false
        )

        if (response == null || !response.isSuccess) {
            logAdapter.addLog(
                eventName = "event_media_media_create",
                message = "Error media create post request. ${response?.body}",
                severity = TruvideoSdkLogSeverity.ERROR
            )
            throw TruvideoSdkException("Error creating media")
        }

        val responseBody = JSONObject(response.body)
        val id = responseBody.optString("id")

        val media = fetchAll(
            tags = TruvideoSdkMediaTags(),
            idList = listOf(id),
            type = TruvideoSdkMediaFileType.All,
            pageNumber = 0,
            pageSize = 1
        ).data.firstOrNull()

        if (media == null) {
            throw TruvideoSdkException("Error fetching media")
        }

        return media

    }

    override suspend fun fetchAll(
        tags: TruvideoSdkMediaTags,
        idList: List<String>,
        type: TruvideoSdkMediaFileType,
        pageNumber: Int,
        pageSize: Int
    ): TruvideoSdkMediaPaginatedResponse<TruvideoSdkMediaResponse> {
        authAdapter.validateAuthentication()
        authAdapter.refresh()

        val token = authAdapter.token

        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
        )

        val body = JSONObject()
        body.apply {
            if (idList.isNotEmpty()) {
                put("ids", JSONArray(idList))
            } else {
                put("tags",
                    JSONObject().apply { for (tag in tags.map.entries) put(tag.key, tag.value) })
                if (type.type != null) {
                    put("type", type.type)
                }
            }
        }

        val baseUrl = getBaseUrl()
        val url = buildSearchUrl(
            baseUrl = baseUrl, pageNumber = pageNumber, pageSize = pageSize
        )
        val response = sdk_common.http.post(
            url = url, headers = headers, retry = true, body = body.toString()
        )

        if (response == null || !response.isSuccess) {
            throw TruvideoSdkException("Error creating media")
        }

        val json = JSONObject(response.body)
        val contentArray = json.getJSONArray("content")

        val mediaList = mutableListOf<TruvideoSdkMediaResponse>()

        for (i in 0 until contentArray.length()) {
            val item = contentArray.getJSONObject(i)
            Log.d("TruvideoSdkMedia", item.toString())
            try {
                val mediaFileUploaded = TruvideoSdkMediaResponse.fromJson(item.toString())
                mediaList.add(mediaFileUploaded)
            } catch (exception: Exception) {
                exception.printStackTrace()
                Log.d(
                    "TruvideoSdkMedia", "Error parsing media response ${exception.localizedMessage}"
                )
            }
        }

        return TruvideoSdkMediaPaginatedResponse(
            data = mediaList,
            totalPages = json.optInt("totalPages"),
            totalElements = json.optInt("totalElements"),
            numberOfElements = json.optInt("numberOfElements"),
            size = json.optInt("size"),
            number = json.optInt("number"),
            first = json.optBoolean("first"),
            empty = json.optBoolean("empty"),
            last = json.optBoolean("last"),
        )
    }

    companion object {
        fun buildSearchUrl(baseUrl: String, pageNumber: Int?, pageSize: Int?): String {
            val resolvedPageNumber = pageNumber ?: 0
            val resolvedPageSize = min(100, pageSize ?: 20)
            return "$baseUrl/api/media/search?page=$resolvedPageNumber&size=$resolvedPageSize"
        }
    }
}