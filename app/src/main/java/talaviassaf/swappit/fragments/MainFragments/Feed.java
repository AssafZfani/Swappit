package talaviassaf.swappit.fragments.MainFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.activities.ShowBy;
import talaviassaf.swappit.models.Brand;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Voucher;
import talaviassaf.swappit.services.InitDistance;
import talaviassaf.swappit.utils.VouchersAdapter;

public class Feed extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @BindView(R.id.feedSearchBrands)
    CleanEditText searchBrands;
    @BindView(R.id.sortBy)
    AppCompatSpinner sortBy;
    @BindView(R.id.clearResults)
    View clearResults;
    @BindView(R.id.locationMessage)
    View locationMessage;
    @BindView(R.id.noResultsMessage)
    AppCompatTextView noResultsMessage;
    @BindView(R.id.feedSwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feedRecyclerView)
    RecyclerView recyclerView;
    @BindArray(R.array.sortBy)
    String[] sortByEntries;

    private Activity activity;
    private Location location;
    private VouchersAdapter vouchersAdapter;
    private ArrayList<Voucher> vouchers;
    private boolean hasFiltered, hasTouched;

    public static Feed newInstance() {

        return new Feed();
    }

    @Override
    public void onClick(View view) {

        hasFiltered = false;

        view.setVisibility(View.GONE);

        onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_feed, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        setupFeed();

        return view;
    }

    @OnClick(R.id.showBy)
    public void showBy() {

        startActivityForResult(new Intent(activity, ShowBy.class).putExtra("CurrentLocation", location), 1);
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
            }

        }, 1500);

        if ((location = ((HomePage) activity).location) != null) {

            activity.startService(new Intent(activity, InitDistance.class).putExtra("CurrentLocation", location));

            refreshList();
        }

        locationMessage.setVisibility(location == null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (!hasFiltered) {

            vouchersAdapter = null;

            recyclerView.setLayoutManager(null);
        }

        onRefresh();

        if (!searchBrands.getText().toString().isEmpty())
            searchBrands.setText("");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null) {

            setTitleBySortByPosition(sortBy.getSelectedItemPosition());

            onResume();
        }
    }

    public void filterList(final String address, final ArrayList<String> vouchersTypes, final ArrayList<Integer> categories) {

        if (hasFiltered = address != null || vouchersTypes != null || categories != null) {

            clearResults.setVisibility(View.VISIBLE);

            Application.vouchersRef.orderByChild(getChildFromPosition(sortBy.getSelectedItemPosition()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            vouchers = null;

                            vouchersAdapter = null;

                            recyclerView.setAdapter(vouchersAdapter = new VouchersAdapter(vouchers = new ArrayList<>()));

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Voucher voucher = snapshot.getValue(Voucher.class);

                                if (LoadingPage.user.areNotThereDeletedVouchers() || (voucher != null &&
                                        !LoadingPage.user.getDeletedVouchers().contains(voucher.getId())))
                                    if (voucher != null && ((address != null && voucher.getAddress().endsWith(address)) ||
                                            (vouchersTypes != null && vouchersTypes.contains(voucher.getType())))) {

                                        if (categories == null)
                                            vouchersAdapter.insert(voucher);
                                        else {

                                            Brand brand = Brand.getBrandByName(voucher.getBrand());

                                            if (brand != null) {

                                                for (int category : categories)
                                                    for (int brandCategory : brand.getTypes())
                                                        if (category == brandCategory) {

                                                            vouchersAdapter.insert(voucher);
                                                            return;
                                                        }
                                            }
                                        }
                                    }
                            }

                            vouchers = vouchersAdapter.getVouchersList();

                            setMessageVisibility();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private String getChildFromPosition(int position) {

        switch (position) {

            case 3:
                return "discount";
            case 2:
                return "popularityCount";
            case 0:
                return "firmProperty";
        }

        return "distance";
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

        recyclerView.getItemAnimator().setAddDuration(750);

        recyclerView.getItemAnimator().setRemoveDuration(750);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        if (searchBrands.getText().length() == 0 && vouchersAdapter == null)
            setupAdapter(getChildFromPosition(sortBy.getSelectedItemPosition()));
    }

    private void setupAdapter(String child) {

        Application.vouchersRef.orderByChild(child).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                recyclerView.setAdapter(vouchersAdapter = new VouchersAdapter(vouchers = new ArrayList<>()));

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Voucher voucher = snapshot.getValue(Voucher.class);

                    if (LoadingPage.user.areNotThereDeletedVouchers() || (voucher != null &&
                            !LoadingPage.user.getDeletedVouchers().contains(voucher.getId())))
                        vouchersAdapter.insert(voucher);
                }

                vouchers = vouchersAdapter.getVouchersList();

                setMessageVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupFeed() {

        swipeRefreshLayout.setOnRefreshListener(this);

        clearResults.setOnClickListener(this);

        searchBrands.addTextChangedListener(new CleanEditText.CleanTextWatcher(searchBrands) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                super.onTextChanged(s, start, before, count);

                if (s.length() == 0)
                    setupAdapter(getChildFromPosition(sortBy.getSelectedItemPosition()));
                else {

                    ArrayList<Voucher> filteredList = new ArrayList<>();

                    String searchBrand = s.toString().toLowerCase();

                    for (Voucher voucher : vouchers) {

                        Brand brand = Brand.getBrandByName(voucher.getBrand());

                        if (brand != null)
                            if (brand.getBrand().toLowerCase().startsWith(searchBrand) || brand.getHebrew().startsWith(searchBrand))
                                filteredList.add(voucher);
                    }

                    recyclerView.setAdapter(vouchersAdapter = new VouchersAdapter(filteredList));
                }

                setMessageVisibility();
            }
        });

        sortBy.setAdapter(new ArrayAdapter<String>(activity, R.layout.model_sort_by, sortByEntries) {

            @Override
            public int getCount() {

                return 4;
            }
        });

        sortBy.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hasTouched = true;

                v.performClick();

                return false;
            }
        });

        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AppCompatTextView spinnerText = (AppCompatTextView) parent.getChildAt(0);

                if (spinnerText != null) {

                    spinnerText.setBackgroundColor(Color.TRANSPARENT);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        spinnerText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_white, 0);
                    else
                        spinnerText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_white, 0);

                    spinnerText.setTextColor(Color.WHITE);
                }

                if (hasTouched) {

                    searchBrands.setText("");

                    setTitleBySortByPosition(position);

                    setupAdapter(getChildFromPosition(position));

                } else
                    sortBy.setSelection(4);

                hasTouched = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setMessageVisibility() {

        noResultsMessage.setVisibility(vouchersAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        noResultsMessage.setText(hasFiltered ? getString(R.string.main_feed_show_by_message) :
                getString(R.string.main_feed_search_by_message) + searchBrands.getText().toString());
    }

    private void setTitleBySortByPosition(int position) {

        int title = R.string.homepage_main_fragment_title_feed;

        switch (position) {

            case 3:
                title = R.string.main_feed_sort_by_discount;
                break;
            case 2:
                title = R.string.main_feed_sort_by_precious_brands;
                break;
            case 0:
                title = R.string.main_feed_sort_by_firm_property;
                break;
        }

        HomePage.setupToolBar(activity, getString(title));
    }
}
