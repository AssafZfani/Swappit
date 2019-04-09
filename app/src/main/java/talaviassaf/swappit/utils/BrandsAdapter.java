package talaviassaf.swappit.utils;

import android.app.Activity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.models.Brand;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> implements Filterable {

    public static ArrayList<Brand> fullList;
    private final Activity activity;
    private final ArrayList<String> preciousBrands;
    private ArrayList<Brand> brandsList, filteredList;

    public BrandsAdapter(Activity activity) {

        this.activity = activity;

        brandsList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Brands").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fullList = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren())
                    fullList.add(data.getValue(Brand.class));

                brandsList.clear();

                for (Brand brand : fullList) {

                    brandsList.add(brand);

                    notifyItemInserted(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        preciousBrands = new ArrayList<>(LoadingPage.user.getPreciousBrands());
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {

                    filteredList = null;

                    results.count = brandsList.size();
                    results.values = brandsList;

                } else {

                    ArrayList<Brand> list = new ArrayList<>();

                    for (Brand brand : brandsList) {

                        String str = constraint.toString().toLowerCase();

                        if (brand != null)
                            if (brand.getBrand().toLowerCase().startsWith(str) || brand.getHebrew().startsWith(str))
                                list.add(brand);
                    }

                    results.count = list.size();
                    results.values = list;
                }

                return results;
            }

            @SuppressWarnings("unchecked")

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (constraint == null || constraint.length() == 0)
                    brandsList = (ArrayList<Brand>) results.values;
                else
                    filteredList = (ArrayList<Brand>) results.values;

                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {

        return filteredList == null ? brandsList.size() : filteredList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        Brand brand = filteredList == null ? brandsList.get(position) : filteredList.get(position);

        Glide.with(activity).load(brand.getImage()).apply(new RequestOptions().placeholder(R.drawable.place_holder1)).into(viewHolder.brand);

        final String brandName = brand.getBrand();

        viewHolder.precious.setVisibility(preciousBrands.contains(brandName) ? View.VISIBLE : View.INVISIBLE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                viewHolder.precious.setVisibility(viewHolder.precious.isShown() ? View.INVISIBLE : View.VISIBLE);

                if (!preciousBrands.contains(brandName))
                    preciousBrands.add(brandName);
                else
                    preciousBrands.remove(brandName);

                activity.getIntent().putExtra("PreciousBrands", preciousBrands);

                if (!preciousBrands.isEmpty())
                    activity.findViewById(R.id.next).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(View.inflate(parent.getContext(), R.layout.model_precious_brand, null));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.brand)
        AppCompatImageView brand;
        @BindView(R.id.precious)
        AppCompatImageView precious;

        ViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
