package talaviassaf.swappit.fragments.ShowByFragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.models.FlowLayout;

public class CategoriesFragment extends Fragment {

    @BindView(R.id.flowLayout)
    FlowLayout flowLayout;
    @BindArray(R.array.categories)
    String[] categoriesEntries;

    private ArrayList<String> categories;

    public static CategoriesFragment newInstance() {

        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.showby_categories, container, false);

        ButterKnife.bind(this, view);

        categories = new ArrayList<>();

        setupCategory();

        return view;
    }

    private void setupCategory() {

        final Activity activity = getActivity();

        if (activity != null) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.model_category, categoriesEntries) {

                @NonNull

                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                    CheckBox checkBox = (CheckBox) (convertView == null ? View.inflate(activity, R.layout.model_category, null) : convertView);

                    final String category = categoriesEntries[position];

                    checkBox.setText(category);

                    checkBox.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (categories.contains(category))
                                categories.remove(category);
                            else
                                categories.add(category);

                            activity.getIntent().putExtra("Categories", categories);
                        }
                    });

                    return checkBox;
                }
            };

            for (int i = 0; i < adapter.getCount(); i++)
                flowLayout.addView(adapter.getView(i, null, flowLayout));
        }
    }
}