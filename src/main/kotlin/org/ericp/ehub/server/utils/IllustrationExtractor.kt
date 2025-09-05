package org.ericp.ehub.server.utils

import org.ericp.ehub.server.repository.ToBuyLinkRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class IllustrationExtractor(
    toBuyLinkRepository: ToBuyLinkRepository
) {
    private val cache = ConcurrentHashMap<String, String>()

    init {
        // Preload cache on startup
        println("IllustrationExtractor initialized 🚀")
        val dbValues = toBuyLinkRepository.findAllUrlAndIllustrationUrl()
        dbValues.forEach { arr ->
            if (arr.size == 2) {
                val url = arr[0] as? String
                val illustrationUrl = arr[1] as? String
                if (!url.isNullOrBlank() && !illustrationUrl.isNullOrBlank()) {
                    cache[url] = illustrationUrl
                }
            }
        }
    }

    fun extract(url: String, forceRefresh: Boolean = false): String? {
        // Check cache first
        if (!forceRefresh) cache[url]?.let { return it }

        val result = when {
            "amzn." in url -> extractAmazon(url)
            "ebay." in url -> extractEbay(url)
            "aliexpress." in url -> extractAliExpress(url)
            "leboncoin.fr" in url -> extractLeboncoin(url)
            else -> extractGeneric(url)
        }

        // Update cache and DB
        if (!result.isNullOrBlank()) {
            cache[url] = result
        }

        return result
    }

    fun extract(urls: List<String>, forceRefresh: Boolean = false): Map<String, String?> {
        return urls.associateWith { url ->
            extract(url, forceRefresh)
        }
    }

    // ---------------- Generic ----------------
    private fun extractGeneric(url: String): String? {
        val doc = fetch(url)

        val selectors = listOf(
            "meta[property=og:image]",
            "meta[name=twitter:image]",
            "link[rel=image_src]",
            "img#main-product-image",
            "img.product-image",
            "img[src*=/products/]"
        )

        for (selector in selectors) {
            val element = doc.selectFirst(selector)
            if (element != null) {
                val content = element.attr("content")
                if (content.isNotBlank()) return content
                val src = element.attr("src")
                if (src.isNotBlank()) return src
                val dataSrc = element.attr("data-src")
                if (dataSrc.isNotBlank()) return dataSrc
            }
        }

        // Fallback: largest <img>
        return doc.select("img")
            .maxByOrNull { it.attr("width").toIntOrNull() ?: 0 }
            ?.let { it.attr("src").ifBlank { it.attr("data-src") } }
    }

    // ---------------- Amazon ----------------
    private fun extractAmazon(url: String): String? {
        val doc = fetch(url)

        // OpenGraph (most reliable)
        doc.selectFirst("meta[property=og:image]")?.attr("content")?.let { if (it.isNotBlank()) return it }

        // Twitter fallback
        doc.selectFirst("meta[name=twitter:image]")?.attr("content")?.let { if (it.isNotBlank()) return it }

        // Landing image
        val landing = doc.selectFirst("#landingImage")
        landing?.attr("data-old-hires")?.let { if (it.isNotBlank()) return it }
        landing?.attr("src")?.let { if (it.isNotBlank()) return it }

        // Dynamic image JSON
        val json = landing?.attr("data-a-dynamic-image")
        if (!json.isNullOrBlank()) {
            try {
                val obj = JSONObject(json)
                // Pick the biggest image
                val best = obj.keys().asSequence().maxByOrNull { key ->
                    val dims = obj.getJSONArray(key).getJSONArray(0)
                    val w = dims.getInt(0)
                    val h = dims.getInt(1)
                    w * h
                }
                if (best != null) return best
            } catch (_: Exception) {
            }
        }

        return null
    }

    // ---------------- eBay ----------------
    private fun extractEbay(url: String): String? {
        val doc = fetch(url)

        doc.selectFirst("meta[property=og:image]")?.attr("content")?.let { if (it.isNotBlank()) return it }
        doc.selectFirst("img#icImg")?.attr("src")?.let { if (it.isNotBlank()) return it }

        return null
    }

    // ---------------- AliExpress ----------------
    private fun extractAliExpress(url: String): String? {
        val doc = fetch(url)

        doc.selectFirst("meta[property=og:image]")?.attr("content")?.let { if (it.isNotBlank()) return it }

        val img = doc.selectFirst("img[src*='ae01.alicdn.com']") ?: doc.selectFirst("img[data-src*='ae01.alicdn.com']")
        img?.attr("src")?.let { if (it.isNotBlank()) return it }
        img?.attr("data-src")?.let { if (it.isNotBlank()) return it }

        return null
    }

    // ---------------- Leboncoin ----------------
    private fun extractLeboncoin(url: String): String? {
        val doc = fetch(url)

        // OpenGraph (main image)
        doc.selectFirst("meta[property=og:image]")?.attr("content")?.let {
            if (it.isNotBlank()) return it
        }

        // Twitter fallback
        doc.selectFirst("meta[name=twitter:image]")?.attr("content")?.let {
            if (it.isNotBlank()) return it
        }

        // Sometimes images are also in <img> tags with lazy load
        val img = doc.selectFirst("img[src*='leboncoin.fr']") ?: doc.selectFirst("img[data-src*='leboncoin.fr']")
        img?.attr("src")?.let { if (it.isNotBlank()) return it }
        img?.attr("data-src")?.let { if (it.isNotBlank()) return it }

        return null
    }


    // ---------------- Utilities ----------------
    private fun fetch(url: String): Document {
        return Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
            .referrer("https://www.google.com/")
            .header("Accept-Language", "fr-FR,fr;q=0.9,en;q=0.8")
            .timeout(10_000)
            .get()
    }
}
