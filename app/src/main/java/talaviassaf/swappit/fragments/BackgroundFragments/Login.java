package talaviassaf.swappit.fragments.BackgroundFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.fragments.MainFragments.PurchaseVouchers;

public class Login extends Fragment {

    public static CallbackManager callbackManager;

    @BindView(R.id.googleSignIn)
    SignInButton googleSignIn;
    @BindView(R.id.facebookSignIn)
    LoginButton loginButton;

    private Activity activity;

    public static Login newInstance() {

        return new Login();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bg_login, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        callbackManager = CallbackManager.Factory.create();

        setupLoginFragment();

        return view;
    }

    @OnClick(R.id.signUp)
    public void signUp() {

        HomePage homePage = (HomePage) activity;

        if (homePage.viewPager.getCurrentItem() == 2)
            ((ViewPager) homePage.findViewById(R.id.containerViewPager)).setCurrentItem(1);
        else
            ((Container) ((PurchaseVouchers) homePage.fragments[3]).fragments[1]).viewPager.setCurrentItem(1);
    }

    private void saveDetailsFromFacebook(Profile profile) {

        LoadingPage.firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(profile.getProfilePictureUri(150, 150)).setDisplayName(profile.getName()).build());
    }

    private void setupLoginFragment() {

        for (int i = 0; i < googleSignIn.getChildCount(); i++) {

            View view = googleSignIn.getChildAt(i);

            if (view instanceof AppCompatTextView)
                view.setPadding(18, 0, 0, 0);
        }

        googleSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(new GoogleApiClient.Builder(view.getContext()).addApi(
                        Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder
                                (GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail().build()).build()), 4);
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                LoadingPage.firebaseUser.linkWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));

                Profile profile = Profile.getCurrentProfile();

                if (profile == null) {

                    new ProfileTracker() {

                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                            saveDetailsFromFacebook(currentProfile);
                        }
                    };
                } else
                    saveDetailsFromFacebook(profile);

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            LoadingPage.firebaseUser.updateEmail(object.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();

                parameters.putString("fields", "email");

                graphRequest.setParameters(parameters);

                graphRequest.executeAsync();

                startActivity(new Intent(getContext(), HomePage.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
}
