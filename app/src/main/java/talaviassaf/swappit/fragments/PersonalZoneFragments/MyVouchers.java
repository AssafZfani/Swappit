package talaviassaf.swappit.fragments.PersonalZoneFragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.models.Voucher;
import talaviassaf.swappit.utils.ExpandableListAdapter;

public class MyVouchers extends Fragment {

    @BindView(R.id.updateVouchers)
    View updateVouchers;
    @BindView(R.id.myVouchersMessage)
    AppCompatTextView myVouchersMessage;
    @BindView(R.id.myVouchersList)
    ExpandableListView myVouchersList;

    public static MyVouchers newInstance() {

        return new MyVouchers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_zone_my_vouchers, container, false);

        ButterKnife.bind(this, view);

        setupMyVouchers();

        return view;
    }

    private void setupMyVouchers() {

        final Activity activity = getActivity();

        Application.vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                myVouchersList.setAdapter(new ExpandableListAdapter(activity, true) {

                    @Override
                    public String getGroup(int groupPosition) {

                        Voucher voucher = dataSnapshot.child(LoadingPage.user.getMyVouchers().get(groupPosition)).getValue(Voucher.class);

                        if (activity != null && voucher != null)
                            return activity.getString(voucher.getType().equalsIgnoreCase("Scrip") ?
                                    R.string.show_by_voucher_type_scrip : voucher.getType().equalsIgnoreCase("Paper") ?
                                    R.string.show_by_voucher_type_paper : R.string.show_by_voucher_type_magnet_card) +
                                    activity.getString(R.string.personal_zone_my_vouchers_in) + voucher.getBrand() + activity.getString
                                    (R.string.personal_zone_my_vouchers_worth) + voucher.getValue() + " ₪";

                        return "";
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int vouchersCount = myVouchersList.getCount();

        updateVouchers.setVisibility(vouchersCount == 0 ? View.GONE : View.VISIBLE);

        myVouchersMessage.setVisibility(vouchersCount == 0 ? View.VISIBLE : View.GONE);

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.bounce);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                myVouchersMessage.setText(getString(R.string.personal_zone_settings_message));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        myVouchersMessage.startAnimation(animation);
    }
}
