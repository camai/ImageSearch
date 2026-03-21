package com.jg.imagesearch.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.UiEffect


@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToViewer: (ImageItem) -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SearchScreen(
        query = query,
        searchResults = searchResults,
        snackbarHostState = snackbarHostState,
        onQueryChanged = viewModel::onQueryChanged,
        onBookmarkToggle = viewModel::toggleBookmark,
        onNavigateToViewer = onNavigateToViewer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    query: String,
    searchResults: LazyPagingItems<ImageItem>,
    snackbarHostState: SnackbarHostState,
    onQueryChanged: (String) -> Unit,
    onBookmarkToggle: (ImageItem) -> Unit,
    onNavigateToViewer: (ImageItem) -> Unit
) {
    var textValue by remember { mutableStateOf(TextFieldValue(query)) }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp >= 600) 4 else 2

    LaunchedEffect(searchResults.loadState.refresh) {
        val refreshState = searchResults.loadState.refresh
        if (refreshState is LoadState.Error) {
            val result = snackbarHostState.showSnackbar(
                message = refreshState.error.localizedMessage ?: "Unknown Error",
                actionLabel = "Retry",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                searchResults.retry()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        textValue = newValue
                        onQueryChanged(newValue.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text(stringResource(id = R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.search_desc)) },
                    trailingIcon = {
                        if (textValue.text.isNotEmpty()) {
                            IconButton(onClick = {
                                textValue = TextFieldValue("")
                                onQueryChanged("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear_desc))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (query.isBlank()) {
                Text(
                    text = stringResource(id = R.string.empty_search_prompt),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val isRefreshing = searchResults.loadState.refresh is LoadState.Loading
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { searchResults.refresh() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        if (searchResults.loadState.refresh is LoadState.Loading && searchResults.itemCount == 0) {
                            items(10) {
                                    ShimmerBox(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                    )
                            }
                        } else {
                            items(
                                count = searchResults.itemCount,
                                key = searchResults.itemKey { it.link },
                                contentType = searchResults.itemContentType { "image" }
                            ) { index ->
                                searchResults[index]?.let { item ->
                                    SearchImageCard(
                                        item = item,
                                        onClick = {
                                            focusManager.clearFocus()
                                            onNavigateToViewer(item)
                                        },
                                        onBookmarkToggle = { onBookmarkToggle(item) }
                                    )
                                }
                            }

                            if (searchResults.loadState.append is LoadState.Loading) {
                                items(columns) {
                                    ShimmerBox(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// Sub-Component — UiState(ImageItem) 직접 전달
// ──────────────────────────────────────────────
@Composable
fun SearchImageCard(
    item: ImageItem,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = onBookmarkToggle) {
                    val icon = if (item.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                    val tint = if (item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = R.string.bookmark_desc),
                        tint = tint
                    )
                }
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
