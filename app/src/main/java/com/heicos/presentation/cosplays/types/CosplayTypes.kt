package com.heicos.presentation.cosplays.types

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.icons.twotone.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.heicos.R
import com.heicos.domain.util.CosplayType

enum class CosplayTypes(
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
    val cosplayType: CosplayType
) {
    New(
        title = R.string.navigation_item_new,
        selectedIcon = Icons.TwoTone.Home,
        icon = Icons.Outlined.Home,
        cosplayType = CosplayType.New
    ),
    Ranking(
        title = R.string.navigation_item_ranking,
        selectedIcon = Icons.TwoTone.Star,
        icon = Icons.Outlined.Star,
        cosplayType = CosplayType.Ranking
    ),
    Recently(
        title = R.string.navigation_item_recently,
        selectedIcon = Icons.TwoTone.Refresh,
        icon = Icons.Outlined.Refresh,
        cosplayType = CosplayType.Recently
    ),
    NewVideo(
        title = R.string.navigation_item_new_video,
        selectedIcon = Icons.TwoTone.ThumbUp,
        icon = Icons.Outlined.ThumbUp,
        cosplayType = CosplayType.NewVideo
    ),
    RankingVideo(
        title = R.string.navigation_item_ranking_video,
        selectedIcon = Icons.TwoTone.Favorite,
        icon = Icons.Outlined.Favorite,
        cosplayType = CosplayType.RankingVideo
    ),
    NewAsian(
        title = R.string.navigation_item_new,
        selectedIcon = Icons.TwoTone.Home,
        icon = Icons.Outlined.Home,
        cosplayType = CosplayType.NewAsian
    ),
    RankingAsian(
        title = R.string.navigation_item_ranking,
        selectedIcon = Icons.TwoTone.Star,
        icon = Icons.Outlined.Star,
        cosplayType = CosplayType.RankingAsian
    ),
    RecentlyAsian(
        title = R.string.navigation_item_recently,
        selectedIcon = Icons.TwoTone.Refresh,
        icon = Icons.Outlined.Refresh,
        cosplayType = CosplayType.RecentlyAsian
    ),
    RecentlyViewed(
        title = R.string.navigation_item_recently_viewed,
        selectedIcon = Icons.TwoTone.Info,
        icon = Icons.Outlined.Info,
        cosplayType = CosplayType.RecentlyViewed
    ),
}