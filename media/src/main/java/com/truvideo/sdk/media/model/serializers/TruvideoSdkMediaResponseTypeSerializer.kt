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
        val string = when (value) {
            TruvideoSdkMediaResponseType.Video -> "VIDEO"
            TruvideoSdkMediaResponseType.Picture -> "IMAGE"
            TruvideoSdkMediaResponseType.Unknown -> ""
        }
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): TruvideoSdkMediaResponseType {
        val string = decoder.decodeString()
        return when (string) {
            "VIDEO" -> TruvideoSdkMediaResponseType.Video
            "IMAGE" -> TruvideoSdkMediaResponseType.Picture
            else -> TruvideoSdkMediaResponseType.Unknown
        }
    }
}