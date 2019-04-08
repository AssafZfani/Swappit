package talaviassaf.swappit.fragments.MainFragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.utils.BrandsAdapter;

public class PreciousBrands extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.brandsSearchBrands)
    CleanEditText searchBrands;
    @BindView(R.id.noResultsMessage)
    AppCompatTextView noResultsMessage;
    @BindView(R.id.brandsSwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.brandsRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.next)
    View next;

    private Activity activity;
    private BrandsAdapter adapter;

    public static PreciousBrands newInstance() {

        return new PreciousBrands();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_precious_brands, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        setupPreciousBrands();

        onRefresh();

        return view;
    }

    @OnClick(R.id.next)
    public void next() {

        if (activity != null) {

            LoadingPage.user.setPreciousBrands(activity.getIntent().getStringArrayListExtra("PreciousBrands"));

            if (!LoadingPage.user.getPreciousBrands().isEmpty())
                new Dialog(activity, "PreciousBrands");

            ((BottomNavigationView) activity.findViewById(R.id.fragmentsBar)).setSelectedItemId(R.id.feed);
        }
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);

        refreshList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null) {

            if (searchBrands.getText().length() != 0)
                searchBrands.setText("");

            next.setVisibility(LoadingPage.user.getPreciousBrands().isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void refreshList() {

        recyclerView.getItemAnimator().setAddDuration(750);

        recyclerView.setAdapter(adapter = new BrandsAdapter(activity));

        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2) {

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.getMessage();
                }
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {

                super.onChanged();

                noResultsMessage.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                noResultsMessage.setText(getString(R.string.main_precious_brands_message, searchBrands.getText().toString()));
            }
        });
    }

    private void setupPreciousBrands() {

        swipeRefreshLayout.setOnRefreshListener(this);

        searchBrands.addTextChangedListener(new CleanEditText.CleanTextWatcher(searchBrands) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                super.onTextChanged(s, start, before, count);

                adapter.getFilter().filter(s);
            }
        });
    }
}
