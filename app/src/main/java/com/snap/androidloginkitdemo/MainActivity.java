package com.snap.androidloginkitdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

public class MainActivity extends AppCompatActivity {

    private View mContentView;
    private View mLoginButton;
    private LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener;
    private TextView mDisplayName;
    private TextView mExternalIdView;
    private MeData meData;
    private ImageView mAvatarImageView;
    private Button mSignOutButton;
    private View mLowerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.contentView);
        mLowerLayout = findViewById(R.id.lowerLayout);
        mLoginButton = SnapLogin.getButton(getBaseContext(), (ViewGroup)mLowerLayout);
        mDisplayName = findViewById(R.id.displayNameView);
        mExternalIdView = findViewById(R.id.externalIDView);
        mAvatarImageView = findViewById(R.id.avatarImageView);
        mSignOutButton = findViewById(R.id.signOutButton);

        mLoginStateChangedListener =
                new LoginStateController.OnLoginStateChangedListener() {
                    @Override
                    public void onLoginSucceeded() {
                        Log.d("SnapkitLogin", "Login was successful");
                        mSignOutButton.setVisibility(View.VISIBLE);
                        mSignOutButton.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                SignOutUser();
                            }
                        });

                        mLoginButton.setVisibility(View.INVISIBLE);
                        GetUserDetails();
                    }

                    @Override
                    public void onLoginFailed() {
                        Log.d("SnapkitLogin", "Login was unsuccessful");
                        mDisplayName.setText(R.string.not_logged_in);
                    }

                    @Override
                    public void onLogout() {
                        Log.d("SnapkitLogin", "User logged out");
                        mDisplayName.setText("");
                        mExternalIdView.setText("");
                        mAvatarImageView.setImageResource(R.drawable.bitmoji450x450);
                        mSignOutButton.setVisibility(View.INVISIBLE);
                        mLoginButton.setVisibility(View.VISIBLE);
                    }
                };

        SnapLogin.getLoginStateController(getBaseContext()).addOnLoginStateChangedListener(mLoginStateChangedListener);
    }

    private void GetUserDetails(){
        boolean isUserLoggedIn = SnapLogin.isUserLoggedIn(getBaseContext());
        if(isUserLoggedIn){
            Log.d("SnapkitLogin", "The user is logged in");

            //add the permissions we want to request
            String query = "{me{bitmoji{avatar},displayName,externalId}}";

            SnapLogin.fetchUserData(this, query, null, new FetchUserDataCallback() {
                @Override
                public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                    if (userDataResponse == null || userDataResponse.getData() == null) {
                        return;
                    }

                    meData = userDataResponse.getData().getMe();
                    if (meData == null) {
                        return;
                    }

                    //set the value of the display name
                    mDisplayName.setText(userDataResponse.getData().getMe().getDisplayName());
                    //set the value of the external id
                    mExternalIdView.setText(userDataResponse.getData().getMe().getExternalId());

                    //not all users have a bitmoji connected, if they do we will load their bitmoji avatar
                    if (meData.getBitmojiData() != null) {
                        Glide.with(getBaseContext()).load(meData.getBitmojiData().getAvatar()).into(mAvatarImageView);
                    }
                }

                @Override
                public void onFailure(boolean isNetworkError, int statusCode) {
                    Log.d("SnapkitLogin", "No user data fetched");
                }
            });

        }
    }

    private void SignOutUser(){
        Log.d("SnapkitLogin", "The user wants to log out");
        SnapLogin.getAuthTokenManager(this).revokeToken();

        boolean isUserLoggedIn = SnapLogin.isUserLoggedIn(getBaseContext());

        if(isUserLoggedIn){
            Log.d("SnapkitLogin", "The user is still signed in");
        } else {
            Log.d("SnapkitLogin", "The user has signed out");
        }
    }

}
