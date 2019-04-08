package talaviassaf.swappit.fragments.BackgroundFragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.models.CleanEditText;

import static android.util.Patterns.EMAIL_ADDRESS;

public class Registration extends Fragment {

    @BindView(R.id.bgProfilePicture)
    AppCompatImageView profilePicture;
    @BindView(R.id.email)
    CleanEditText email;
    @BindView(R.id.firstName)
    CleanEditText firstName;
    @BindView(R.id.lastName)
    CleanEditText lastName;
    @BindView(R.id.address)
    CleanEditText address;
    @BindView(R.id.apartment)
    CleanEditText apartment;
    @BindView(R.id.postcode)
    CleanEditText postcode;
    @BindViews({R.id.save, R.id.cancel})
    List<View> views;

    public static Registration newInstance() {

        return new Registration();
    }

    public static void changeProfilePicture(final View view, final int id) {

        final Context context = view.getContext();

        final AppCompatImageView profilePicture = view.findViewById(id);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
            Glide.with(context).asBitmap().load(user.isAnonymous() ? new File(Application.sharedPreferences.getString
                    ("ProfilePicture", "")) : user.getPhotoUrl()).into(new BitmapImageViewTarget(profilePicture) {

                @Override
                protected void setResource(Bitmap resource) {

                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);

                    circularBitmapDrawable.setCircular(true);

                    profilePicture.setImageDrawable(circularBitmapDrawable);
                }

                @Override
                public void onLoadFailed(Drawable errorDrawable) {

                    profilePicture.setImageResource(id == R.id.bgProfilePicture ? R.drawable.camera1 : R.drawable.profile_picture);
                }
            });
    }

    public static boolean isValidData(Activity activity, String[] details) {

        if (!EMAIL_ADDRESS.matcher(details[0]).matches())
            Toast.makeText(activity, "" + (details[0].isEmpty() ? activity.getString(R.string.toast_email) : activity.getString(R.string.toast_correctly2)), Toast.LENGTH_SHORT).show();
        else if (details[1].length() < 2)
            Toast.makeText(activity, activity.getString(R.string.toast_first_name) + (details[1].isEmpty() ? "" : activity.getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else if (details[2].length() < 2)
            Toast.makeText(activity, activity.getString(R.string.toast_last_name) + (details[2].isEmpty() ? "" : activity.getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else if (details[3].length() < 4)
            Toast.makeText(activity, activity.getString(R.string.toast_home_address) + (details[3].isEmpty() ? "" : activity.getString(R.string.toast_correctly2)), Toast.LENGTH_SHORT).show();
        else
            return true;

        return false;
    }

    public static void initAddressAndCity(final CleanEditText address, String text) {

        Context context = address.getContext();

        try {

            address.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                    new FetchData().execute("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + text +
                            "&types=address&language=he_IL&key=" + context.getResources().getString(R.string.google_api_key)).get()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveData(AppCompatImageView profilePicture, final String[] details) {

        if (profilePicture != null) {

            profilePicture.setDrawingCacheEnabled(true);

            profilePicture.buildDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            profilePicture.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, baos);

            FirebaseStorage.getInstance().getReference().child("ProfilePictures/" + LoadingPage.firebaseUser.getUid() + ".jpg")
                    .putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @SuppressWarnings("VisibleForTests")

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    LoadingPage.firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(taskSnapshot.getDownloadUrl())
                            .setDisplayName(details[1] + " " + details[2]).build());
                }
            });
        }

        LoadingPage.firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(details[1] + " " + details[2]).build());

        Application.sharedPreferences.edit().putString("Address", details[3])
                .putString("Apartment", details[4].isEmpty() ? "0" : details[4])
                .putString("Postcode", details[5].isEmpty() ? "0" : details[5]).apply();
    }

    @OnClick(R.id.register)

    public void register() {

        Activity activity = getActivity();

        String[] details = {email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                address.getText().toString(), apartment.getText().toString(), postcode.getText().toString()};

        if (isValidData(activity, details)) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null)
                user.linkWithCredential(EmailAuthProvider.getCredential(details[0], "123456"));

            saveData(profilePicture, details);

            Toast.makeText(activity, getString(R.string.toast_sign_in_succeeded), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bg_registration, container, false);

        ButterKnife.bind(this, view);

        ButterKnife.apply(views, HomePage.Disappear);

        setupRegistration();

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (Application.sharedPreferences.getString("ProfilePicture", null) != null)
            changeProfilePicture(getView(), R.id.bgProfilePicture);
    }

    private void setupRegistration() {

        PersonalZone.setupEditText(email, "Email");
        PersonalZone.setupEditText(firstName, "FirstName");
        PersonalZone.setupEditText(lastName, "LastName");
        PersonalZone.setupEditText(address, "Address");
        PersonalZone.setupEditText(apartment, "Apartment");
        PersonalZone.setupEditText(postcode, "Postcode");

        address.setText(Application.sharedPreferences.getString("Address", ""));
    }

    private static class FetchData extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection connection = null;

            String json = null;

            try {

                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line);

                bufferedReader.close();

                json = stringBuilder.toString();

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (connection != null)
                    connection.disconnect();
            }

            JSONObject obj = null;

            try {

                if (json != null)
                    obj = new JSONObject(json);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            if (obj != null) {

                try {

                    ArrayList<String> data = new ArrayList<>();

                    JSONArray jsonArray = obj.getJSONArray("predictions");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("structured_formatting");

                        String value = jsonObject.getString("main_text");

                        String country = "";

                        if (jsonObject.has("secondary_text"))
                            country = jsonObject.getString("secondary_text");

                        if ((country.isEmpty() || country.contains("ישראל") && !data.contains(value)))
                            data.add(value + ", " + country.substring(0, country.indexOf(",")));
                    }

                    return data;

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}