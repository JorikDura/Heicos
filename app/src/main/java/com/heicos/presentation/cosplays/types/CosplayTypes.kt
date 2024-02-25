package com.heicos.presentation.cosplays.types

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.heicos.R
import com.heicos.domain.util.CosplayType

enum class CosplayTypes(
    @StringRes val title: Int,
    val icon: ImageVector,
    val cosplayType: CosplayType
) {
    New(
        title = R.string.navigation_item_new,
        icon = Icons.Outlined.Search,
        cosplayType = CosplayType.New
    ),
    Ranking(
        title = R.string.navigation_item_ranking,
        icon = Icons.Outlined.Star,
        cosplayType = CosplayType.Ranking
    ),
    Recently(
        title = R.string.navigation_item_recently,
        icon = Icons.Outlined.Refresh,
        cosplayType = CosplayType.Recently
    )
}