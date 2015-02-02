package dev.jinkim.snappollandroid.util;

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

}
