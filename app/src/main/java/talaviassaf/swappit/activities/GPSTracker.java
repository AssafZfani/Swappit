package talaviassaf.swappit.activities;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import talaviassaf.swappit.R;
import talaviassaf.swappit.models.Dialog;

@SuppressWarnings("deprecation, Registered")

public class GPSTracker extends HideKeyboard implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public Location location;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public static void setTextViewWithAddress(TextView textView, LatLng latLng) {

        Location location = new Location("Location");

        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        setTextViewWithAddress(textView, location);
    }

    public static void setTextViewWithAddress(final TextView textView, Location location) {

        Context context = textView.getContext();

        Volley.newRequestQueue(context).add(new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + location.getLatitude() + "," + location.getLongitude() + "&key=" + context.getString(R.string.google_geo_coding_api),
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    textView.setText(response.getJSONArray("results").getJSONObject(0).getString("formatted_address"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, null));
    }

    public static LatLng getLatLngFromAddress(Context context, String address) {

        try {

            Address foundAddress = new Geocoder(context).getFromLocationName(address, 1).get(0);

            return new LatLng(foundAddress.getLatitude(), foundAddress.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

        initLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {

            try {
                connectionResult.startResolutionForResult(this, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }

        } else
            Toast.makeText(this, "Location services connection failed with code "
                    + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 10 * 1000).setFastestInterval(1000);
    }

    @Override
    public void onLocationChanged(Location location) {

        handleNewLocation(location);
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (googleApiClient.isConnected())
            initLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {

                case 1:
                    if (googleApiClient.isConnected())
                        initLocation();
                    break;
                case 2:
                    new Dialog(this, "Camera");
                    break;
            }
        }
    }

    @Override
    protected void onStart() {

        googleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {

        googleApiClient.disconnect();

        super.onStop();
    }

    private void handleNewLocation(Location location) {

        this.location = location;

        ((HomePage) this).fragments[0].onResume();
    }

    private void initLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (!(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                return;
            }
        }

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location == null)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        else
            handleNewLocation(location);
    }
}
