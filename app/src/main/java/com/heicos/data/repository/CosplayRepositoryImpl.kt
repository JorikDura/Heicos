package com.heicos.data.repository

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import javax.inject.Inject

class CosplayRepositoryImpl @Inject constructor() : CosplayRepository {
    private var lastPage: Int? = null
    override suspend fun getCosplays(
        page: Int,
        cosplayType: CosplayType
    ): Flow<Resource<List<CosplayPreview>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            when (cosplayType) {
                CosplayType.New -> {
                    val url = "$BASE_URL/search/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url)
                    } catch (e: HttpStatusException) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    } catch (e: Exception) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    }
                    result?.let {
                        emit(Resource.Success(data = result))
                    }
                }

                CosplayType.Ranking -> {
                    val url = "$BASE_URL/ranking/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url)
                    } catch (e: HttpStatusException) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    } catch (e: Exception) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    }
                    result?.let {
                        emit(Resource.Success(data = result))
                    }
                }

                CosplayType.Recently -> {
                    val url = "$BASE_URL/recently/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url)
                    } catch (e: HttpStatusException) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    } catch (e: Exception) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    }
                    result?.let {
                        emit(Resource.Success(data = result))
                    }
                }

                is CosplayType.Search -> {
                    val url =
                        "$BASE_URL/search/keyword/${cosplayType.query}/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url)
                    } catch (e: HttpStatusException) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    } catch (e: Exception) {
                        emit(
                            Resource.Error(
                                message = e.message ?: ERROR_MESSAGE,
                                data = emptyList()
                            )
                        )
                        null
                    }
                    result?.let {
                        emit(Resource.Success(data = result))
                    }
                }
            }
            emit(Resource.Loading(isLoading = false))
        }
    }

    override suspend fun getFullCosplay(url: String): Flow<Resource<List<String>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            try {
                val result = mutableListOf<String>()
                val fullUrl = "$BASE_URL$url"
                val doc = Jsoup.connect(fullUrl).get()

                doc.select("amp-img.auto-style").forEach { image ->
                    var imageUrl = image.attr("src")

                    if (!imageUrl.contains("https")) {
                        imageUrl = imageUrl.replace("http", "https")
                    }

                    result.add(imageUrl)
                }

                emit(Resource.Success(data = result))
            } catch (e: HttpStatusException) {
                emit(Resource.Error(message = e.message ?: ERROR_MESSAGE, data = emptyList()))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.message ?: ERROR_MESSAGE, data = emptyList()))
            }
            emit(Resource.Loading(isLoading = false))
        }
    }

    override suspend fun getCosplayTags(url: String): Flow<Resource<List<String>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            try {
                val result = mutableListOf<String>()
                val doc = Jsoup.connect(url).get()

                val main = doc.select("div#main_contents")
                val tags = main.select("p#detail_tag").select("span").select("a")

                tags.forEach { tag ->
                    val text = tag.text()
                    if (!result.contains(text))
                        result.add(text)
                }

                emit(Resource.Success(data = result))
            } catch (e: HttpStatusException) {
                emit(Resource.Error(message = e.message ?: ERROR_MESSAGE, data = emptyList()))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.message ?: ERROR_MESSAGE, data = emptyList()))
            }
            emit(Resource.Loading(isLoading = false))
        }
    }

    override suspend fun getCosplayLastPage(): Int {
        return lastPage ?: 0
    }

    private fun getCosplaysFromUrl(url: String): List<CosplayPreview> {
        val result = mutableListOf<CosplayPreview>()
        val doc = Jsoup.connect(url).get()

        if (lastPage == null) {
            val lastUrlPage =
                doc.select("div#outline").select("div#center_left").select("div#center")
                    .select("div.wp-pagenavi").select("a.last").attr("href")
            val lastPageResult = lastUrlPage.substringAfter("page/")
            lastPage = lastPageResult.replace("/", "").toInt()
        }

        val imageList = doc.select("div.image-list-item")
        for (i in 0 until imageList.size) {
            val pageUrl =
                BASE_URL + imageList.select("div.image-list-item-image")
                    .select("a")
                    .eq(i)
                    .attr("href")

            val storyPageUrl = imageList.select("div.image-list-item-image")
                .select("a")
                .eq(i)
                .attr("href")
                .replaceFirst("image", "story")

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

    companion object {
        private const val ERROR_MESSAGE = "Something bad happened"
        private const val BASE_URL = "https://hentai-cosplay-xxx.com"
    }
}