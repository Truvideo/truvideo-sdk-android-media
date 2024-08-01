package com.truvideo.sdk.media.util

import android.util.Log
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object MetadataConverterUtil {

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
        prettyPrint = true
        useArrayPolymorphism = true
        explicitNulls = false
    }

    fun toJson(value: Map<String, Any>): String {
        try {
            val jsonElement = toJsonElement(value)
            return jsonConfig.encodeToString(JsonObject.serializer(), jsonElement)
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.d("TruvideoSdkMedia", "Error parsing metadata ${exception.localizedMessage}")
            return jsonConfig.encodeToString(JsonObject.serializer(), JsonObject(content = mapOf()))
        }
    }

    fun toMap(json: String): Map<String, Any> {
        try {
            val jsonObject = jsonConfig.decodeFromString(JsonObject.serializer(), json)
            return toMap(jsonObject)
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.d("TruvideoSdkMedia", "Error parsing metadata ${exception.localizedMessage}")
            return emptyMap()
        }
    }

    private fun toMap(jsonObject: JsonObject): Map<String, Any> {
        fun convertValue(jsonElement: JsonElement): Any? {
            return when (jsonElement) {
                is JsonObject -> toMap(jsonElement)
                is JsonArray -> jsonElement.map { convertValue(it) }.toList()
                is JsonPrimitive -> jsonElement.content
                else -> null
            }
        }

        val result = mutableMapOf<String, Any>()

        jsonObject.entries.forEach {
            val value = convertValue(it.value)
            if (value != null) {
                result[it.key] = value
            }
        }

        return result.toMap()
    }

    private fun toJsonElement(value: Map<String, Any>): JsonObject {

        fun convertValue(value: Any?): JsonElement? {
            return when (value) {
                is String -> JsonPrimitive(value)
                is Map<*, *> -> toJsonElement(value as Map<String, Any>)
                is List<*> -> JsonArray(value.mapNotNull { convertValue(it) }.toList())
                else -> null
            }
        }

        val map = mutableMapOf<String, JsonElement>()

        value.entries.forEach {
            val entryValue = convertValue(it.value)
            if (entryValue != null) {
                map[it.key] = entryValue
            }
        }

        return JsonObject(map)
    }
}