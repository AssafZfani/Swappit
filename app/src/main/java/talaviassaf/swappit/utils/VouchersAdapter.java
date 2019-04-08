package talaviassaf.swappit.utils;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.fragments.TicketFragments.Contact;
import talaviassaf.swappit.fragments.TicketFragments.Delete;
import talaviassaf.swappit.fragments.TicketFragments.Firm;
import talaviassaf.swappit.fragments.TicketFragments.Ticket;
import talaviassaf.swappit.models.Voucher;

public class VouchersAdapter extends RecyclerView.Adapter<VouchersAdapter.ViewHolder> {

    private final ArrayList<Voucher> vouchersList;

    public VouchersAdapter(ArrayList<Voucher> vouchersList) {

        this.vouchersList = vouchersList;
    }

    public void insert(Voucher voucher) {

        vouchersList.add(voucher);

        notifyItemInserted(0);
    }

    public void remove(String voucherId) {

        Application.vouchersRef.child(voucherId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Voucher voucher = dataSnapshot.getValue(Voucher.class);

                int index = vouchersList.indexOf(voucher);

                if (index != -1)
                    vouchersList.remove(index);

                vouchersList.remove(voucher);

                notifyItemRemoved(index);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {

        return vouchersList.size();
    }

    public ArrayList<Voucher> getVouchersList() {

        return vouchersList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();

        final boolean purchaseFragment = ((HomePage) activity).getCurrentDisplayedFragment() == 3;

        final Voucher voucher = vouchersList.get(position);

        viewHolder.viewPager.setId(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                View.generateViewId() : (int) System.currentTimeMillis());

        viewHolder.viewPager.setTag(voucher);

        viewHolder.viewPager.setAdapter(new FragmentPagerAdapter(activity.getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {

                    case 0:
                        return purchaseFragment ? Ticket.newInstance() : voucher.isFirmProperty() ? Firm.newInstance() : Contact.newInstance();
                    case 1:
                        return Ticket.newInstance();
                    default:
                        return Delete.newInstance();
                }
            }

            @Override
            public int getCount() {

                return purchaseFragment ? 1 : 3;
            }

            @Override
            public float getPageWidth(int position) {

                return position == 2 ? 1 / 3f : 1;
            }
        });

        viewHolder.viewPager.setCurrentItem(1);

        viewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position != 1) {

                    int id = activity.getIntent().getIntExtra("ViewPager", 0);

                    if (id != viewHolder.viewPager.getId()) {

                        if (id != 0) {

                            View view = activity.findViewById(id);

                            if (view != null)
                                ((ViewPager) view).setCurrentItem(1);

                        }

                        activity.getIntent().putExtra("ViewPager", viewHolder.viewPager.getId());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(View.inflate(parent.getContext(), R.layout.model_layouts_container, null));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewPager)
        public ViewPager viewPager;

        ViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
