package com.jg.imagesearch.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.jg.imagesearch.core.model.AppColors
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.SnackbarMessage
import com.jg.imagesearch.core.model.UiEffect


@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToViewer: (ImageItem) -> Unit,
    onNavigateToLocalSearch: () -> Unit
) {
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.ShowSnackbar -> {
                    val msg = when (effect.message) {
                        SnackbarMessage.BOOKMARK_ADDED -> context.getString(R.string.msg_bookmark_added)
                        SnackbarMessage.BOOKMARK_REMOVED -> context.getString(R.string.msg_bookmark_removed)
                        SnackbarMessage.BOOKMARKS_ADDED -> context.getString(R.string.msg_bookmarks_added, *effect.args.toTypedArray())
                        SnackbarMessage.ERROR_DEFAULT -> context.getString(R.string.msg_error_default, *effect.args.toTypedArray())
                        else -> context.getString(R.string.msg_error_unknown)
                    }
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    MainScreen(
        searchResults = searchResults,
        snackbarHostState = snackbarHostState,
        onBookmarkToggle = viewModel::toggleBookmark,
        onBookmarkAll = viewModel::bookmarkAll,
        onNavigateToViewer = onNavigateToViewer,
        onNavigateToLocalSearch = onNavigateToLocalSearch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    searchResults: LazyPagingItems<ImageItem>,
    snackbarHostState: SnackbarHostState,
    onBookmarkToggle: (ImageItem) -> Unit,
    onBookmarkAll: (List<ImageItem>) -> Unit,
    onNavigateToViewer: (ImageItem) -> Unit,
    onNavigateToLocalSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp >= 600) 4 else 2

    var isSelectionMode by rememberSaveable { mutableStateOf(false) }
    var selectedItems by rememberSaveable { mutableStateOf(emptyList<ImageItem>()) }

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
            TopAppBar(
                title = {
                    Text(
                        if (isSelectionMode) stringResource(R.string.selected_count_format, selectedItems.size)
                        else stringResource(id = R.string.nav_main)
                    )
                },
                navigationIcon = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedItems = emptyList()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_cancel_selection))
                        }
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(
                            onClick = {
                                onBookmarkAll(selectedItems)
                                isSelectionMode = false
                                selectedItems = emptyList()
                            },
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = stringResource(R.string.action_bookmark_selected))
                        }
                    } else {
                        IconButton(onClick = onNavigateToLocalSearch) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = stringResource(id = R.string.nav_search))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // derivedStateOf: loadState가 변경될 때만 재계산 → 불필요한 리컴포지션 차단
            val isRefreshing by remember {
                derivedStateOf { searchResults.loadState.refresh is LoadState.Loading }
            }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isSelectionMode = false
                    selectedItems = emptyList()
                    searchResults.refresh()
                },
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
                                val isSelected = selectedItems.any { it.link == item.link }
                                MainImageCard(
                                    item = item,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = isSelected,
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (isSelectionMode) {
                                            selectedItems = if (isSelected) {
                                                selectedItems.filter { it.link != item.link }
                                            } else {
                                                selectedItems + item
                                            }
                                            if (selectedItems.isEmpty()) isSelectionMode = false
                                        } else {
                                            onNavigateToViewer(item)
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionMode) {
                                            isSelectionMode = true
                                            selectedItems = listOf(item)
                                        }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainImageCard(
    item: ImageItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
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
                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(if (isSelected) AppColors.SelectionOverlay else Color.Transparent)
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
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
