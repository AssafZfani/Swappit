package talaviassaf.swappit.fragments.MainFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
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
import talaviassaf.swappit.fragments.BackgroundFragments.Container;
import talaviassaf.swappit.fragments.BackgroundFragments.EditAddress;
import talaviassaf.swappit.fragments.PurchaseFragments.Cart;
import talaviassaf.swappit.fragments.PurchaseFragments.PaymentSystems;
import talaviassaf.swappit.fragments.PurchaseFragments.Success;
import talaviassaf.swappit.models.Dialog;

import static android.util.Patterns.EMAIL_ADDRESS;

public class PurchaseVouchers extends Fragment {

    @BindView(R.id.purchaseViewPager)
    public ViewPager viewPager;
    public Fragment[] fragments;
    @BindView(R.id.continueToPay)
    AppCompatButton continueToPay;
    private Activity activity;

    public static PurchaseVouchers newInstance() {

        return new PurchaseVouchers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_purchase_vouchers, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        fragments = new Fragment[4];

        setupPurchaseVoucher();

        return view;
    }

    @OnClick(R.id.continueToPay)
    public void continueToPay() {

        int currentItem = viewPager.getCurrentItem();

        switch (currentItem) {

            default:
                moveToNextFragment(currentItem);
                break;
            case 2:
                if (!HomePage.isUnregisteredUser)
                    moveToNextFragment(currentItem);
                else if (isValidData())
                    moveToNextFragment(currentItem);
                break;
            case 1:
                new Dialog(activity, "PurchaseVouchers");

                ((BottomNavigationView) activity.findViewById(R.id.fragmentsBar)).setSelectedItemId(R.id.feed);
                break;
        }

        /*

        LoadingPage.user.emptyCart();

        ((HomePage) activity).updateBadges();

        */
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null) {

            if (viewPager.getCurrentItem() != 3)
                viewPager.setCurrentItem(3);

            ((Cart) fragments[0]).onRefresh();

            continueToPay.setText(getString(R.string.main_purchase_vouchers_continue_for, getString(R.string.main_purchase_vouchers_payment)));

            continueToPay.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidData() {

        int checkedPaymentSystem = ((PaymentSystems) fragments[1]).paymentSystem.getCheckedRadioButtonId();

        if (checkedPaymentSystem == R.id.creditCard) {

            String id = Application.sharedPreferences.getString("Id", "");

            if (id.length() < 9)
                Toast.makeText(activity, getString(R.string.toast_id) + (id.isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
            else {

                String creditNumber = Application.sharedPreferences.getString("CreditNumber", "");

                if (creditNumber.length() < 8)
                    Toast.makeText(activity, getString(R.string.toast_credit_number) +
                            (creditNumber.isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
                else {

                    String creditCvv = Application.sharedPreferences.getString("CreditCvv", "");

                    if (creditCvv.length() < 3)
                        Toast.makeText(activity, getString(R.string.toast_credit_cvv) +
                                (creditCvv.isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
                    else
                        return true;
                }
            }
        } else {

            String paypalEmail = Application.sharedPreferences.getString("PaypalEmail", "");

            if (!EMAIL_ADDRESS.matcher(paypalEmail).matches())
                Toast.makeText(activity, getString(R.string.toast_email) +
                        (paypalEmail.isEmpty() ? "" : getString(R.string.toast_correctly2)), Toast.LENGTH_SHORT).show();
            else if (Application.sharedPreferences.getString("PaypalPassword", "").isEmpty())
                Toast.makeText(activity, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            else
                return true;
        }

        return false;
    }

    private void moveToNextFragment(int currentItem) {

        if (currentItem == 3 && HomePage.isUnregisteredUser)
            continueToPay.setVisibility(View.GONE);
        else if (currentItem == 2) {

            continueToPay.setText(getString(R.string.main_purchase_vouchers_make_a, getString(R.string.main_purchase_vouchers_payment)));

            continueToPay.setVisibility(View.VISIBLE);
        }

        viewPager.setCurrentItem(currentItem - 1);
    }

    public void moveToPreviousFragment(int currentItem) {

        continueToPay.setText(getString(currentItem == 0 ? R.string.main_purchase_vouchers_make_a : R.string.main_purchase_vouchers_continue_for, getString(R.string.main_purchase_vouchers_payment)));

        continueToPay.setVisibility(View.VISIBLE);

        viewPager.setCurrentItem(currentItem + 1);
    }

    private void setupPurchaseVoucher() {

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {

                    case 0:
                        return fragments[3] = EditAddress.newInstance();
                    case 1:
                        return fragments[2] = Success.newInstance();
                    case 2:
                        return fragments[1] = HomePage.isUnregisteredUser ? Container.newInstance(2) : PaymentSystems.newInstance();
                    default:
                        return fragments[0] = Cart.newInstance();
                }
            }

            @Override
            public int getCount() {

                return 4;
            }
        });

        viewPager.setCurrentItem(3);

        viewPager.setOffscreenPageLimit(4);
    }
}
