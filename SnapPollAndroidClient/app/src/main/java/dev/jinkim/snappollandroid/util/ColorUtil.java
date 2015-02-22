package dev.jinkim.snappollandroid.util;

/**
 * Created by Jin on 2/22/15.
 */
public class ColorUtil {
    public static String convertToHex(int colorInt) {
        return String.format("#%06X", (0xFFFFFF & colorInt));
    }
}
