package talaviassaf.swappit.fragments.PurchaseFragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
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
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.models.Voucher;
import talaviassaf.swappit.utils.VouchersAdapter;

public class Cart extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.cartSwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.cartRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyCartMessage1)
    View emptyCartMessage1;
    @BindView(R.id.emptyCartMessage2)
    View emptyCartMessage2;
    @BindView(R.id.locationMessage)
    View locationMessage;
    @BindView(R.id.cartWorth)
    AppCompatTextView cartWorth;
    @BindView(R.id.savedSum)
    AppCompatTextView savedSum;
    @BindView(R.id.priceSum)
    AppCompatTextView priceSum;

    private Activity activity;
    private VouchersAdapter vouchersAdapter;

    public static Cart newInstance() {

        return new Cart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.purchase_cart, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        return view;
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);

        Location location = ((HomePage) activity).location;

        if (location != null)
            refreshList();

        locationMessage.setVisibility(location == null ? View.VISIBLE : View.GONE);

        if (locationMessage.isShown()) {

            emptyCartMessage1.setVisibility(View.GONE);
            emptyCartMessage2.setVisibility(View.GONE);
        }
    }

    private void refreshList() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dx + dy > 0)
                    for (int position = 0; position < recyclerView.getChildCount(); position++)
                        ((VouchersAdapter.ViewHolder) recyclerView.getChildViewHolder
                                (recyclerView.getChildAt(position))).viewPager.setCurrentItem(1);
            }
        });

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {

            @Override
            public void onChildViewAttachedToWindow(View view) {

                boolean showEmptyCartMessage = vouchersAdapter.getItemCount() == 0;

                emptyCartMessage1.setVisibility(showEmptyCartMessage ? View.VISIBLE : View.GONE);

                emptyCartMessage2.setVisibility(showEmptyCartMessage ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

                boolean showEmptyCartMessage = vouchersAdapter.getItemCount() == 0;

                emptyCartMessage1.setVisibility(showEmptyCartMessage ? View.VISIBLE : View.GONE);

                emptyCartMessage2.setVisibility(showEmptyCartMessage ? View.VISIBLE : View.GONE);
            }
        });

        recyclerView.getItemAnimator().setAddDuration(750);

        recyclerView.getItemAnimator().setRemoveDuration(750);

        recyclerView.setAdapter(vouchersAdapter = new VouchersAdapter(new ArrayList<Voucher>()));

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        setupCart();
    }

    private void setupCart() {

        swipeRefreshLayout.setOnRefreshListener(this);

        Application.vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int cartWorthNum = 0, priceSumNum = 0;

                for (String voucherId : LoadingPage.user.getCartVouchers()) {

                    Voucher voucher = dataSnapshot.child(voucherId).getValue(Voucher.class);

                    if (voucher != null) {

                        cartWorthNum += voucher.getValue();

                        priceSumNum += voucher.getPrice();

                        vouchersAdapter.insert(voucher);
                    }
                }

                cartWorth.setText(new StringBuilder().append(cartWorthNum).append(" ₪"));

                String savedSumText = (cartWorthNum - priceSumNum) + " " + "₪";

                savedSum.setText(savedSumText);

                String priceSumText = priceSumNum + " ₪";

                priceSum.setText(priceSumText);

                Application.sharedPreferences.edit().putString("SavedSum", savedSumText).putString("PriceSum", priceSumText).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
