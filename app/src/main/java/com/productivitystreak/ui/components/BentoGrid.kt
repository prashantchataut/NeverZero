package com.productivitystreak.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Spacing

/**
 * Bento Grid Layout
 * A reusable staggered grid for the dashboard and other screens.
 */
@Composable
fun BentoGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Spacing.md),
    verticalItemSpacing: Dp = Spacing.md,
    content: LazyStaggeredGridScope.() -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalItemSpacing = verticalItemSpacing,
        content = content
    )
}

/**
 * Helper to span full width in the grid
 */
fun LazyStaggeredGridScope.fullWidthItem(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable () -> Unit
) {
    item(
        key = key,
        contentType = contentType,
        span = StaggeredGridItemSpan.FullLine,
        content = { content() }
    )
}
