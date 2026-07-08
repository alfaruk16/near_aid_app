package com.nearaid.feature.discovery.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.feature.discovery.resources.Res
import com.nearaid.feature.discovery.resources.*
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.ListingCardView
import com.nearaid.core.designsystem.component.NearAidChip
import com.nearaid.core.designsystem.component.NearAidSegmentedTabs
import com.nearaid.core.designsystem.component.statusSemantics
import com.nearaid.core.designsystem.theme.NearAidTheme

@Composable
fun HomeScreen(
    onListingClick: (id: String) -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is HomeEffect.OpenListing -> onListingClick(effect.id)
            HomeEffect.OpenNotifications -> onOpenNotifications()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        // Location header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .semantics(mergeDescendants = true) { heading() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = NearAidTheme.colors.teal,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(
                    Res.string.home_location_radius,
                    stringResource(Res.string.home_location_placeholder),
                    state.radiusKm.toInt(),
                ),
                style = MaterialTheme.typography.titleSmall,
                color = NearAidTheme.colors.ink,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
            )
            IconButton(onClick = { viewModel.onIntent(HomeIntent.OpenNotificationsClicked) }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = stringResource(Res.string.cd_notifications),
                    tint = NearAidTheme.colors.ink2,
                )
            }
        }

        // Needs / Offers toggle
        NearAidSegmentedTabs(
            options = listOf(
                stringResource(Res.string.home_tab_needs),
                stringResource(Res.string.home_tab_offers),
            ),
            selectedIndex = state.selectedTabIndex,
            onSelect = { viewModel.onIntent(HomeIntent.SelectTab(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(10.dp))

        // Category filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                NearAidChip(
                    label = stringResource(Res.string.home_category_all),
                    selected = state.selectedCategoryKey == null,
                    onClick = { viewModel.onIntent(HomeIntent.SelectCategory(null)) },
                )
            }
            items(state.categories) { category ->
                NearAidChip(
                    label = category.nameEn,
                    selected = state.selectedCategoryKey == category.key,
                    onClick = { viewModel.onIntent(HomeIntent.SelectCategory(category.key)) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Body
        when {
            state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = NearAidTheme.colors.marigold,
                        modifier = Modifier.statusSemantics(stringResource(Res.string.loading)),
                    )
                }
            }

            state.listings.isEmpty() && !state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Filled.LocationOn,
                        title = stringResource(Res.string.home_empty_title),
                        message = stringResource(Res.string.home_empty_message, state.radiusKm.toInt()),
                    )
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.listings, key = { it.id }) { card ->
                        ListingCardView(
                            card = card,
                            onClick = { viewModel.onIntent(HomeIntent.ListingClicked(card.id)) },
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}
