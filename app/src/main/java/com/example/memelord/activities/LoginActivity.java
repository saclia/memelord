package com.example.memelord.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memelord.databinding.ActivityLoginBinding;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.google.ParseGoogleUtils;

import java.util.Arrays;
import java.util.List;

// Main activity
public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String FEATURE_WARN = "This feature is currently not implemented yet.";
    public static final List<String> PERMISSIONS_FB = Arrays.asList("email");

    protected ActivityLoginBinding mBinding;

    protected LoginButton mFBLoginBtn;
    protected Button mFacebookLoginBtn;
    protected Button mLoginBtn;
    protected Button mGoogleBtn;
    protected TextView mRegisterNavBtn;

    protected EditText mUsernameInput;
    protected EditText mPasswordInput;

    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        if(ParseUser.getCurrentUser() != null)
            navigateToHome();

        mFBLoginBtn = (LoginButton) mBinding.loginButton;
        mFacebookLoginBtn = mBinding.btnFB;
        // If you are using in a fragment, call loginButton.setFragment(this);
        mRegisterNavBtn = mBinding.tvRegisterBTN;
        mLoginBtn = mBinding.loginButton;
        mGoogleBtn = mBinding.btnGoogle;
        mUsernameInput = mBinding.etUsername;
        mPasswordInput = mBinding.etPassword;
        mProgressBar = mBinding.progressBar1;

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameInput.getText().toString();
                String password = mPasswordInput.getText().toString();
                showProgressBar();
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        hideProgressBar();
                        if(e != null) {
                            Log.e(TAG, "Failed to log in user", e);
                        } else {
                            navigateToHome();
                        }
                    }
                });
            }
        });

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
//                Toast.makeText(LoginActivity.this, FEATURE_WARN, Toast.LENGTH_SHORT).show();
            }
        });

        mRegisterNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRegistry();
            }
        });

        mFacebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        ParseGoogleUtils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void facebookLogin() {
        showProgressBar();
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, PERMISSIONS_FB, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                hideProgressBar();
                if(user != null) {
                    generateProfile(user);
                    navigateToHome();
                } else {
                    Log.e(TAG, "User cancelled login with Facebook");
                }
                if(e != null) {
                    Log.e(TAG, "Failed to login to Facebook with Parse", e);
                    return;
                }
            }
        });
    }

    protected void googleLogin() {
        showProgressBar();
        ParseGoogleUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                hideProgressBar();
                if(user != null) {
                    generateProfile(user);
                    navigateToHome();
                } else {
                    Log.e(TAG, "User cancelled login with Google");
                }
                if(e != null) {
                    Log.e(TAG, "Failed to login to Google with Parse", e);
                    return;
                }
            }
        });
    }

    protected void generateProfile(ParseUser user) {
        Profile profile = (Profile) user.getParseObject(User.KEY_PROFILE);
        if(profile == null)
            profile = new Profile();
        profile.setUser(user);
        profile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to save Profile", e);
                    return;
                }
            }
        });
        user.put(User.KEY_PROFILE, profile);
        user.saveInBackground();
    }

    protected void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void navigateToRegistry() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    protected void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }
}