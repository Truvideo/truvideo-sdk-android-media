package com.truvideo.sdk.media.usecases

import android.content.Context
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class GetMediaDurationUseCase(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(
        path: String
    ): Long {
        return suspendCancellableCoroutine { cont ->
            scope.launch {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(path)
                    val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val duration = durationString?.toLong() ?: 0L
                    cont.resumeWith(Result.success(duration))
                } catch (e: Exception) {
                    e.printStackTrace()
                    cont.resumeWith(Result.failure(e))
                } finally {
                    retriever.release()
                }
            }
        }
    }
}