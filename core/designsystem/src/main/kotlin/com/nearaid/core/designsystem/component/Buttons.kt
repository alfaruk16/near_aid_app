package com.nearaid.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.OnMarigold
import com.nearaid.core.designsystem.theme.Rust
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal

enum class NearAidButtonVariant { Primary, Teal, Ink, Rust, Ghost }

@Composable
fun NearAidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    variant: NearAidButtonVariant = NearAidButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = when (variant) {
                    NearAidButtonVariant.Primary -> OnMarigold
                    NearAidButtonVariant.Ghost -> Ink
                    else -> Surface
                },
            )
        } else {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                leadingIcon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp)) }
                Text(text, style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
            }
        }
    }

    if (variant == NearAidButtonVariant.Ghost) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled && !loading,
            modifier = modifier.heightIn(min = 50.dp),
            shape = androidx.compose.material3.MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(1.dp, Line),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
        ) { content() }
    } else {
        val (bg, fg) = when (variant) {
            NearAidButtonVariant.Primary -> Marigold to OnMarigold
            NearAidButtonVariant.Teal -> Teal to Surface
            NearAidButtonVariant.Ink -> Ink to Surface
            NearAidButtonVariant.Rust -> Rust to Surface
            NearAidButtonVariant.Ghost -> Surface to Ink
        }
        Button(
            onClick = onClick,
            enabled = enabled && !loading,
            modifier = modifier.heightIn(min = 50.dp).padding(0.dp),
            shape = androidx.compose.material3.MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        ) { content() }
    }
}
