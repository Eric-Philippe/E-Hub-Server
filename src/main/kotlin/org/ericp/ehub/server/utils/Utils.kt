package org.ericp.ehub.server.utils

class Utils {
    companion object {
        fun generateRandomColor(): String {
            val chars = "0123456789ABCDEF"
            return (1..6).map { chars.random() }.joinToString("", "#")
        }
    }
}