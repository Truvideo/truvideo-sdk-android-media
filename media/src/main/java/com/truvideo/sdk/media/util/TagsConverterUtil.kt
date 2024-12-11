package com.truvideo.sdk.media.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object TagsConverterUtil {

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
        prettyPrint = true
        useArrayPolymorphism = true
        explicitNulls = false
    }

    fun toJson(value: Map<String, String>): String = jsonConfig.encodeToString(JsonObject.serializer(), toJsonElement(value))

    fun toMap(json: String): Map<String, String> {
        val jsonObject = jsonConfig.decodeFromString(JsonObject.serializer(), json)
        return toMap(jsonObject)
    }

    private fun toMap(jsonObject: JsonObject): Map<String, String> {

        fun convertValue(jsonElement: JsonElement): String? {
            return when (jsonElement) {
                is JsonPrimitive -> jsonElement.content
                else -> null
            }
        }

        val result = mutableMapOf<String, String>()

        jsonObject.entries.forEach {
            val value = convertValue(it.value)
            if (value != null) {
                result[it.key] = value
            }
        }

        return result.toMap()
    }

    private fun toJsonElement(value: Map<String, String>): JsonObject {

        fun convertValue(value: Any?): JsonElement? {
            if (value == null) return null
            return JsonPrimitive(value.toString())
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