package talaviassaf.swappit.services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.IOException;
import java.util.List;

import talaviassaf.swappit.Application;
import talaviassaf.swappit.models.Voucher;

public class InitDistance extends Service {

    private Location location;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null)
            location = intent.getParcelableExtra("CurrentLocation");

        Application.vouchersRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                for (MutableData data : mutableData.getChildren()) {

                    Voucher voucher = data.getValue(Voucher.class);

                    if (voucher == null)
                        return Transaction.success(mutableData);

                    List<Address> addresses = null;

                    try {

                        addresses = new Geocoder(getBaseContext()).getFromLocationName(voucher.getAddress(), 1);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    Address address = null;

                    if (addresses != null && !addresses.isEmpty())
                        address = addresses.get(0);

                    if (location != null && address != null) {

                        float[] distanceArray = new float[5];

                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                address.getLatitude(), address.getLongitude(), distanceArray);

                        voucher.setDistance(distanceArray[0]);

                        data.setValue(voucher);
                    }
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
