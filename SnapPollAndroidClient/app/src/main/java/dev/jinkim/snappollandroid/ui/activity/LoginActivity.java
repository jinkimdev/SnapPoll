package dev.jinkim.snappollandroid.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.GoogleApiClientConnectedEvent;
import dev.jinkim.snappollandroid.event.RevokeGplusAccessEvent;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.session.SessionManager;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class LoginActivity extends SnapPollBaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private LoginButton btnFacebookLogin;
    private SignInButton btnGoogleLogin;
    private Button btnSignOut;
    private Button btnRevokeAccess;

    private boolean loginActive = false;
//    private GoogleApiClient mGoogleApiClient;

    private UiLifecycleHelper uiHelper;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private ImageView ivBackground;

    private SessionManager appSession;

    private Activity mActivity;

    /* prevent launching main activity multiple times */
    private boolean googleConnectedOnLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // change the system status bar from orange (default theme) to dark gray on this activity only
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_gray));
        }

        // facebook login
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        // hide action bar
//        getSupportActionBar().hide();

        mActivity = this;
        appSession = new SessionManager(mActivity);

        setContentView(R.layout.activity_login);

//        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_login_screen);

        // load background image
        loadBackgroundImage();

        btnFacebookLogin = (LoginButton) findViewById(R.id.fb_login_button);
        // TODO: Temporarily unavailable
        btnFacebookLogin.setEnabled(false);
        btnFacebookLogin.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser fbUser) {
                if (fbUser != null) {
                    Log.d(TAG, "Facebook: " + fbUser.getName());
                    User user = new User(fbUser);

                    loginUserToApi(user);
                    appSession.createLoginSession(mActivity.getResources().getString(R.string.session_facebook), user);

                } else {
                    Log.d(TAG, "Facebook: Not logged in");
                }
            }
        });

        btnGoogleLogin = (SignInButton) findViewById(R.id.btn_sign_in);
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGplus();
            }
        });

        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutFromGplus();
            }
        });

        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeGplusAccess();
            }
        });

        bus.register(this);
    }

    private void loadBackgroundImage() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        ivBackground = (ImageView) findViewById(R.id.login_iv_background);
        Picasso.with(this)
                .load(R.drawable.login_background)
                .resize(width, height)
                .centerCrop()
                .into(ivBackground);
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };


//    /**
//     * Method to resolve any signin errors
//     */
//    private void resolveSignInError() {
//        if (mConnectionResult.hasResolution()) {
//            try {
//                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
//            } catch (IntentSender.SendIntentException e) {
//                mIntentInProgress = false;
//                mGoogleApiClient.connect();
//            }
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        uiHelper.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }


    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnGoogleLogin.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnGoogleLogin.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        showProgressBar(R.string.msg_signing_in_google);
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

    private void loginUserToApi(User user) {
        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
        rest.loginUser(user, new Callback<Object>() {
            @Override
            public void success(Object user, Response response) {
                if (user != null) {
                    Toast.makeText(mActivity, R.string.msg_logged_in_snappoll, Toast.LENGTH_SHORT).show();
                    moveToMainScreen();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Could not connect to SnapPoll API login");
            }
        });
    }

    private void moveToMainScreen() {
        hideProgressBar();
        Intent in = new Intent(this, MainActivity.class);

        // Prevent from double login (when both Google+ and Facebook login sessions are available
        if (!loginActive) {
            startActivity(in);
            loginActive = true;
        }
        this.finish();
    }

    @Subscribe
    public void onGoogleApiClientConnected(GoogleApiClientConnectedEvent event) {
        Log.d(TAG, "Received GoogleApiClientConnectedEvent");

        if (!googleConnectedOnLogin) {
            // set this so subscribe will only get events from BaseActivity of Login, not Main
            googleConnectedOnLogin = true;

            if (event.success) {
                showProgressBar(R.string.msg_signing_in_google);
                User u = getProfileInformation();
                Log.d(TAG, "Google user: " + u.getFirstName() + " " + u.getLastName());

                loginUserToApi(u);

            } else {
                updateUI(false);
            }
        }
    }

    @Subscribe
    public void onRevokeGoogleAccess(RevokeGplusAccessEvent event) {
        Log.d(TAG, "Received RevokeGplusAccessEvent");

        updateUI(false);
    }

}
