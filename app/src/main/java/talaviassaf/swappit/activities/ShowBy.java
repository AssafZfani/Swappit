package talaviassaf.swappit.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.fragments.BackgroundFragments.Registration;
import talaviassaf.swappit.fragments.ShowByFragments.CategoriesFragment;
import talaviassaf.swappit.fragments.ShowByFragments.LocationFragment;
import talaviassaf.swappit.fragments.ShowByFragments.VoucherTypeFragment;

public class ShowBy extends HideKeyboard {

    public LatLng latLng;

    @BindView(R.id.showByMainLayout)
    View mainLayout;
    @BindView(R.id.showByViewPager)
    ViewPager viewPager;
    @BindView(R.id.showByFragmentsBar)
    RadioGroup fragmentsBar;
    @BindView(R.id.showByFragmentTitle)
    AppCompatTextView fragmentTitle;
    @BindView(R.id.locationSearch)
    AppCompatEditText locationSearch;
    @BindView(R.id.chosenCity)
    AppCompatTextView chosenCity;
    @BindView(R.id.cleanChosenCity)
    View cleanChosenCity;

    private LocationFragment locationFragment;
    private Stack<Integer> stack;

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 2)
            super.onBackPressed();
        else {

            stack.pop();

            int lastElement = stack.lastElement();

            ((AppCompatRadioButton) findViewById(lastElement == 2 ? R.id.location : lastElement == 1
                    ? R.id.voucherType : R.id.category)).setChecked(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_Translucent);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_by);

        ButterKnife.bind(this);

        HomePage.setupToolBar(this, getString(R.string.show_by_title));

        Location location = getIntent().getParcelableExtra("CurrentLocation");

        if (location != null)
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        stack = new Stack<>();

        setupShowBy();
    }

    @OnClick(R.id.cleanChosenCity)
    public void clean() {

        locationSearch.setText("");

        chosenCity.setText("");

        cleanChosenCity.setVisibility(View.GONE);

        locationFragment.map.changeCamera(latLng);

        locationFragment.map.googleMap.clear();
    }

    @OnClick(R.id.filter)
    public void filter() {

        setResult(RESULT_OK, getIntent());

        finish();
    }

    private void setupShowBy() {

        if (!HomePage.isUnregisteredUser)
            Registration.changeProfilePicture(mainLayout, R.id.profilePicture);

        fragmentsBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int fragmentTitleText;

                int item;

                switch (checkedId) {

                    case R.id.category:
                        fragmentTitleText = R.string.show_by_categories;
                        item = 0;
                        break;

                    case R.id.voucherType:
                        fragmentTitleText = R.string.show_by_voucher_type;
                        item = 1;
                        break;

                    default:
                        fragmentTitleText = R.string.show_by_location;
                        item = 2;
                        break;
                }

                fragmentTitle.setText(getString(fragmentTitleText));

                viewPager.setCurrentItem(item);

                if (stack.contains(item))
                    stack.remove(stack.indexOf(item));

                stack.push(item);
            }
        });

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                return position == 2 ? locationFragment = LocationFragment.newInstance() : position == 1 ?
                        VoucherTypeFragment.newInstance() : CategoriesFragment.newInstance();
            }

            @Override
            public int getCount() {

                return 3;
            }
        });

        fragmentsBar.check(R.id.location);
    }
}
