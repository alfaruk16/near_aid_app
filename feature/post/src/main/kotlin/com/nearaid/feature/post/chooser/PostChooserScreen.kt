package com.nearaid.feature.post.chooser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.TextChooserRow
import com.nearaid.core.designsystem.component.headingSemantics
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.feature.post.R

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
        NearAidTopBar(title = stringResource(R.string.post_chooser_top_bar_title), onBack = onDismiss)

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.post_chooser_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.headingSemantics(),
            )
            Text(
                text = stringResource(R.string.post_chooser_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = NearAidTheme.colors.ink2,
                modifier = Modifier.padding(bottom = 4.dp),
            )

            TextChooserRow(
                title = stringResource(R.string.post_chooser_request_title),
                subtitle = stringResource(R.string.post_chooser_request_subtitle),
                onClick = onPickRequest,
                highlighted = false,
            )

            TextChooserRow(
                title = stringResource(R.string.post_chooser_offer_title),
                subtitle = stringResource(R.string.post_chooser_offer_subtitle),
                onClick = onPickOffer,
                highlighted = true,
            )
        }
    }
}
