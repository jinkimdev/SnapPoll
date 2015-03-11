package dev.jinkim.snappollandroid.model;

import com.google.android.gms.plus.model.people.Person;

/**
 * Created by Jin on 3/10/15.
 */
public class RowFriend {
    public Person person;
    public boolean selected = false;

    public RowFriend(Person person) {
        this.person = person;
    }
}
