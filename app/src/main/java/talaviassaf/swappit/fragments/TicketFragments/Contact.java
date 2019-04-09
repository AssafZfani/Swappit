package talaviassaf.swappit.fragments.TicketFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.models.Voucher;

public class Contact extends Fragment {

    public static Contact newInstance() {

        return new Contact();
    }

    public static void contact(Activity activity, String brand, int discount, String phoneNumber, int id) {

        if (activity instanceof HomePage)
            ((ViewPager) activity.findViewById(id).getParent().getParent()).setCurrentItem(1);

        switch (id) {

            case R.id.call:
                activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
                break;

            case R.id.share:

            case R.id.sms:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.putExtra("exit_on_sent", true);

                shareIntent.setType("text/plain");

                shareIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.ticket_share_message_part1) + brand + activity.getString(R.string.ticket_share_message_part2) + discount + activity.getString(R.string.ticket_share_message_part3));

                activity.startActivity(Intent.createChooser(shareIntent, null));
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ticket_contact, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.call, R.id.share, R.id.sms})
    public void contact(View view) {

        View mainView = getView();

        if (mainView != null) {

            Voucher voucher = ((Voucher) ((View) mainView.getParent()).getTag());

            contact((Activity) view.getContext(), voucher.getBrand(), voucher.getDiscount(), "0509907979", view.getId());
        }
    }
}
