package com.heicos.domain.use_case

import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class GetFullCosplayUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(url: String): List<String> {
        return repository.getFullCosplay(url)
    }

}