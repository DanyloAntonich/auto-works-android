package helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import autoworks.app.R;
import br.com.dina.oauth.instagram.InstagramApp;

import com.facebook.*;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

/**
 * Created by INNOVATION on 2/12/2015.
 */
//Fragment for handling social APIs
public class MyCustomFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static final String TAG = "MyCustomFragment";
    protected LoginButton facebook_loginButton;
    protected GraphUser facebook_user;
    protected UiLifecycleHelper uiHelper;
    protected PendingAction pendingAction = PendingAction.NONE;
    protected View view;
    protected boolean useMyCustomFragment = true;
    //twitter
    protected TwitterLoginButton twitter_loginButton;
    protected static boolean clearCacheOnStop = true;

    //google api callback
    SignInButton google_loginButton;

    ImageView facebook_loginButton_icon;
    ImageView google_loginButton_icon;
    ImageView twitter_loginButton_icon;
    ImageView instagram_loginButton_icon;

    protected IOnGoogleConnectedHandler onGoogleConnectedHandler;


    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private static GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve
 * all issues preventing sign-in without waiting.
 */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    protected boolean isFacebookLoggedIn = false;
    protected boolean isTwitterLoggedIn = false;
    protected boolean isGooglePlusLoggedIn = false;
    protected boolean isInstagramLoggedIn = false;

    protected static InstagramApp instagramApp;


    //Instagram keys

    public static final String CLIENT_ID = "379d744556c743c090c8a2014779f59f";
    public static final String CLIENT_SECRET = "fd6ec75e44054da1a5088ad2d72f2253";
    public static final String CALLBACK_URL = "instagram://connect";

    public MyCustomFragment() {

    }

    public void setView(View view) {
        this.view = view;
    }

    protected Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            onSessionStateChange(session, state, exception);
        }
    };
    protected FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };


    protected enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this.getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        setSocialLogin();

    }



    private void setSocialLogin() {
        if (useMyCustomFragment) {
            //test facebook

            try {
                facebook_loginButton = (LoginButton) this.view.findViewById(R.id.connect_facebook);
                facebook_loginButton_icon = (ImageView) this.view.findViewById(R.id.connect_facebook_icon);
                facebook_loginButton_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        facebook_loginButton.callOnClick();
                        logoutTwitter();
                        logoutGooglePlus();
                        logoutInstagram();
                    }
                });
                // facebook_loginButton.setBackgroundResource(R.drawable.facebook);
                // facebook_loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                // facebook_loginButton.setOnClickListener(this);
                //facebook_loginButton.log
                facebook_loginButton.setFragment(this);
                facebook_loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
            } catch (Exception ex) {
                Log.d(TAG, "This view do not contain a facebook login button or view is null");
            }

            //test twitter
            twitter_loginButton = (TwitterLoginButton) this.view.findViewById(R.id.connect_twitter);

            //twitter_loginButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.twitter), null, null);
            // twitter_loginButton.setBackgroundResource(R.drawable.twitter);


            // twitter_loginButton.setOnClickListener(this);
            twitter_loginButton_icon = (ImageView) this.view.findViewById(R.id.connect_twitter_icon);
            twitter_loginButton_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twitter_loginButton.callOnClick();
                    logoutFacebook();
                    logoutGooglePlus();
                    logoutInstagram();
                }
            });


            google_loginButton = (SignInButton) this.view.findViewById(R.id.connect_googleplus);
            google_loginButton_icon = (ImageView) this.view.findViewById(R.id.connect_googleplus_icon);
            google_loginButton_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutFacebook();
                    logoutInstagram();
                    logoutTwitter();

                    if (v.getId() == R.id.connect_googleplus_icon
                            && !mGoogleApiClient.isConnecting()) {
                        if (mGoogleApiClient.isConnected()) {

                            //logout - make a logout confirm dialog here

                            new AlertDialog.Builder(MyCustomFragment.this.getActivity())
                                    .setTitle("Logged in as: ")
                                    .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            if (mGoogleApiClient.isConnected()) {
                                                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                                mGoogleApiClient.disconnect();
                                                mGoogleApiClient.connect();
                                                isGooglePlusLoggedIn = false;
                                                //facebook_loginButton.setEnabled(true);
                                                // setEnableFacebookButton(true);
                                                //twitter_loginButton.setEnabled(true);
                                                //setEnableTwitterButton(true);
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null).show();


                        } else {
                            //login
                            mSignInClicked = true;
                            resolveSignInError();
                        }
                    }
                }
            });

            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .build();

            //Instagram
            ImageView instagram_loginButton_icon = (ImageView) this.view.findViewById(R.id.connect_instagram_icon);
            if (instagramApp == null) {
                instagramApp = new InstagramApp(this.getActivity(), CLIENT_ID,
                        CLIENT_SECRET, CALLBACK_URL);
            }
            //instagramApp.setListener(instagramAuthListener);

            instagram_loginButton_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutTwitter();
                    logoutFacebook();
                    logoutGooglePlus();

                    if (instagramApp.hasAccessToken()) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(
                                getActivity());
                        builder.setMessage("Disconnect from Instagram?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                instagramApp.resetAccessToken();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        instagramApp.authorize();
                    }
                }
            });
        }
    }
/*
    private void setEnableFacebookButton(boolean enable)
    {
        facebook_loginButton_icon.setEnabled(enable);
        if (!enable)
            facebook_loginButton_icon.setImageResource(R.drawable.facebook_disable);
        else
            facebook_loginButton_icon.setImageResource(R.drawable.facebook);

    }
    private void setEnableTwitterButton(boolean enable)
    {
        twitter_loginButton_icon.setEnabled(enable);
        if (!enable)
            twitter_loginButton_icon.setImageResource(R.drawable.twitter_disable);
        else
            twitter_loginButton_icon.setImageResource(R.drawable.twitter);
        // google_loginButton_icon.setIm
    }
    private void setEnableGoogleplusButton(boolean enable)
    {
        google_loginButton_icon.setEnabled(enable);
        if (!enable)
            google_loginButton_icon.setImageResource(R.drawable.googleplus_disable);
        else
            google_loginButton_icon.setImageResource(R.drawable.googleplus);
       // google_loginButton_icon.setIm
    }*/

    //clear cache, logout all social networks when click a login button (of any social network)

    private static void logoutFacebook() {
        if (Session.getActiveSession()!=null)
         Session.getActiveSession().closeAndClearTokenInformation();
    }

    private static void logoutTwitter()
    {
        //twitter_loginButton

        try {
            Twitter.logOut();
        }catch (Exception ex)
        {

        }

    }
    private static void logoutGooglePlus() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }

    }
    private static void logoutInstagram()
    {
        if (instagramApp!=null)
            instagramApp.resetAccessToken();
    }


    //use this when user choose logout
    public static void logoutAllSocialNetworks() {
        Log.d("MyCustomFragment", "logoutAllSocialNetworks");
        //facebook


        //twitter
        //don't need


        //google plus

        if (clearCacheOnStop)
        {
            logoutFacebook();
            logoutTwitter();
            logoutGooglePlus();
            logoutInstagram();
        }


    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // session.req
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            new AlertDialog.Builder(this.getActivity())
                    .setTitle("cancel")
                    .setMessage("permission not granted")
                    .setPositiveButton("ok", null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            //handlePendingAction();
        }

        if (state.isOpened()) {
            Log.d(TAG,"access token 1 =" + session.getAccessToken());
            isFacebookLoggedIn = true;
            //twitter_loginButton.setEnabled(false);
            //  setEnableTwitterButton(false);
            // google_loginButton.setEnabled(false);
            //setEnableGoogleplusButton(false);

        } else {
            Log.d(TAG,"access token 2 =" + session.getAccessToken());
            isFacebookLoggedIn = false;
            //  setEnableTwitterButton(true);
            //twitter_loginButton.setEnabled(true);
            //google_loginButton.setEnabled(true);
            //  setEnableGoogleplusButton(true);
        }


    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        /*if (view.getId() == R.id.connect_googleplus
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;

            resolveSignInError();
        }*/
    }

    public void onStart() {
        super.onStart();
        if (useMyCustomFragment) {
            clearCacheOnStop = true;
            mGoogleApiClient.connect();
        }

        //twitter_loginButton.callOnClick();
        // mGoogleApiClient.get
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

       // Toast.makeText(this.getActivity(), "onDetachFragment", Toast.LENGTH_SHORT);
    }
    public void onStop() {

        super.onStop();


        if (useMyCustomFragment) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();

            }
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
       // Toast.makeText(this.getActivity(), "User is connected!", Toast.LENGTH_LONG).show();


        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            isGooglePlusLoggedIn = true;
            // facebook_loginButton.setEnabled(false);
            //setEnableFacebookButton(false);
            //  twitter_loginButton.setEnabled(false);
            // setEnableTwitterButton(false);

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String id = currentPerson.getId();
            Log.d("Test ", "name = " + personName + ", email = " + email + ", id = " + id);
            if (onGoogleConnectedHandler != null) {
                onGoogleConnectedHandler.onConnected(currentPerson, email);
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (useMyCustomFragment) {
            uiHelper.onResume();
            Session session = Session.getActiveSession();
            if (session != null &&
                    (session.isOpened() || session.isClosed())) {
                onSessionStateChange(session, session.getState(), null);
            }

            if (instagramApp.hasAccessToken()) {
                isInstagramLoggedIn = true;
            } else {
                isInstagramLoggedIn = false;
            }

            try {
                TwitterSession tsession =
                        Twitter.getSessionManager().getActiveSession();

                if (tsession != null) {
                    twitter_loginButton.getCallback().success(new Result<TwitterSession>(tsession, null));
                }
            } catch (Exception ex) {

            }
            if (instagramApp.hasAccessToken()) {
                instagramApp.getListener().onSuccess();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (useMyCustomFragment) {
            uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
            twitter_loginButton.onActivityResult(requestCode, resultCode, data);
            //  TwitterSession session =
            //    Twitter.getSessionManager().getActiveSession();


            if (requestCode == RC_SIGN_IN) {
                if (resultCode != Activity.RESULT_OK) {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (useMyCustomFragment) {
            uiHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (useMyCustomFragment) {
            uiHelper.onDestroy();

            Toast.makeText(this.getActivity(), "onDestroyFragment", Toast.LENGTH_SHORT);
            if (clearCacheOnStop) {
                logoutFacebook();
                logoutTwitter();
                logoutGooglePlus();
                logoutInstagram();
            }
        }
    }

    public interface IOnGoogleConnectedHandler {
        void onConnected(Person person, String email);
    }

}
