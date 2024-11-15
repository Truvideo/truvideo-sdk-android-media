import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.util.MetadataConverterUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object MetadataSerializer : KSerializer<TruvideoSdkMediaMetadata> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Map", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TruvideoSdkMediaMetadata) {
        val json = MetadataConverterUtil.toJson(value.map)
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): TruvideoSdkMediaMetadata {
        val json = decoder.decodeString()
        return TruvideoSdkMediaMetadata(
            map = MetadataConverterUtil.toMap(json)
        )
    }
}