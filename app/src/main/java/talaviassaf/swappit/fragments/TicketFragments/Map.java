package talaviassaf.swappit.fragments.TicketFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.GPSTracker;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.activities.ShowBy;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Voucher;

public class Map extends SupportMapFragment implements OnMapReadyCallback {

    public GoogleMap googleMap;
    private Activity activity;
    private View cleanChosenCity;
    private AppCompatTextView chosenCity;

    public static Map newInstance(LatLng latLng) {

        Map map = new Map();

        Bundle args = new Bundle();

        args.putParcelable("LatLng", latLng);

        map.setArguments(args);

        return map;
    }

    public void changeCamera(LatLng latLng) {

        googleMap.clear();

        loadMarkers();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2)).position(latLng));
    }

    @Override
    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);

        activity = getActivity();

        if (activity != null) {

            chosenCity = activity.findViewById(R.id.chosenCity);

            cleanChosenCity = activity.findViewById(R.id.cleanChosenCity);
        }

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        Bundle args = getArguments();

        if (args != null) {

            LatLng latLng = args.getParcelable("LatLng");

            if (latLng == null)
                latLng = new LatLng(32.0115, 34.7834);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }

        if (activity instanceof ShowBy) {

            final CleanEditText locationSearch = activity.findViewById(R.id.locationSearch);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {

                    GPSTracker.setTextViewWithAddress(locationSearch, latLng);

                    saveCity(locationSearch.getText().toString());

                    changeCamera(latLng);
                }
            });

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {

                    loadMarkers();
                }
            });

            locationSearch.addTextChangedListener(new CleanEditText.CleanTextWatcher(locationSearch) {

                @Override
                public void afterTextChanged(Editable s) {

                    super.afterTextChanged(s);

                    locationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                            String address = v.getText().toString();

                            if (address.length() != 0) {

                                try {

                                    saveCity(address);

                                    changeCamera(GPSTracker.getLatLngFromAddress(activity, address));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            return false;
                        }
                    });
                }
            });
        }
    }

    private void loadMarkers() {

        Application.vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Voucher voucher = snapshot.getValue(Voucher.class);

                    if (voucher != null && LoadingPage.user.areNotThereDeletedVouchers() || (voucher != null &&
                            !LoadingPage.user.getDeletedVouchers().contains(voucher.getId()))) {

                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource
                                (R.drawable.marker1)).position(new LatLng(Double.parseDouble(voucher.getRealLocation().getLat()),
                                Double.parseDouble(voucher.getRealLocation().getLon()))));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveCity(String address) {

        if (address != null && !address.endsWith("null")) {

            int index = address.lastIndexOf(',');

            chosenCity.setText(index == -1 ? address : address.substring(index + 2, address.length()));

            cleanChosenCity.setVisibility(View.VISIBLE);
        }

        activity.getIntent().putExtra("Address", chosenCity.getText());
    }
}
