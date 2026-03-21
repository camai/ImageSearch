package com.jg.imagesearch.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.jg.imagesearch.core.model.SnackbarMessage
import com.jg.imagesearch.core.model.UiEffect

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToViewer: (ImageItem) -> Unit,
    onBack: () -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
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
                        SnackbarMessage.ERROR_DEFAULT -> context.getString(R.string.msg_error_default, *effect.args.toTypedArray())
                        else -> context.getString(R.string.msg_error_unknown)
                    }
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    LocalSearchScreen(
        query = query,
        searchResults = searchResults,
        snackbarHostState = snackbarHostState,
        onQueryChanged = viewModel::onQueryChanged,
        onBookmarkToggle = viewModel::toggleBookmark,
        onNavigateToViewer = onNavigateToViewer,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalSearchScreen(
    query: String,
    searchResults: LazyPagingItems<ImageItem>,
    snackbarHostState: SnackbarHostState,
    onQueryChanged: (String) -> Unit,
    onBookmarkToggle: (ImageItem) -> Unit,
    onNavigateToViewer: (ImageItem) -> Unit,
    onBack: () -> Unit
) {
    var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(query))
    }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp >= 600) 4 else 2

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.local_search_back))
                    }
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            textValue = newValue
                            onQueryChanged(newValue.text)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (query.isBlank()) {
                Text(
                    text = stringResource(id = R.string.local_search_empty_prompt),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (searchResults.loadState.refresh is LoadState.NotLoading && searchResults.itemCount == 0) {
                Text(
                    text = stringResource(id = R.string.local_search_empty_result),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
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
                }
            }
        }
    }
}

@Composable
private fun SearchImageCard(
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
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
