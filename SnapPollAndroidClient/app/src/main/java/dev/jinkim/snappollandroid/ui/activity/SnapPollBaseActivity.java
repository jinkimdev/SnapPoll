package dev.jinkim.snappollandroid.ui.activity;

import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.otto.Bus;

import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.event.GoogleApiClientConnectedEvent;
import dev.jinkim.snappollandroid.event.RevokeGplusAccessEvent;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.session.SessionManager;

/**
 * Created by Jin on 1/21/15.
 */
public class SnapPollBaseActivity extends ActionBarActivity {

    private static final String TAG = "SnapPollBaseActivity ####";

    private Activity mActivity;
    protected SessionManager session;
    protected Bus bus;

    /* GOOGLE LOGIN */
    protected static final int RC_SIGN_IN = 0;
    protected GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    protected boolean mIntentInProgress;
    protected boolean mSignInClicked;
    protected ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        session = new SessionManager(mActivity);
        bus = BusProvider.getInstance();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mSignInClicked = false;
                        Log.d(TAG, "GoogleApiClient connected");

                        bus.post(new GoogleApiClientConnectedEvent(true));
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                        bus.post(new GoogleApiClientConnectedEvent(false));
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        if (!connectionResult.hasResolution()) {
                            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mActivity,
                                    0).show();
                            return;
                        }

                        if (!mIntentInProgress) {
                            // Store the ConnectionResult for later usage
                            mConnectionResult = connectionResult;

                            if (mSignInClicked) {
                                // The user has already clicked 'sign-in' so we attempt to
                                // resolve all
                                // errors until the user is signed in, or they cancel.
                                resolveSignInError();
                            }
                        }
                    }
                }).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    protected User getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String userFirstName = currentPerson.getName().getGivenName();
                String userPhotoUrl = currentPerson.getImage().getUrl();
                String userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.d(TAG, "Name: " + userFirstName + ", email: " + userEmail
                        + ", Image: " + userPhotoUrl);

                User user = new User(currentPerson);
                user.setUserId(userEmail);

//                loginUserToApi(user);
                session.createLoginSession("gPlus", user);

                return user;

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method to resolve any signin errors
     */
    protected void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Revoking access from google
     */
    protected void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked");
                            mGoogleApiClient.connect();

                            bus.post(new RevokeGplusAccessEvent());
                        }
                    });
        }
//        session.validateLogin();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    protected void displaySnackBar(String msg, String btnText, View.OnClickListener listener) {
        SnackBar snackBar;
        if (btnText != null) {
            snackBar = new SnackBar(this, msg, btnText, listener);
        } else {
            snackBar = new SnackBar(this, msg);
        }
        snackBar.show();
    }

    protected void displaySnackBar(String msg) {
        SnackBar snackBar = new SnackBar(this, msg);
        // TODO: Use theme color of resource
        snackBar.setBackgroundSnackBar(Color.parseColor("#555"));
        snackBar.show();
    }

}
