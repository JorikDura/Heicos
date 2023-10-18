package com.heicos.data.repository

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import org.jsoup.Jsoup
import javax.inject.Inject

class CosplayRepositoryImpl @Inject constructor() : CosplayRepository {
    override suspend fun getCosplays(page: Int, query: String): List<CosplayPreview> {
        val result = mutableListOf<CosplayPreview>()

        val url = if (query.isNotEmpty()) {
            "https://hentai-cosplays.com/search/keyword/$query/page/$page/"
        } else {
            "https://hentai-cosplays.com/search/page/$page/"
        }
        val doc = Jsoup.connect(url).get()
        val imageList = doc.select("div.image-list-item")
        for (i in 0 until imageList.size) {
            val pageUrl =
                "https://hentai-cosplays.com/" + imageList.select("div.image-list-item-image")
                    .select("a")
                    .eq(i)
                    .attr("href")

            val storyPageUrl = imageList.select("div.image-list-item-image")
                .select("a")
                .eq(i)
                .attr("href")
                .replace("image", "story")

            var image = imageList.select("div.image-list-item-image")
                .select("img")
                .eq(i)
                .attr("src")
                .replace("/p=160x200", "")

            if (!image.contains("https")) {
                image = image.replace("http", "https")
            }

            val title = doc.select("p.image-list-item-title")
                .select("a")
                .eq(i)
                .text()

            val date = doc.select("p.image-list-item-regist-date")
                .select("span")
                .eq(i)
                .text()

            result.add(
                CosplayPreview(
                    pageUrl = pageUrl,
                    storyPageUrl = storyPageUrl,
                    previewUrl = image,
                    title = title,
                    date = date
                )
            )
        }
        return result
    }

    override suspend fun getFullCosplay(url: String): List<String> {
        val result = mutableListOf<String>()
        val fullUrl = "https://hentai-cosplays.com$url"
        val doc = Jsoup.connect(fullUrl).get()

        doc.select("amp-img.auto-style").forEach { image ->
            var imageUrl = image.attr("src")

            if (!imageUrl.contains("https")) {
                imageUrl = imageUrl.replace("http", "https")
            }

            result.add(imageUrl)
        }

        return result
    }

}