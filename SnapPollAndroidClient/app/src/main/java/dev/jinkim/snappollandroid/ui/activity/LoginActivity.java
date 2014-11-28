package dev.jinkim.snappollandroid.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 11/27/14.
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_login);
    }
}
