package talaviassaf.swappit.fragments.MainFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.GPSTracker;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.fragments.BackgroundFragments.EditAddress;
import talaviassaf.swappit.fragments.UploadFragments.Success;
import talaviassaf.swappit.fragments.UploadFragments.VoucherDetails;
import talaviassaf.swappit.fragments.UploadFragments.VoucherType;
import talaviassaf.swappit.fragments.UploadFragments.VoucherWorth;
import talaviassaf.swappit.models.CircularSeekBar;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.models.RealLocation;
import talaviassaf.swappit.models.Voucher;

public class UploadVouchers extends Fragment {

    @BindView(R.id.uploadViewPager)
    public ViewPager viewPager;
    @BindView(R.id.phase)
    AppCompatTextView phase;
    @BindView(R.id.uploadFragmentTitle)
    AppCompatTextView fragmentTitle;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.next)
    AppCompatButton next;

    private Activity activity;

    public static UploadVouchers newInstance() {

        return new UploadVouchers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_upload_vouchers, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        setupUploadVouchers();

        return view;
    }

    @OnClick(R.id.next)
    public void next() {

        int item = viewPager.getCurrentItem();

        switch (item) {

            case 4:
                if (Application.sharedPreferences.getString("VoucherType", null) == null)
                    Toast.makeText(activity, getString(R.string.toast_voucher_type), Toast.LENGTH_SHORT).show();
                else if (Application.sharedPreferences.getString("Brand", "").length() < 2)
                    Toast.makeText(activity, getString(R.string.toast_brand), Toast.LENGTH_SHORT).show();
                else
                    moveToNextFragment(item);
                break;
            case 3:
                int value = Application.sharedPreferences.getInt("Value", 0);

                if (value > 50)
                    moveToNextFragment(item);
                else
                    Toast.makeText(activity, getString(value == 0 ? R.string.toast_value_empty :
                            R.string.toast_value_less_than_50), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String expDateText = Application.sharedPreferences.getString("ExpDate", null);

                @SuppressLint("SimpleDateFormat")

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                dateFormat.setLenient(false);

                boolean hasMagnetCardSelected = Application.sharedPreferences.getString("VoucherType", "").equalsIgnoreCase("Magnet Card");

                try {

                    dateFormat.parse(expDateText);

                    if (Application.sharedPreferences.getString("Barcode", "").length() < 4)
                        Toast.makeText(activity, getString(R.string.toast_barcode) + (getString(hasMagnetCardSelected ?
                                R.string.toast_barcode_magnet_card : R.string.toast_barcode_voucher)), Toast.LENGTH_SHORT).show();
                    else if (Application.sharedPreferences.getString("Cvv", "").length() < 3)
                        Toast.makeText(activity, getString(R.string.toast_magnet_card_cvv), Toast.LENGTH_SHORT).show();
                    else if (Application.sharedPreferences.getString("Image", null) == null)
                        Toast.makeText(activity, getString(R.string.toast_image) + getString(hasMagnetCardSelected ? R.string.the_magnet_card : R.string.the_voucher), Toast.LENGTH_SHORT).show();
                    else
                        moveToNextFragment(item);

                } catch (Exception e) {

                    Toast.makeText(activity, getString(R.string.toast_exp_date) + getString(hasMagnetCardSelected ?
                            R.string.toast_exp_date_magnet_card : R.string.toast_exp_date_voucher), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                sendUploadRequest();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null)
            clearData();
    }

    private void clearData() {

        phase.setText(getString(R.string.main_upload_vouchers_phase, 1));

        progressBar.setVisibility(View.VISIBLE);

        fragmentTitle.setVisibility(View.VISIBLE);

        viewPager.setCurrentItem(4);

        ((RadioGroup) activity.findViewById(R.id.uploadTypeRadioGroup)).clearCheck();

        ((AppCompatTextView) activity.findViewById(R.id.chosenBrand)).setText("");

        int[] ids = new int[]{R.id.value, R.id.expDate, R.id.barcode, R.id.cvv};

        for (int id : ids)
            ((CleanEditText) activity.findViewById(id)).setText("");

        ((CircularSeekBar) activity.findViewById(R.id.circularSeekBar)).setProgress(10);

        next.setText(getString(R.string.main_upload_vouchers_next));

        Application.sharedPreferences.edit().remove("Image").apply();

        activity.getIntent().putExtra("UploadSucceeded", false);
    }

    private void moveToNextFragment(int currentItem) {

        if (currentItem > 2)
            phase.setText(getString(R.string.main_upload_vouchers_phase, (currentItem - 6) * -1));
        else {

            fragmentTitle.setVisibility(View.GONE);

            next.setText(getString(R.string.main_upload_vouchers_finish));
        }

        viewPager.setCurrentItem(currentItem - 1);
    }

    public void moveToPreviousFragment(int currentItem) {

        if (currentItem == 1) {

            fragmentTitle.setVisibility(View.VISIBLE);

            next.setText(getString(R.string.main_upload_vouchers_next));
        }

        if (currentItem != 0)
            phase.setText(getString(R.string.main_upload_vouchers_phase, (currentItem - 4) * -1));

        viewPager.setCurrentItem(currentItem + 1);

        next.setVisibility(View.VISIBLE);
    }

    private void sendUploadRequest() {

        DatabaseReference voucherRef = Application.vouchersRef.getRef().push();

        String address = Application.sharedPreferences.getString("Address", "");

        LatLng latLng = GPSTracker.getLatLngFromAddress(activity, address);

        if (latLng != null) {

            Voucher voucher = new Voucher(address,
                    Application.sharedPreferences.getString("Barcode", "0"),
                    Application.sharedPreferences.getString("Brand", ""),
                    Application.sharedPreferences.getString("Cvv", "0"),
                    Application.sharedPreferences.getInt("Discount", 0),
                    Application.sharedPreferences.getString("ExpDate", ""),
                    false,
                    voucherRef.getKey(),
                    new RealLocation(latLng.latitude + "", latLng.longitude + ""),
                    Application.sharedPreferences.getInt("Price", 0),
                    "pending",
                    Application.sharedPreferences.getString("VoucherType", ""),
                    Application.sharedPreferences.getInt("Value", 0));

            voucherRef.setValue(voucher);

            ArrayList<String> vouchers = LoadingPage.user.getMyVouchers();

            vouchers.add(voucher.getId());

            LoadingPage.user.setMyVouchers(vouchers);

            activity.getIntent().putExtra("UploadSucceeded", true);

            new Dialog(activity, "UploadVouchers");

            ((BottomNavigationView) activity.findViewById(R.id.fragmentsBar)).setSelectedItemId(R.id.feed);
        }
    }

    private void setupUploadVouchers() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                double progress;

                int fragmentTitleText;

                switch (position) {

                    case 4:
                        progress = 4;
                        fragmentTitleText = R.string.show_by_voucher_type;
                        break;

                    case 3:
                        progress = 2;
                        fragmentTitleText = R.string.ticket_voucher_worth;
                        break;

                    case 2:
                        progress = 4 / 3f;
                        fragmentTitleText = R.string.voucher_info_title;
                        break;

                    default:
                        progress = 1;
                        fragmentTitleText = R.string.app_name;
                        break;
                }

                Animation animation = new ProgressBarAnimation(progressBar.getProgress(), 100 / progress);

                animation.setDuration(1000);

                progressBar.startAnimation(animation);

                fragmentTitle.setText(getString(fragmentTitleText));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {

                    case 0:
                        return EditAddress.newInstance();
                    case 1:
                        return Success.newInstance();
                    case 2:
                        return VoucherDetails.newInstance();
                    case 3:
                        return VoucherWorth.newInstance();
                    default:
                        return VoucherType.newInstance();
                }
            }

            @Override
            public int getCount() {

                return 5;
            }
        });

        viewPager.setCurrentItem(4);

        viewPager.setOffscreenPageLimit(5);
    }

    private class ProgressBarAnimation extends Animation {

        final int from;
        final double to;

        ProgressBarAnimation(int from, double to) {

            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            super.applyTransformation(interpolatedTime, t);

            progressBar.setProgress((int) (from + (to - from) * interpolatedTime));
        }
    }
}
