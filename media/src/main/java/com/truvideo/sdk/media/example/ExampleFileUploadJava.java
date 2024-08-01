package com.truvideo.sdk.media.example;

import androidx.annotation.NonNull;

import com.truvideo.sdk.media.TruvideoSdkMedia;
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder;
import com.truvideo.sdk.media.exception.TruvideoSdkMediaException;
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback;
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback;
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest;
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata;
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadataBuilder;
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags;


class ExampleFileUploadJava {

    private void uploadFile(String filePath) {
        final TruvideoSdkMediaFileUploadRequestBuilder builder = TruvideoSdkMedia.getInstance().FileUploadRequestBuilder(filePath);

        // Tags
        builder.addTag("key", "value");
        builder.addTag("color", "red");
        builder.addTag("order-number", "123");

        // Metadata
        builder.addMetadata("key", "value");
        builder.addMetadata("list", new String[]{"value1", "value2"});
        builder.addMetadata("nested", new TruvideoSdkMediaMetadataBuilder()
                .set("key", "value")
                .set("list", new String[]{"value1", "value2"})
                .build()
        );

        // Build request
        builder.build(new TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest>() {
            @Override
            public void onComplete(TruvideoSdkMediaFileUploadRequest data) {
                // Request ready

                // Send to upload
//                data.upload(new TruvideoSdkMediaFileUploadCallback() {
//                    @Override
//                    public void onComplete(@NonNull String id, @NonNull TruvideoSdkMediaFileUploadRequest response) {
//                        // Handle result
//                        String remoteId = response.getRemoteId();
//                        String remoteUrl = response.getRemoteUrl();
//                        String transcriptionUrl = response.getTranscriptionUrl();
//                        TruvideoSdkMediaTags tags = response.getTags();
//                        TruvideoSdkMediaMetadata metadata = response.getMetadata();
//                    }
//
//                    @Override
//                    public void onProgressChanged(@NonNull String id, float progress) {
//                        // Handle progress changed
//                    }
//
//                    @Override
//                    public void onError(@NonNull String id, @NonNull TruvideoSdkMediaException ex) {
//                        // Handle error uploading the file
//                    }
//                });
            }

            @Override
            public void onError(@NonNull TruvideoSdkMediaException exception) {
                // Handle error creating the request
            }
        });
    }
}
