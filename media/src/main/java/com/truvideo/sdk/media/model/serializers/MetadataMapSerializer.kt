import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.util.MetadataConverterUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object MetadataMapSerializer : KSerializer<Map<String, Any>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Map", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        val json = MetadataConverterUtil.toJson(value)
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any> {
        val json = decoder.decodeString()
        return MetadataConverterUtil.toMap(json)
    }
}