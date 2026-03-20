package com.jg.imagesearch.feature.viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    selectedItem: ImageItem,
    onBack: () -> Unit,
    viewModel: ViewerViewModel = hiltViewModel()
) {
    LaunchedEffect(selectedItem) {
        viewModel.initialize(selectedItem)
    }

    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이미지 뷰어") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(images, key = { index, item -> "${item.link}_$index" }) { index, item ->
                    if (index == 0) {
                        // First item is interactive
                        ZoomableImageCard(
                            item = item,
                            onBookmarkToggle = { viewModel.toggleBookmark(item) }
                        )
                    } else {
                        // Others are just normal cards
                        ViewerImageCard(
                            item = item,
                            onBookmarkToggle = { viewModel.toggleBookmark(item) }
                        )
                    }
                }
            }

            if (isLoading && images.size <= 1) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ZoomableImageCard(
    item: ImageItem,
    onBookmarkToggle: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 3f)
                            if (scale > 1f) {
                                val maxOffset = (scale - 1) * 300 // roughly adjust bounding
                                offsetX = (offsetX + pan.x).coerceIn(-maxOffset, maxOffset)
                                offsetY = (offsetY + pan.y).coerceIn(-maxOffset, maxOffset)
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
            ) {
                AsyncImage(
                    model = item.link, // Used original link for better quality in viewer!
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
                
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    val icon = if(item.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                    val tint = if(item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(imageVector = icon, contentDescription = "Bookmark", tint = tint)
                }
            }
            Text(
                text = "핀치하여 확대/축소 지원",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ViewerImageCard(
    item: ImageItem,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AsyncImage(
                model = item.thumbnail, // Random list items use thumbnail exactly like search list
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )
            IconButton(onClick = onBookmarkToggle) {
                val icon = if(item.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                val tint = if(item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(imageVector = icon, contentDescription = "Bookmark", tint = tint)
            }
        }
    }
}
