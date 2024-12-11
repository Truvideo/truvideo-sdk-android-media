package com.truvideo.media.app.ui.activities.fetch_media

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truvideo.media.app.ui.theme.TruvideoSdkMediaTheme
import com.truvideo.sdk.media.TruvideoSdkMedia
import com.truvideo.sdk.media.model.TruvideoSdkMediaResponse
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class FetchMediaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruvideoSdkMediaTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content() {
        val coroutineScope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }
        var page by remember { mutableIntStateOf(0) }
        var items by remember { mutableStateOf<ImmutableList<TruvideoSdkMediaResponse>>(persistentListOf()) }
        var loadingMore by remember { mutableStateOf(false) }
        var lastPage by remember { mutableStateOf(false) }
        var id by remember { mutableStateOf("") }
        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }

        fun fetch(reset: Boolean = false) {
            if (loading || loadingMore) return
            coroutineScope.launch {
                if (reset) {
                    items = persistentListOf()
                    lastPage = false
                    page = 0
                }

                if (items.isEmpty()) {
                    loading = true
                } else {
                    loadingMore = true
                }

                try {
                    if (id.trim().isNotEmpty()) {
                        val data = TruvideoSdkMedia.searchById(id)
                        items = if (data != null) {
                            persistentListOf(data)
                        } else {
                            persistentListOf()
                        }
                        lastPage = true
                        page = 0
                    } else {
                        val tags = if (key.trim().isNotEmpty() && value.trim().isNotEmpty()) {
                            mapOf(key to value)
                        } else {
                            mapOf()
                        }

                        val data = TruvideoSdkMedia.search(
                            pageNumber = page,
                            tags = TruvideoSdkMediaTags(tags),
                            pageSize = 10
                        )

                        items = (items + data.data.toPersistentList()).toPersistentList()
                        lastPage = data.last
                        Log.d("TruvideoSdkMedia", "Fetched ${data.data.size} items. Last page ${data.last}")
                        page++
                    }
                } catch (exception: Exception) {
                    Log.d("TruvideoSdkMedia", "Error fetching media ${exception.localizedMessage}")
                    exception.printStackTrace()
                } finally {
                    loading = false
                    loadingMore = false
                }
            }
        }

        LaunchedEffect(key1 = Unit) { fetch() }

        Scaffold { internalPadding ->
            Column(
                modifier = Modifier
                    .padding(internalPadding)
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text("Fetch media") },
                    navigationIcon = {
                        IconButton(
                            onClick = { finish() }
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "")
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = !loading,
                            onClick = { fetch(reset = true) }
                        ) {
                            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "")
                        }
                    }
                )

                val precompiledId = "effc5a2f-72ce-4ab2-8329-bc034211747d"

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = id,
                            placeholder = { Text("Id") },
                            onValueChange = { id = it },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { id = precompiledId }
                        ) {
                            Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste ID")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = key,
                            placeholder = { Text("Key") },
                            onValueChange = { key = it },
                            modifier = Modifier.weight(1f)
                        )

                        TextField(
                            value = value,
                            placeholder = { Text("Value") },
                            onValueChange = { value = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(targetState = loading, label = "") { loadingTarget ->
                        Box(Modifier.fillMaxSize()) {
                            if (loadingTarget) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(items, key = { it.id }) {
                                        MediaResponseListItem(model = it)
                                    }

                                    if (!lastPage) {
                                        item(key = "load-more") {
                                            Box(
                                                modifier = Modifier
                                                    .clickable(enabled = !loadingMore) { fetch() }
                                                    .padding(16.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                if (loadingMore) {
                                                    Text("Loading...")
                                                } else {
                                                    Text("Load more")
                                                }
                                            }
                                        }
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
    @Preview(showBackground = true)
    private fun Test() {
        TruvideoSdkMediaTheme {
            Content()
        }
    }
}