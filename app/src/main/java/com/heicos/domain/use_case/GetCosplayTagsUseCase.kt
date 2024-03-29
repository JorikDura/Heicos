package com.heicos.domain.use_case

import com.heicos.domain.repository.CosplayRepository
import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCosplayTagsUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(url: String): Flow<Resource<List<String>>> {
        return repository.getCosplayTags(url)
    }

}