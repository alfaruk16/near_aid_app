package com.nearaid.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.theme.CategoryColors

fun categoryIcon(key: String?): ImageVector = when (key) {
    "food" -> Icons.Filled.Restaurant
    "clothes" -> Icons.Filled.Checkroom
    "medicine" -> Icons.Filled.Medication
    "goods" -> Icons.Filled.Inventory2
    "shelter" -> Icons.Filled.Cottage
    else -> Icons.Filled.MoreHoriz
}

@Composable
fun CategoryIconBox(
    categoryKey: String?,
    modifier: Modifier = Modifier,
    boxSize: Int = 46,
    cornerRadius: Int = 13,
) {
    val accent = CategoryColors.forKey(categoryKey)
    Box(
        modifier = modifier
            .size(boxSize.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(accent.container),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = categoryIcon(categoryKey),
            contentDescription = null,
            tint = accent.content,
            modifier = Modifier.size((boxSize * 0.48f).dp),
        )
    }
}
