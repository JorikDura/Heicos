package com.heicos.data.repository

import com.heicos.BuildConfig
import com.heicos.data.database.CosplaysDataBase
import com.heicos.data.mapper.toCosplayPreview
import com.heicos.data.mapper.toCosplayPreviewEntity
import com.heicos.data.mapper.toSearchQuery
import com.heicos.data.mapper.toSearchQueryEntity
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.repository.CosplayRepository
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import com.heicos.utils.time.convertTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import javax.inject.Inject

class CosplayRepositoryImpl @Inject constructor(
    private val dataBase: CosplaysDataBase
) : CosplayRepository {
    private var lastPage: Int? = null
    override suspend fun getCosplays(
        page: Int,
        cosplayType: CosplayType,
        showDownloaded: Boolean
    ): Flow<Resource<List<CosplayPreview>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            when (cosplayType) {
                CosplayType.New -> {
                    val url = "${BuildConfig.baseUrl}/search/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url, showDownloaded)
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
                    val url = "${BuildConfig.baseUrl}/ranking/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url, showDownloaded)
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
                    val url = "${BuildConfig.baseUrl}/recently/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url, showDownloaded)
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
                        "${BuildConfig.baseUrl}/search/keyword/${cosplayType.query}/page/$page/"
                    val result = try {
                        getCosplaysFromUrl(url, showDownloaded)
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

                CosplayType.RecentlyViewed -> {
                    val offset = if (page > 1) (page - 1) * 20 else 0

                    val databaseCosplays = dataBase.cosplayDao.getRecentlyViewedCosplays(offset)

                    val result = databaseCosplays.map { cosplay ->
                        with(cosplay) {
                            CosplayPreview(
                                pageUrl = url,
                                storyPageUrl = storyPageUrl,
                                previewUrl = previewUrl,
                                title = name,
                                isViewed = true,
                                isDownloaded = downloadedAt != null
                            )
                        }
                    }

                    emit(Resource.Success(data = result))
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
                val fullUrl = "${BuildConfig.baseUrl}$url"
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

    override suspend fun getSearchQueries(): Flow<List<SearchQuery>> {
        return dataBase.searchDao.getSearchQueries().transform { list ->
            emit(list.map { searchQueryEntity ->
                searchQueryEntity.toSearchQuery()
            })
        }
    }

    override suspend fun upsertSearchQuery(searchItem: SearchQuery) {
        dataBase.searchDao.upsertSearchQuery(searchItem = searchItem.toSearchQueryEntity())
    }

    override suspend fun deleteSearchQueryById(searchItem: SearchQuery) {
        dataBase.searchDao.deleteById(searchItem = searchItem.toSearchQueryEntity())
    }

    override suspend fun deleteAllSearchQueries() {
        dataBase.searchDao.deleteAllSearchQueries()
    }

    override suspend fun findCosplayPreview(url: String): CosplayPreview? {
        val cosplay = dataBase.cosplayDao.findCosplay(url)

        return cosplay?.toCosplayPreview()
    }

    override suspend fun insertCosplayPreview(
        cosplay: CosplayPreview,
        time: Long?
    ): Long {
        val cosplayEntity = cosplay.toCosplayPreviewEntity(time, false)

        return dataBase.cosplayDao.insertCosplayPreview(cosplayEntity)
    }

    override suspend fun updateCosplayPreview(
        cosplay: CosplayPreview,
        time: Long?,
        isDownloaded: Boolean
    ) {
        val cosplayEntity = cosplay.toCosplayPreviewEntity(time, isDownloaded)

        return dataBase.cosplayDao.updateCosplayPreview(cosplayEntity)
    }

    private suspend fun getCosplaysFromUrl(
        url: String,
        showDownloaded: Boolean
    ): List<CosplayPreview> {
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
                BuildConfig.baseUrl + imageList.select("div.image-list-item-image")
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
                    date = date,
                )
            )
        }

        val cosplaysDataBase = dataBase.cosplayDao.getCosplayPreviews(result.map { it.pageUrl })

        cosplaysDataBase.forEach { databaseItem ->
            val index = result.indexOfFirst {
                it.pageUrl == databaseItem.url
            }

            if (!showDownloaded && databaseItem.downloadedAt != null) {
                result.removeAt(index)
            } else {
                with(result[index]) {
                    id = databaseItem.id
                    isDownloaded = databaseItem.downloadedAt != null
                    downloadedAt = databaseItem.downloadedAt
                    downloadTime = databaseItem.downloadedAt?.let { convertTime(it) }
                    isViewed = true
                }
            }
        }

        return result
    }

    companion object {
        private const val ERROR_MESSAGE = "Something bad happened"
    }
}