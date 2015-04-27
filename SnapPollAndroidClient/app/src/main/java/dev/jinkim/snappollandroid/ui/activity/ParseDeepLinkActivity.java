package dev.jinkim.snappollandroid.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.plus.PlusShare;

/**
 * Created by Jin on 3/23/15.
 *
 * Work in progress - Attempt on Facebook deep linking
 */
public class ParseDeepLinkActivity extends Activity {

    public static String TAG = ParseDeepLinkActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "### INSIDE PARSEDEEPLINKACTIVITY ###");

        String deepLinkId = PlusShare.getDeepLinkId(this.getIntent());
        Intent target = parseDeepLinkId(deepLinkId);
        if (target != null) {
            startActivity(target);
        }

        finish();
    }

    /**
     * Get the intent for an activity corresponding to the deep-link ID.
     *
     * @param deepLinkId The deep-link ID to parse.
     * @return The intent corresponding to the deep-link ID.
     */
    private Intent parseDeepLinkId(String deepLinkId) {
        Intent route = new Intent();
//        if ("/pages/create".equals(deepLinkId)) {
        if (deepLinkId.contains("snappoll://") && deepLinkId.contains("view_poll")) {
            Log.d(TAG, "DeepLinkId parsed: " + deepLinkId);
//            route.setClass(getApplicationContext(), CreatePageActivity.class);
        } else {
            // Fallback to the MainActivity in your app.
            route.setClass(getApplicationContext(), MainActivity.class);
        }
        return route;
    }
}