package talaviassaf.swappit.fragments.UploadFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.GPSTracker;
import talaviassaf.swappit.activities.HomePage;

public class Success extends Fragment {

    @BindView(R.id.address)
    AppCompatTextView address;
    @BindView(R.id.voucherWorth)
    AppCompatTextView voucherWorth;
    @BindView(R.id.percentOff)
    AppCompatTextView percentOff;
    @BindView(R.id.salePrice)
    AppCompatTextView salePrice;

    private Activity activity;

    public static Success newInstance() {

        return new Success();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_success, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        return view;
    }

    @OnClick(R.id.address)
    public void changeAddress() {

        ((ViewPager) activity.findViewById(R.id.uploadViewPager)).setCurrentItem(0);

        activity.findViewById(R.id.next).setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser) {

            GPSTracker.setTextViewWithAddress(address, ((HomePage) activity).location);

            voucherWorth.setText(getString(R.string.upload_success_voucher_worth, NumberFormat.getIntegerInstance().format(Application.sharedPreferences.getInt("Value", 0))));

            percentOff.setText(getString(R.string.upload_success_percent_off, Application.sharedPreferences.getInt("Discount", 0)));

            salePrice.setText(getString(R.string.upload_success_sale_price, NumberFormat.getIntegerInstance().format(Application.sharedPreferences.getInt("Price", 0))));
        }
    }
}
