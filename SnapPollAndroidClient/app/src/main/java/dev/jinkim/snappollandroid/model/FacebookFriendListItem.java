package dev.jinkim.snappollandroid.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Jin on 3/9/15.
 */
public class FacebookFriendListItem {
    private Drawable pic;
    private String name;
    private int id;

    private int requestCode;

    public FacebookFriendListItem() {
    }

    public FacebookFriendListItem(Drawable pic, String name, int id, int requestCode) {
        this.pic = pic;
        this.name = name;
        this.id = id;
        this.requestCode = requestCode;
    }

    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
