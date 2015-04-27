package dev.jinkim.snappollandroid.ui.adapter;

/**
 * Created by Jin on 4/20/15.
 *
 * Object for row item in navigation drawer
 */
public class DrawerItem {
    private int iconResId;
    private String title;

    public DrawerItem(int iconResId, String title) {
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
