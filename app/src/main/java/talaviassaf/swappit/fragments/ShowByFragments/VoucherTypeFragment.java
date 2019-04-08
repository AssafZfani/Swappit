package talaviassaf.swappit.fragments.ShowByFragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;

public class VoucherTypeFragment extends Fragment {

    @BindView(R.id.scrip)
    AppCompatButton scrip;

    private Drawable drawable;
    private ArrayList<String> vouchersTypes;

    public static VoucherTypeFragment newInstance() {

        return new VoucherTypeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.showby_voucher_type, container, false);

        ButterKnife.bind(this, view);

        drawable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                scrip.getCompoundDrawablesRelative()[2] : scrip.getCompoundDrawables()[2];

        vouchersTypes = new ArrayList<>();

        vouchersTypes.add("Scrip");
        vouchersTypes.add("Magnet Card");
        vouchersTypes.add("Paper");

        return view;
    }

    @OnClick({R.id.scrip, R.id.magnetCard, R.id.paper})
    public void choose(View view) {

        String voucherType;

        switch (view.getId()) {

            case R.id.scrip:
                voucherType = "Scrip";
                break;

            case R.id.magnetCard:
                voucherType = "Magnet Card";
                break;

            default:
                voucherType = "Paper";
                break;
        }

        if (vouchersTypes.contains(voucherType))
            vouchersTypes.remove(voucherType);
        else
            vouchersTypes.add(voucherType);

        AppCompatButton button = ((AppCompatButton) view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            button.setCompoundDrawablesRelative(null, null, vouchersTypes.contains(voucherType) ? drawable : null, null);
        else
            button.setCompoundDrawables(null, null, vouchersTypes.contains(voucherType) ? drawable : null, null);

        Activity activity = getActivity();

        if (activity != null)
            activity.getIntent().putExtra("VouchersTypes", vouchersTypes);
    }
}
