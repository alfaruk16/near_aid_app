package com.nearaid.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldSoft
import com.nearaid.core.designsystem.theme.TealSoft

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Brush.linearGradient(listOf(MarigoldSoft, TealSoft))),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(Marigold))
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Real help, right around you",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                "Post what you need or browse nearby requests. When you match, you coordinate the handoff safely in-app.",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 14.dp),
            )
            NearAidButton(text = "Get started", onClick = onGetStarted)
        }
    }
}
