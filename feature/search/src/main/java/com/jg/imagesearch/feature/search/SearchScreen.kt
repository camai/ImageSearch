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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToViewer: (ImageItem) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("검색어를 입력하세요 (예: 만화)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "검색") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "지우기")
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
                    text = "검색어를 입력하여 이미지를 찾아보세요.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        searchResults.refresh()
                        coroutineScope.launch {
                            delay(500)
                            isRefreshing = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(searchResults.itemCount) { index ->
                            searchResults[index]?.let { item ->
                                SearchImageCard(
                                    item = item,
                                    onClick = {
                                        focusManager.clearFocus()
                                        onNavigateToViewer(item)
                                    },
                                    onBookmarkToggle = { viewModel.toggleBookmark(item) }
                                )
                            }
                        }
                        
                        searchResults.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    // initial loading is handled by indicator
                                }
                                loadState.append is LoadState.Loading -> {
                                    // infinite scroll loading indicator could be placed here 
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
                    val icon = if(item.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                    val tint = if(item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(
                        imageVector = icon,
                        contentDescription = "Bookmark",
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
