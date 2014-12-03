package dev.jinkim.snappollandroid.model;

/**
 * Created by Jin on 11/30/14.
 *
 * Model object that is returned upon uploading an image
 * Reference: https://api.imgur.com/models/basic
 */
public class ImgurResponse {
    public ImgurResponseData data;
    public boolean success;
    public int status;
}
