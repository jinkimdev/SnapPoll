package dev.jinkim.snappollandroid.app;

import android.app.Application;
import android.content.Context;

import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.session.SessionManager;

/**
 * Created by Jin on 11/27/14.
 * <p/>
 * Singleton App for SnapPoll
 */
public class App extends Application {

    private static App app = null;
    public static Context context;

    private SessionManager session;
    private User currentUser;

    private App() {
        App.context = this;
    }

    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    /**
     * Return current user if logged in and retrieved from session
     * otherwise return null
     */
    public User getCurrentUser() {
        if (currentUser != null) {
            return currentUser;
        }

        if (session == null) {
            session = new SessionManager(context);
        }
        return session.getUserFromSession();
    }

}
