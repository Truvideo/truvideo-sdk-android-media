import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import com.truvideo.sdk.media.util.TagsConverterUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject

internal object TagsSerializer : KSerializer<TruvideoSdkMediaTags> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TruvideoSdkMediaTags") {
        element<Map<String, String>>("map")
    }

    override fun serialize(encoder: Encoder, value: TruvideoSdkMediaTags) {
        val json = TagsConverterUtil.toJson(value.map)
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): TruvideoSdkMediaTags {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("This class can be loaded only by Json")
        val jsonElement = jsonDecoder.decodeJsonElement().jsonObject
        val result = TruvideoSdkMediaTags(
            map = TagsConverterUtil.toMap(jsonElement.toString())
        )
        return result
    }
}