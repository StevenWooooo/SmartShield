package com.frogdesign.smartshield;

import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;

// import com.amazonaws.mobile.auth.facebook.FacebookButton;
// import com.amazonaws.mobile.auth.google.GoogleButton;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

public class AuthenticatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Add a call to initialize AWSMobileClient
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(final AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)  // true? show the Email and Password UI
                                //.signInButton(FacebookButton.class) // Show Facebook button
                                //.signInButton(GoogleButton.class) // Show Google button
                                .logoResId(R.drawable.logo) // Change the logo
                                .backgroundColor(Color.WHITE) // Change the backgroundColor
                                .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screenff
                                .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                                .canCancel(true)
                                .build();
                SignInUI signinUI = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
                signinUI.login(AuthenticatorActivity.this, MainActivity.class).authUIConfiguration(config).execute();
            }
        }).execute();
    }
}
