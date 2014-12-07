package dev.jinkim.snappollandroid.model;

import com.google.android.gms.plus.model.people.Person;

/**
 * Created by Jin on 12/7/14.
 */
public class User {
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;

    /**
     * Populate User fields with the passed in Person object
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
            photoUrl = p.getImage().getUrl();
            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            photoUrl = photoUrl.substring(0, photoUrl.length() - 2) + 200;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
