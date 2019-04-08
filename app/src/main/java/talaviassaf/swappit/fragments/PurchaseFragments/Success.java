package talaviassaf.swappit.fragments.PurchaseFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;

public class Success extends Fragment {

    @BindView(R.id.address)
    AppCompatTextView address;
    @BindView(R.id.creditNumber)
    AppCompatTextView creditNumber;
    @BindView(R.id.postcode)
    AppCompatTextView postcode;
    @BindView(R.id.priceSum)
    AppCompatTextView priceSum;
    @BindView(R.id.savedSum)
    AppCompatTextView savedSum;

    public static Success newInstance() {

        return new Success();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.purchase_success, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.changeAddress, R.id.changePaymentSystem})
    public void change(View view) {

        Activity activity = getActivity();

        if (activity != null) {

            if (view.getId() == R.id.changePaymentSystem)
                activity.onBackPressed();
            else {

                ((ViewPager) activity.findViewById(R.id.purchaseViewPager)).setCurrentItem(0);

                activity.findViewById(R.id.continueToPay).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser) {

            address.setText(Application.sharedPreferences.getString("Address", ""));

            String creditNumberText = Application.sharedPreferences.getString("CreditNumber", "");

            if (!creditNumberText.isEmpty())
                creditNumber.setText(new StringBuilder(creditNumberText.substring(0, 4) + " " +
                        switchStringToAsterisks(creditNumberText.length() - 4)));

            String postcodeText = Application.sharedPreferences.getString("Postcode", "");

            String apartment = Application.sharedPreferences.getString("Apartment", "");

            postcode.setText(apartment.isEmpty() ? "" : " " + (getString(R.string.bg_field_apartment) + ": " + apartment + ", ") + postcodeText);

            postcode.setVisibility(postcode.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);

            priceSum.setText(new StringBuilder(getString(R.string.purchase_cart_total_payment) + " " + Application.sharedPreferences.getString("PriceSum", "")));

            savedSum.setText(new StringBuilder(getString(R.string.purchase_cart_saving) + " " + Application.sharedPreferences.getString("SavedSum", "")));
        }
    }

    private String switchStringToAsterisks(int stringLength) {

        StringBuilder result = new StringBuilder("");

        for (int i = 0; i < stringLength; i++)
            result.append("*").append(i != 0 && i % 3 == 0 ? " " : "");

        return result.toString();
    }
}
