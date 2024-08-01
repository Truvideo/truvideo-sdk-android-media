import com.truvideo.sdk.media.util.TagsConverterUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object TagsMapSerializer : KSerializer<Map<String, String>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("tags-map", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Map<String, String>) {
        val json = TagsConverterUtil.toJson(value)
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): Map<String, String> {
        val json = decoder.decodeString()
        return TagsConverterUtil.toMap(json)
    }
}