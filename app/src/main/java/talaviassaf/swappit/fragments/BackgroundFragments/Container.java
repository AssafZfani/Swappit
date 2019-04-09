package talaviassaf.swappit.fragments.BackgroundFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.fragments.PersonalZoneFragments.ContactUs;
import talaviassaf.swappit.fragments.PersonalZoneFragments.MyVouchers;

public class Container extends Fragment {

    @BindView(R.id.containerViewPager)
    public ViewPager viewPager;

    private int fragmentsNumber;

    public static Container newInstance(int fragmentsNumber) {

        Container container = new Container();

        Bundle bundle = new Bundle();

        bundle.putInt("FragmentsNumber", fragmentsNumber);

        container.setArguments(bundle);

        return container;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bg_container, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();

        if (args != null)
            fragmentsNumber = args.getInt("FragmentsNumber");

        setupContainer();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null) {

            viewPager.setCurrentItem(0);

            if (fragmentsNumber > 2)
                resetLayout();
        }
    }

    private void collapseAllGroups(View view) {

        ExpandableListView expandableListView = (ExpandableListView) view;

        if (expandableListView != null)
            for (int i = 0; i < expandableListView.getCount(); i++)
                expandableListView.collapseGroup(i);
    }

    private void resetLayout() {

        View view = getView();

        if (view != null) {

            Registration.changeProfilePicture(view, R.id.pzProfilePicture);

            collapseAllGroups(view.findViewById(R.id.personalZoneList));

            collapseAllGroups(view.findViewById(R.id.myVouchersList));
        }
    }

    private void setupContainer() {

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                return fragmentsNumber == 2 ? position == 0 ? Login.newInstance() : Registration.newInstance() :
                        position == 0 ? PersonalZone.newInstance() : position == 1 ? ContactUs.newInstance() : MyVouchers.newInstance();
            }

            @Override
            public int getCount() {

                return fragmentsNumber;
            }
        });

        if (fragmentsNumber > 2) {

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    resetLayout();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}
