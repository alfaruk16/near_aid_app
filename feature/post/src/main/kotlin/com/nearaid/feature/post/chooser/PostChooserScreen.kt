package com.nearaid.feature.post.chooser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.TextChooserRow
import com.nearaid.core.designsystem.theme.Ink2

/**
 * Entry screen that lets the user choose between posting a Request or an Offer.
 * No ViewModel — purely presentational.
 */
@Composable
fun PostChooserScreen(
    onPickRequest: () -> Unit,
    onPickOffer: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Post", onBack = onDismiss)

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "What would you like to post?",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Ask for a hand, or share what you can spare.",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink2,
                modifier = Modifier.padding(bottom = 4.dp),
            )

            TextChooserRow(
                title = "I need help",
                subtitle = "Post a request for food, clothes, medicine…",
                onClick = onPickRequest,
                highlighted = false,
            )

            TextChooserRow(
                title = "I have something to give",
                subtitle = "Post an offer — surplus, spare, or extra",
                onClick = onPickOffer,
                highlighted = true,
            )
        }
    }
}
