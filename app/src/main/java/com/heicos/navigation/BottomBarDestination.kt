package com.heicos.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.heicos.R
import com.heicos.presentation.destinations.NewCosplaysScreenDestination
import com.heicos.presentation.destinations.RankingCosplaysScreenDestination
import com.heicos.presentation.destinations.RecentlyCosplaysScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    New(
        NewCosplaysScreenDestination,
        Icons.Outlined.Search,
        R.string.navigation_item_new
    ),
    Recently(
        RecentlyCosplaysScreenDestination,
        Icons.Outlined.Refresh,
        R.string.navigation_item_recently
    ),
    Ranking(
        RankingCosplaysScreenDestination,
        Icons.Outlined.Star,
        R.string.navigation_item_ranking
    )
}