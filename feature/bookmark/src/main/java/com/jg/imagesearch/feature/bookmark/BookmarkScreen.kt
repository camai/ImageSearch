package com.jg.imagesearch.feature.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jg.imagesearch.core.model.AppColors
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.UiEffect


@Composable
fun BookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onNavigateToViewer: (ImageItem) -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    BookmarkScreen(
        bookmarks = bookmarks,
        snackbarHostState = snackbarHostState,
        onRemoveBookmarks = viewModel::removeBookmarks,
        onNavigateToViewer = onNavigateToViewer
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkScreen(
    bookmarks: List<ImageItem>,
    snackbarHostState: SnackbarHostState,
    onRemoveBookmarks: (List<ImageItem>) -> Unit,
    onNavigateToViewer: (ImageItem) -> Unit
) {
    var isSelectionMode by rememberSaveable { mutableStateOf(false) }
    var selectedLinks by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp >= 600) 4 else 2

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isSelectionMode) stringResource(id = R.string.selected_items_count, selectedLinks.size)
                        else stringResource(id = R.string.my_bookmarks)
                    )
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            val itemsToRemove = bookmarks.filter { selectedLinks.contains(it.link) }
                            onRemoveBookmarks(itemsToRemove)
                            isSelectionMode = false
                            selectedLinks = emptyList()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.action_delete))
                        }
                    } else if (bookmarks.isNotEmpty()) {
                        TextButton(onClick = { isSelectionMode = true }) {
                            Text(stringResource(id = R.string.action_edit))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.empty_bookmarks), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(bookmarks, key = { it.link }) { item ->
                    val isSelected = selectedLinks.contains(item.link)
                    BookmarkCard(
                        item = item,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                selectedLinks = if (isSelected) {
                                    selectedLinks - item.link
                                } else {
                                    selectedLinks + item.link
                                }
                                if (selectedLinks.isEmpty()) {
                                    isSelectionMode = false
                                }
                            } else {
                                onNavigateToViewer(item)
                            }
                        },
                        onLongClick = {
                            if (!isSelectionMode) {
                                isSelectionMode = true
                                selectedLinks = selectedLinks + item.link
                            }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookmarkCard(
    item: ImageItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
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
                        .fillMaxSize()
                        .matchParentSize()
                        .background(if (isSelected) AppColors.SelectionOverlay else Color.Transparent)
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(id = R.string.selected_content_desc),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
