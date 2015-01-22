package dev.jinkim.snappollandroid.model;

import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jin on 12/7/14.
 */
public class User {

    private String fullName;

    @SerializedName("f_name")
    private String firstName;

    @SerializedName("l_name")
    private String lastName;

    @SerializedName("user_id")
    private String userId;
    private String profilePicUrl;

    /**
     * Populate User fields with the passed in Person object (Google+)
     * Reference: https://developer.android.com/reference/com/google/android/gms/plus/model/people/Person.html
     *
     * @param p Google Plus Person model
     */
    public User(Person p) {

        if (p.hasDisplayName()) {
            fullName = p.getDisplayName();
        }

        Person.Name name = p.getName();

        if (name.hasGivenName()) {
            firstName = name.getGivenName();
        }
        if (name.hasFamilyName()) {
            lastName = name.getFamilyName();
        }
        if (p.hasImage()) {
            profilePicUrl = p.getImage().getUrl();
            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            profilePicUrl = profilePicUrl.substring(0, profilePicUrl.length() - 2) + 200;
        }
    }

    /**
     * Populate User fields with the passed in GraphUser object (Facebook SDK)
     * Reference: https://developers.facebook.com/docs/reference/android/current/interface/GraphUser/
     *
     * @param fbUser Facebook GraphUser model
     */
    public User(GraphUser fbUser) {

        if (fbUser.getId() != null) {
            userId = fbUser.getId();
            fullName = fbUser.getName();
            firstName = fbUser.getFirstName();
            lastName = fbUser.getLastName();
            profilePicUrl = getFbProfilePicUrl(fbUser.getId());
        }
    }

    /* Generate FB profile picture url based on passed in user id */
    private String getFbProfilePicUrl(String id) {
        return "http://graph.facebook.com/" + id + "/picture?type=large";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
