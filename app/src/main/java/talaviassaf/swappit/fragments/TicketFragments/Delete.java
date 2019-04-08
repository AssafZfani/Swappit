package talaviassaf.swappit.fragments.TicketFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.activities.VoucherInfo;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.models.Voucher;
import talaviassaf.swappit.utils.VouchersAdapter;

public class Delete extends Fragment {

    @BindView(R.id.delete)
    View delete;

    public static Delete newInstance() {

        return new Delete();
    }

    public static void delete(final Activity activity) {

        final String voucherId = activity.getIntent().getStringExtra("VoucherId");

        LoadingPage.user.deleteVoucher(voucherId);

        if (activity instanceof HomePage) {

            ((HomePage) activity).updateBadges();

            ((VouchersAdapter) ((RecyclerView) activity.findViewById(R.id.feedRecyclerView)).getAdapter()).remove(voucherId);

        } else {

            activity.setResult(Activity.RESULT_CANCELED);

            activity.finish();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ticket_delete, container, false);

        ButterKnife.bind(this, view);

        final Activity activity = getActivity();

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (activity != null) {

                    ((ViewPager) activity.findViewById(container.getId())).setCurrentItem(1);

                    activity.getIntent().putExtra("VoucherId", ((Voucher) container.getTag()).getId());

                    if (activity instanceof HomePage)
                        ((HomePage) activity).dialog = new Dialog(activity, "Delete");
                    else
                        ((VoucherInfo) activity).dialog = new Dialog(activity, "Delete");
                }
            }
        });

        return view;
    }
}
