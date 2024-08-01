package com.truvideo.media.app.util

import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal object TruvideoSdkSignature {

    fun generate(payload: String, secret: String): String {
        val keyBytes: ByteArray = secret.toByteArray(StandardCharsets.UTF_8)
        val messageBytes: ByteArray = payload.toByteArray(StandardCharsets.UTF_8)

        try {
            val hmacSha256 = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(keyBytes, "HmacSHA256")
            hmacSha256.init(secretKeySpec)
            val signatureBytes = hmacSha256.doFinal(messageBytes)
            return bytesToHex(signatureBytes)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val hex = StringBuilder(bytes.size * 2)
        for (i in bytes.indices) {
            val value = bytes[i].toInt() and 0xFF
            hex.append(hexChars[value shr 4])
            hex.append(hexChars[value and 0x0F])
        }
        return hex.toString()
    }
}