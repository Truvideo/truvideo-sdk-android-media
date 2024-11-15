package com.truvideo.sdk.media.example;

import androidx.annotation.NonNull;

import com.truvideo.sdk.media.TruvideoSdkMedia;
import com.truvideo.sdk.media.builder.TruvideoSdkMediaFileUploadRequestBuilder;
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback;
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback;
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest;
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata;
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags;

import kotlin.Unit;
import truvideo.sdk.common.exceptions.TruvideoSdkException;


class ExampleFileUploadJava {

    private void uploadFile(String filePath) {
        final TruvideoSdkMediaFileUploadRequestBuilder builder = TruvideoSdkMedia.getInstance().FileUploadRequestBuilder(filePath);

        // --------------------------
        // TAGS
        // --------------------------

        // Option 1: use the file upload request builder directly
        builder.addTag("key", "value");
        builder.addTag("color", "red");
        builder.addTag("order-number", "123");

        // Option 2: use the tag builder
        TruvideoSdkMediaTags tags = TruvideoSdkMediaTags.builder()
                .set("key", "value")
                .set("color", "red")
                .set("order-number", "123")
                .build();
        builder.setTags(tags);

        // --------------------------
        // Metadata
        // --------------------------

        // Option 1: use the file upload request builder directly
        builder.addMetadata("key", "value");
        builder.addMetadata("list", new String[]{"value1", "value2"});
        builder.addMetadata("nested", TruvideoSdkMediaMetadata.builder()
                .set("key", "value")
                .set("list", new String[]{"value1", "value2"})
                .build()
        );

        // Option 2: use the metadata builder
        TruvideoSdkMediaMetadata metadata = TruvideoSdkMediaMetadata
                .builder()
                .set("key", "value")
                .set("list", new String[]{"value1", "value2"})
                .set("nested", TruvideoSdkMediaMetadata.builder()
                        .set("key", "value")
                        .set("list", new String[]{"value1", "value2"})
                        .build()
                )
                .build();
        builder.setMetadata(metadata);

        // Build request
        builder.build(new TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest>() {
            @Override
            public void onComplete(TruvideoSdkMediaFileUploadRequest data) {
                // File upload request created successfully

                // Send to upload
                data.upload(
                        new TruvideoSdkMediaCallback<Unit>() {

                            @Override
                            public void onComplete(Unit data) {
                                // Upload started successfully
                            }

                            @Override
                            public void onError(@NonNull TruvideoSdkException exception) {
                                // Upload started with error
                            }
                        },
                        new TruvideoSdkMediaFileUploadCallback() {
                            @Override
                            public void onError(@NonNull String id, @NonNull TruvideoSdkException ex) {
                                // Handle error uploading the file
                            }

                            @Override
                            public void onProgressChanged(@NonNull String id, float progress) {
                                // Handle uploading progress

                            }

                            @Override
                            public void onComplete(@NonNull String id, @NonNull TruvideoSdkMediaFileUploadRequest response) {
                                // File uploaded successfully
                            }
                        }
                );
            }

            @Override
            public void onError(@NonNull TruvideoSdkException exception) {
                // Handle error creating the file upload request
            }
        });
    }
}
