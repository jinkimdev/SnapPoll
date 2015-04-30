package dev.jinkim.snappollandroid.event;

import android.net.Uri;

/**
 * Created by Jin on 4/30/15.
 */
public class ImagePickedFromGalleryEvent {
    public Uri selectedImageUri;

    public ImagePickedFromGalleryEvent(Uri selectedImageUri) {
        this.selectedImageUri = selectedImageUri;
    }
}
