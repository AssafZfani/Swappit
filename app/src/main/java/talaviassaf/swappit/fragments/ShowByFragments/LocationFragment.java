package talaviassaf.swappit.fragments.ShowByFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.ShowBy;
import talaviassaf.swappit.fragments.TicketFragments.Map;

public class LocationFragment extends Fragment {

    public Map map;

    @BindView(R.id.chosenCity)
    AppCompatTextView chosenCity;

    public static LocationFragment newInstance() {

        return new LocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.showby_location, container, false);

        ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null)
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.mapContainer,
                    map = Map.newInstance(((ShowBy) activity).latLng)).commit();

        return view;
    }

    @OnClick(R.id.cleanChosenCity)
    public void clean() {

        chosenCity.setText("");
    }
}