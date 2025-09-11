package org.ericp.ehub.server.utils

class Utils {
    companion object {
        fun generateRandomColor(): String {
            val chars = "0123456789ABCDEF"
            return (1..6).map { chars.random() }.joinToString("", "#")
        }

        fun encodeWithKey(text: String, key: String): String {
            val keyBytes = key.toByteArray()
            val textBytes = text.toByteArray()
            val encodedBytes = ByteArray(textBytes.size)
            for (i in textBytes.indices) {
                encodedBytes[i] = (textBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
            }
            return encodedBytes.joinToString(separator = "") { "%02x".format(it) }
        }

        fun decodeWithKey(encoded: String, key: String): String {
            val keyBytes = key.toByteArray()
            val encodedBytes = ByteArray(encoded.length / 2)
            for (i in encodedBytes.indices) {
                encodedBytes[i] = encoded.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }
            val decodedBytes = ByteArray(encodedBytes.size)
            for (i in encodedBytes.indices) {
                decodedBytes[i] = (encodedBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
            }
            return String(decodedBytes)
        }
    }
}