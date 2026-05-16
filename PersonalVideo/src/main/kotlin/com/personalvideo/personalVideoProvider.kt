package com.personalvideo

import android.content.Context
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.ErrorLoadingException
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.mapper
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newMovieLoadResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.newExtractorLink
import java.net.URI

class PersonalVideoProvider(context: Context) : MainAPI() {
    override var name = "Personal Video"
    override var mainUrl = PersonalVideoSettings.getBaseUrl(context)
    override var lang = "it"
    override val supportedTypes = setOf(TvType.Movie)
    override val hasMainPage = true
    override val hasDownloadSupport = true
    override val hasChromecastSupport = true
    override val mainPage = mainPageOf("$CATALOG_PATH" to "Catalog")

    private var catalogCache: Catalog? = null
    private var catalogCacheTimeMs = 0L

    fun updateBaseUrl(baseUrl: String) {
        val normalizedBaseUrl = PersonalVideoSettings.normalizeBaseUrl(baseUrl)
            .ifBlank { PersonalVideoSettings.DEFAULT_BASE_URL }
        if (mainUrl == normalizedBaseUrl) return

        mainUrl = normalizedBaseUrl
        catalogCache = null
        catalogCacheTimeMs = 0L
    }

    data class Catalog(
        @param:JsonProperty("catalog_name")
        val catalogName: String = "Personal Video",
        @param:JsonProperty("contents")
        val contents: List<CatalogItem> = emptyList(),
    )

    data class CatalogItem(
        @param:JsonProperty("title")
        val title: String = "",
        @param:JsonProperty("plot")
        val plot: String = "",
        @param:JsonProperty("poster")
        val poster: String = "",
        @param:JsonProperty("video_url")
        val videoUrl: String = "",
        @param:JsonProperty("category")
        val category: String = DEFAULT_CATEGORY,
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val catalog = fetchCatalog()
        val sections = catalog.contents
            .asSequence()
            .filter { it.isPlayable() }
            .groupBy { it.categoryName() }
            .map { (category, contents) ->
                HomePageList(
                    category,
                    contents.map { it.toSearchResponse() },
                    false,
                )
            }
            .toList()

        return if (sections.isEmpty()) {
            newHomePageResponse(
                name = catalog.catalogName.ifBlank { request.name },
                list = emptyList(),
                hasNext = false,
            )
        } else {
            newHomePageResponse(sections, hasNext = false)
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) {
            return fetchCatalog().contents
                .filter { it.isPlayable() }
                .map { it.toSearchResponse() }
        }

        return fetchCatalog(force = true).contents
            .asSequence()
            .filter { item ->
                item.isPlayable() &&
                    (item.title.lowercase().contains(normalizedQuery) ||
                        item.plot.lowercase().contains(normalizedQuery) ||
                        item.categoryName().lowercase().contains(normalizedQuery))
            }
            .map { it.toSearchResponse() }
            .toList()
    }

    override suspend fun load(url: String): LoadResponse {
        val item = fetchCatalog(force = true).contents.firstOrNull { it.videoUrl == url || it.streamUrl() == url }
            ?: CatalogItem(
                title = url.substringAfterLast("/").substringBefore("?").ifBlank { "Personal video" },
                plot = "Personal video served by the configured HTTP server.",
                poster = "",
                videoUrl = url,
                category = DEFAULT_CATEGORY,
            )

        return newMovieLoadResponse(
            name = item.title,
            url = item.streamUrl(),
            type = TvType.Movie,
            dataUrl = item.streamUrl(),
        ) {
            plot = item.plot.ifBlank { null }
            posterUrl = item.posterUrl()
            tags = listOf(item.categoryName())
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit,
    ): Boolean {
        if (!data.startsWith("http", ignoreCase = true)) return false

        callback(
            newExtractorLink(
                source = name,
                name = "Personal Video",
                url = data,
                type = ExtractorLinkType.VIDEO,
            ) {
                quality = Qualities.Unknown.value
            },
        )

        return true
    }

    private suspend fun fetchCatalog(force: Boolean = false): Catalog {
        val now = System.currentTimeMillis()
        val cached = catalogCache

        if (!force && cached != null && now - catalogCacheTimeMs < CACHE_MS) {
            return cached
        }

        val response = app.get("$mainUrl$CATALOG_PATH", cacheTime = 0)
        val catalog = runCatching {
            mapper.readValue(response.text, Catalog::class.java)
        }.getOrElse {
            throw ErrorLoadingException("catalog.json is invalid or unreachable")
        }

        catalogCache = catalog
        catalogCacheTimeMs = now
        return catalog
    }

    private fun CatalogItem.isPlayable(): Boolean {
        return title.isNotBlank() && videoUrl.isNotBlank()
    }

    private fun CatalogItem.categoryName(): String {
        return category.trim().ifBlank { DEFAULT_CATEGORY }
    }

    private fun CatalogItem.toSearchResponse(): SearchResponse {
        return newMovieSearchResponse(
            name = title,
            url = streamUrl(),
            type = TvType.Movie,
            fix = false,
        ) {
            posterUrl = posterUrl()
        }
    }

    private fun CatalogItem.streamUrl(): String {
        return videoUrl.toLocalUrl()
    }

    private fun CatalogItem.posterUrl(): String? {
        return poster.takeIf { it.isNotBlank() }?.toLocalUrl()
    }

    private fun String.toLocalUrl(): String {
        val trimmed = trim()
        if (trimmed.startsWith("http", ignoreCase = true)) return trimmed
        return "${mainUrl.trimEnd('/')}/${trimmed.trimStart('/').encodeLocalPath()}"
    }

    private fun String.encodeLocalPath(): String {
        return URI(null, null, "/$this", null).rawPath.removePrefix("/")
    }

    companion object {
        private const val CATALOG_PATH = "/catalog.json"
        private const val CACHE_MS = 60_000L
        private const val DEFAULT_CATEGORY = "Uncategorized"
    }
}
