package talaviassaf.swappit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.fragments.BackgroundFragments.Registration;
import talaviassaf.swappit.fragments.TicketFragments.Contact;
import talaviassaf.swappit.fragments.TicketFragments.Delete;
import talaviassaf.swappit.fragments.TicketFragments.Firm;
import talaviassaf.swappit.fragments.TicketFragments.Map;
import talaviassaf.swappit.fragments.TicketFragments.Ticket;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.models.Voucher;

public class VoucherInfo extends AppCompatActivity {

    public Dialog dialog;

    @BindView(R.id.voucherInfoMainLayout)
    View mainLayout;
    @BindView(R.id.cartActions)
    AppCompatButton cartActions;
    @BindView(R.id.voucherFragment)
    View voucherFragment;
    @BindViews({R.id.buy, R.id.cartActions})
    List<View> viewsToAppear;
    @BindViews({R.id.call, R.id.sms, R.id.divider1, R.id.divider2})
    List<View> viewsToDisappear;

    private Intent intent;
    private String voucherId;
    private LatLng latLng;
    private Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voucher_info);

        ButterKnife.bind(this);

        intent = getIntent();

        voucherId = intent.getStringExtra("VoucherId");

        setupVoucherInfo();
    }

    @OnClick(R.id.buy)
    public void buy() {

        setResult(RESULT_OK);

        if (!LoadingPage.user.isVoucherExistsInCart(voucherId))
            Firm.cart(this, voucherId, R.id.voucherFragment);

        finish();
    }

    @OnClick(R.id.cartActions)
    public void cart() {

        Firm.cart(this, voucherId, R.id.voucherFragment);

        cartActions.setText(getString(R.string.ticket_firm_cart, getString(LoadingPage.user.isVoucherExistsInCart(voucherId) ? R.string.ticket_firm_remove : R.string.ticket_firm_add)));

        setResult(RESULT_CANCELED);
    }

    public void cancel(View view) {

        dialog.dismiss();
    }

    public void confirm(View view) {

        Delete.delete(this);

        dialog.dismiss();
    }

    @OnClick({R.id.call, R.id.sms, R.id.share})
    public void contact(View view) {

        Contact.contact(this, intent.getStringExtra("Brand"), intent.getIntExtra("Discount", 0),
                intent.getStringExtra("PhoneNumber"), view.getId());
    }

    @OnClick(R.id.delete)
    public void delete() {

        intent.putExtra("VoucherId", voucherId);

        dialog = new Dialog(this, "Delete");
    }

    @OnClick(R.id.voucherFragment)
    public void resetCamera() {

        map.changeCamera(latLng);

        map.googleMap.clear();
    }

    private void setupVoucherInfo() {

        if (!HomePage.isUnregisteredUser)
            Registration.changeProfilePicture(mainLayout, R.id.profilePicture);

        HomePage.setupToolBar(this, getString(R.string.voucher_info_title));

        Application.vouchersRef.child(voucherId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Voucher voucher = dataSnapshot.getValue(Voucher.class);

                if (voucher != null) {

                    voucherFragment.setTag(voucher);

                    getSupportFragmentManager().beginTransaction().add(R.id.voucherFragment, Ticket.newInstance()).add(R.id.mapContainer,
                /* map = Map.newInstance(latLng = GPSTracker.getLatLngFromAddress(this, voucher.getAddress()))).commit(); */
                            map = Map.newInstance(latLng = new LatLng(32.011498, 34.783390))).commit();

                    if (voucher.isFirmProperty()) {

                        ButterKnife.apply(viewsToAppear, HomePage.Appear);

                        ButterKnife.apply(viewsToDisappear, HomePage.Disappear);

                        cartActions.setText(getString(R.string.ticket_firm_cart, getString(LoadingPage.user.isVoucherExistsInCart(voucherId) ? R.string.ticket_firm_remove : R.string.ticket_firm_add)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
