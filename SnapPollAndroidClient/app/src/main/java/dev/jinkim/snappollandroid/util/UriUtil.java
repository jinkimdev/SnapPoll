package dev.jinkim.snappollandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jin on 2/2/15.
 */
public class UriUtil {
    public UriUtil() {

    }

    /**
     * https://api.imgur.com/models/image
     *
     * @param imgurUrl
     * @param thumbnailType
     * @return
     */
    public String convertImgurThumbnail(String imgurUrl, char thumbnailType) {

        if (imgurUrl.contains(".")) {
            // index of '.' in the imgur url
            int index = imgurUrl.lastIndexOf(".");
            String urlBeforeExtension = imgurUrl.substring(0, index);
            String extension = imgurUrl.substring(index);

            if (extension.equals(".gif")) {
                return imgurUrl;
            }
            String out = urlBeforeExtension + thumbnailType + extension;

            return out;
        }
        return imgurUrl;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
