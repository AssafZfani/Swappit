package talaviassaf.swappit.fragments.BackgroundFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.models.CleanEditText;

public class EditAddress extends Fragment {

    @BindView(R.id.address)
    CleanEditText address;
    @BindView(R.id.apartment)
    CleanEditText apartment;
    @BindView(R.id.postcode)
    CleanEditText postcode;
    @BindView(R.id.successTitle)
    View successTitle;

    private Activity activity;

    public static EditAddress newInstance() {

        return new EditAddress();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bg_edit_address, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();


        setupAddressFields();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null)
            successTitle.setVisibility(((HomePage) activity).getCurrentDisplayedFragment() == 2 ? View.GONE : View.VISIBLE);
    }

    private void setupAddressFields() {

        PersonalZone.setupEditText(address, "Address");
        PersonalZone.setupEditText(apartment, "Apartment");
        PersonalZone.setupEditText(postcode, "Postcode");

        address.setText(Application.sharedPreferences.getString("Address", ""));
    }

    @OnClick({R.id.cancel, R.id.confirm})

    public void editAddress(View view) {

        if (view.getId() == R.id.confirm) {

            String addressText = address.getText().toString();

            if (addressText.length() < 4) {

                Toast.makeText(activity, getString(R.string.toast_home_address) +
                        (addressText.isEmpty() ? "" : getString(R.string.toast_correctly2)), Toast.LENGTH_SHORT).show();

                return;

            } else {

                String apartmentText = apartment.getText().toString(), postcodeText = postcode.getText().toString();

                Application.sharedPreferences.edit().putString("Address", addressText)
                        .putString("Apartment", apartmentText.isEmpty() ? "0" : apartmentText)
                        .putString("Postcode", postcodeText.isEmpty() ? "0" : postcodeText).apply();

                Toast.makeText(activity, getString(R.string.toast_home_address_updated_successfully), Toast.LENGTH_SHORT).show();
            }
        }

        activity.onBackPressed();
    }
}
