package com.truvideo.sdk.media.model.serializers

import com.truvideo.sdk.media.model.TruvideoSdkMediaResponseType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TruvideoSdkMediaResponseTypeSerializer : KSerializer<TruvideoSdkMediaResponseType> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("type", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TruvideoSdkMediaResponseType) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): TruvideoSdkMediaResponseType {
        val string = decoder.decodeString()
        try {
            return TruvideoSdkMediaResponseType.entries.first { it.value == string }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return TruvideoSdkMediaResponseType.UNKNOWN
        }
    }
}