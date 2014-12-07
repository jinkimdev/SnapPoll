package dev.jinkim.snappollandroid.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.activity.LoginActivity;

/**
 * Created by Jin on 12/7/14.
 */
public class SessionManager {

    private final String TAG = "SessionManager ####";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context mContext;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SnapPollPref";

    /* All Shared Preferences Keys */

    private static final String SESSION_LOGGED_IN = "loggedIn";
    // store User model object
    public static final String SESSION_USER = "user";
    public static final String SESSION_USER_NAME = "userName";
    public static final String SESSION_USER_EMAIL = "userEmail";
    public static final String SESSION_USER_PHOTO_URL = "userPhotoUrl";

    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String photoUrl) {
        // Storing login value as TRUE
        editor.putBoolean(SESSION_LOGGED_IN, true);
        editor.putString(SESSION_USER_NAME, name);
        editor.putString(SESSION_USER_EMAIL, email);
        editor.putString(SESSION_USER_PHOTO_URL, photoUrl);

        // commit changes
        editor.commit();
    }

    /**
     * Create login session with passed in User model
     */
    public void createLoginSession(User user) {
        // Storing login value as TRUE
        editor.putBoolean(SESSION_LOGGED_IN, true);

        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(SESSION_USER, userJson);

        // commit changes
        editor.commit();
    }

    public User getUserFromSession() {
        if (!(pref.getBoolean(SESSION_LOGGED_IN, false))) {
            // TODO: throw exception?
            return null;
        }

        Gson gson = new Gson();
        String json = pref.getString(SESSION_USER, "");
        User user = gson.fromJson(json, User.class);

        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
            return false;

        } else {
            return true;
        }
    }

    /**
     * Check login status from shared preference
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(SESSION_LOGGED_IN, false);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(mContext, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(i);
    }
}
