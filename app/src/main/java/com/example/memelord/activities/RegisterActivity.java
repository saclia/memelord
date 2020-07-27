package com.example.memelord.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memelord.R;
import com.example.memelord.databinding.ActivityRegisterBinding;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.facebook.ParseFacebookUtils;

public class RegisterActivity extends LoginActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    public static final String TN_WARN_EMAIL = "Please enter a valid email address!";
    public static final String TN_WARN_USN = "Please enter a valid username.";
    public static final String TN_WARN_PASS = "Please enter a valid password.";

    private ActivityRegisterBinding mBinding;

    private Button mBTNRegister;
    private EditText mETEmail;
    private TextView mTVLoginNavBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        if(ParseUser.getCurrentUser() != null)
            navigateToHome();

        mBTNRegister = mBinding.btnSignUp;
        mETEmail = mBinding.etEmail;
        mFBLoginBtn = mBinding.loginButton;
        mFacebookLoginBtn = mBinding.btnFB;
        mUsernameInput = mBinding.etUsername;
        mPasswordInput = mBinding.etPassword;
        mTVLoginNavBtn = mBinding.tvRegisterBTN;
        mGoogleBtn = mBinding.btnGoogle;
        mProgressBar = mBinding.progressBar;
        mBTNRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mETEmail.getText().toString();
                String username = mUsernameInput.getText().toString();
                String password = mPasswordInput.getText().toString();
                if(email == null || email.isEmpty()) {
                    provideToastNotification(TN_WARN_EMAIL);
                } else if(username == null || username.isEmpty()) {
                    provideToastNotification(TN_WARN_USN);
                } else if(password == null || password.isEmpty()) {
                    provideToastNotification(TN_WARN_PASS);
                }
                register(email, username, password);
            }
        });

        mTVLoginNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mFacebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        setContentView(view);
    }

    private void provideToastNotification(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT);
    }

    private void register(String email, String password, String username) {
        showProgressBar();
        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setScreenName(username);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressBar();
                if(e != null) {
                    Log.e(TAG, "Failed to register user via Parse", e);
                    return;
                }
                Profile profile = new Profile();
                profile.setUser(user);
                profile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        user.setProfile(profile);
                        user.saveInBackground();
                    }
                });
                navigateToHome();
            }
        });
    }

    @Override
    protected void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void facebookLogin() {
        super.facebookLogin();
    }
}