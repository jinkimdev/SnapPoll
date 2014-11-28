package dev.jinkim.snappollandroid.app;

import android.app.Application;

/**
 * Created by Jin on 11/27/14.
 *
 * Singleton App for SnapPoll
 */
public class App extends Application {

    private static App app;

    private App() {
    }

    public static App getInstance() {
        if (app == null) {
            app = new App();
        }

        return app;
    }

}
