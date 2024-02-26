package com.heicos.domain.use_case

import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class GetCosplaysLastPageUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(): Int {
        return repository.getCosplayLastPage()
    }

}