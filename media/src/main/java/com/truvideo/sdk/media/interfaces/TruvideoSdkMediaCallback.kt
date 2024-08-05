package com.truvideo.sdk.media.interfaces

import truvideo.sdk.common.exception.TruvideoSdkException

interface TruvideoSdkMediaCallback<T : Any?> {

    fun onComplete(data: T)

    fun onError(exception: TruvideoSdkException)
}
