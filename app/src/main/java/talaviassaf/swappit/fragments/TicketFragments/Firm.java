package talaviassaf.swappit.fragments.TicketFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.models.Voucher;

public class Firm extends Fragment {

    @BindView(R.id.cartActions)
    AppCompatTextView cartActions;

    private Voucher voucher;
    private String voucherId;

    public static Firm newInstance() {

        return new Firm();
    }

    public static void cart(Activity activity, String voucherId, int id) {

        boolean isVoucherExistsInCart = LoadingPage.user.isVoucherExistsInCart(voucherId);

        if (isVoucherExistsInCart)
            LoadingPage.user.deleteVoucherFromCart(voucherId);
        else
            LoadingPage.user.addVoucherToCart(voucherId);

        Toast.makeText(activity, activity.getString(isVoucherExistsInCart ? R.string.toast_voucher_removed_from_cart :
                R.string.toast_voucher_added_to_cart), Toast.LENGTH_SHORT).show();

        isVoucherExistsInCart = !isVoucherExistsInCart;

        if (activity instanceof HomePage) {

            ((HomePage) activity).updateBadges();

            AppCompatTextView cartActions = activity.findViewById(id).findViewById(R.id.cartActions);

            cartActions.setCompoundDrawablesWithIntrinsicBounds(0,
                    isVoucherExistsInCart ? R.drawable.remove_from_cart1 : R.drawable.add_to_cart, 0, 0);

            cartActions.setText(activity.getString(R.string.ticket_firm_cart, activity.getString(isVoucherExistsInCart ? R.string.ticket_firm_remove : R.string.ticket_firm_add)));
        }

        activity.findViewById(id).findViewById(R.id.cart).setBackgroundResource
                (isVoucherExistsInCart ? R.drawable.cart2 : R.drawable.cart1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ticket_firm, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.buy, R.id.share, R.id.cartActions})
    public void firm(View view) {

        Activity activity = getActivity();

        boolean isHomePageActivity = activity instanceof HomePage;

        int id = ((View) view.getParent().getParent()).getId();

        if (isHomePageActivity)
            ((ViewPager) activity.findViewById(id)).setCurrentItem(1);

        switch (view.getId()) {

            case R.id.buy: {
                if (!LoadingPage.user.isVoucherExistsInCart(voucherId))
                    cart(activity, voucherId, id);

                if (isHomePageActivity)
                    ((BottomNavigationView) activity.findViewById(R.id.fragmentsBar)).setSelectedItemId(R.id.purchaseVouchers);

                break;
            }
            case R.id.share:
                Contact.contact(activity, voucher.getBrand(), voucher.getDiscount(), "0509907979", R.id.share);
                break;
            case R.id.cartActions:
                cart(activity, voucherId, id);
                break;
        }
    }

    @Override
    public void onStart() {

        super.onStart();

        View view = getView();

        if (view != null) {

            this.voucher = (Voucher) ((View) view.getParent()).getTag();

            this.voucherId = voucher.getId();

            boolean isVoucherExistsInCart = LoadingPage.user.isVoucherExistsInCart(voucherId);

            cartActions.setCompoundDrawablesWithIntrinsicBounds(0,
                    isVoucherExistsInCart ? R.drawable.remove_from_cart1 : R.drawable.add_to_cart, 0, 0);

            cartActions.setText(getString(R.string.ticket_firm_cart, getString(isVoucherExistsInCart ? R.string.ticket_firm_remove : R.string.ticket_firm_add)));
        }
    }
}
