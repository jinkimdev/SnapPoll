package dev.jinkim.snappollandroid.ui.invitefriends;

import com.google.android.gms.plus.model.people.Person;

/**
 * Created by Jin on 3/10/15.
 *
 * Object for the InviteFriends list view for displaying Google+ Friend (Person) object
 */
public class RowFriend {
    public Person person;
    public boolean selected = false;

    public RowFriend(Person person) {
        this.person = person;
    }
}
