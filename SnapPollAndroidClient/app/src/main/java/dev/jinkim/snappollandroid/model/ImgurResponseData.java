package dev.jinkim.snappollandroid.model;

/**
 * Created by Jin on 11/30/14.
 *
 * Model object - Data field that is returned upon uploading an image
 * Reference: https://api.imgur.com/models/basic
 */
public class ImgurResponseData {
    public String id;
    public String title;
    public String description;
    public String type;
    public String deletehash;
    public String name;
    public String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeletehash() {
        return deletehash;
    }

    public void setDeletehash(String deletehash) {
        this.deletehash = deletehash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
