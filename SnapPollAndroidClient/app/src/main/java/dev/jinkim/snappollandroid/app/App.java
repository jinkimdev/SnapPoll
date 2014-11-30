package dev.jinkim.snappollandroid.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jin on 11/27/14.
 * <p/>
 * Singleton App for SnapPoll
 */
public class App extends Application {

    private static App app = null;
    public static Context context;

    private App() {
        App.context = this;
    }

    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

}
