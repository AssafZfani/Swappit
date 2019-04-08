package talaviassaf.swappit.fragments.TicketFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.activities.VoucherInfo;
import talaviassaf.swappit.models.Brand;
import talaviassaf.swappit.models.Voucher;

public class Ticket extends Fragment {

    public static Ticket newInstance() {

        return new Ticket();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.ticket_voucher, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();

        View view = getView();

        Voucher voucher;

        if (view != null) {

            voucher = (Voucher) ((View) view.getParent()).getTag();

            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.setBrand(Brand.getBrandByName(voucher.getBrand()));

            viewHolder.setDiscount(voucher.getDiscount());

            viewHolder.setDistance(voucher.getDistance());

            String id = voucher.getId();

            viewHolder.setLayout(voucher.isFirmProperty(), LoadingPage.user.isVoucherExistsInCart(id), id);

            viewHolder.setPrice(voucher.getPrice());

            viewHolder.setValue(voucher.getValue());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final Activity activity;
        private final View view;
        @BindView(R.id.brand)
        AppCompatImageView brandImageView;
        @BindView(R.id.cart)
        View cart;
        @BindView(R.id.deliveryTag)
        View deliveryTag;
        @BindView(R.id.discount)
        AppCompatTextView discountTextView;
        @BindView(R.id.distance)
        AppCompatTextView distanceTextView;
        @BindView(R.id.mapContainer)
        View mapContainer;
        @BindView(R.id.precious)
        View precious;
        @BindView(R.id.price)
        AppCompatTextView priceTextView;
        @BindView(R.id.viewMore)
        View viewMore;
        @BindView(R.id.voucherValue)
        AppCompatTextView voucherValue;
        @BindView(R.id.voucherWorth)
        AppCompatTextView voucherWorth;

        ViewHolder(View view) {

            super(view);

            ButterKnife.bind(this, view);

            this.activity = (Activity) view.getContext();

            this.view = view;
        }

        void setBrand(Brand brand) {

            final String strip = brand.getStrip(), name = brand.getBrand();

            Glide.with(activity).asBitmap().load(strip).apply(new RequestOptions()
                    .placeholder(R.drawable.place_holder2)).listener(new RequestListener<Bitmap>() {

                @Override
                public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                    if (Bitmap.createScaledBitmap(bitmap, 1, 1, true).getPixel(0, 0) == Color.WHITE)
                        ((AppCompatTextView) viewMore).setTextColor(Color.BLACK);

                    return false;
                }

            }).into(brandImageView);

            if (activity instanceof VoucherInfo) {

                ButterKnife.apply(mapContainer, HomePage.Appear);

                viewMore.setVisibility(View.GONE);

                precious.setBackgroundResource(LoadingPage.user.getPreciousBrands().contains(name) ?
                        R.drawable.precious1 : R.drawable.precious2);

                precious.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!LoadingPage.user.getPreciousBrands().contains(name))
                            LoadingPage.user.getPreciousBrands().add(name);
                        else
                            LoadingPage.user.getPreciousBrands().remove(name);

                        LoadingPage.user.setPreciousBrands(LoadingPage.user.getPreciousBrands());

                        precious.setBackgroundResource(LoadingPage.user.getPreciousBrands().contains(name)
                                ? R.drawable.precious1 : R.drawable.precious2);

                        activity.setResult(Activity.RESULT_CANCELED);
                    }
                });
            } else {
                if (((HomePage) activity).getCurrentDisplayedFragment() == 3)
                    viewMore.setVisibility(View.GONE);
                else
                    viewMore.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ((AppCompatEditText) activity.findViewById(R.id.feedSearchBrands)).setText(name);
                        }
                    });
            }
        }

        void setDiscount(int discount) {

            discountTextView.setText(activity.getString(R.string.ticket_voucher_percent_off, discount));
        }

        void setDistance(double distance) {

            distanceTextView.setText(distance > 1000 ? new DecimalFormat("#.#").format
                    (distance / 1000) + activity.getString(R.string.gps_tracker_distance_kilometer) : (int) distance + activity.getString(R.string.gps_tracker_distance_meter));
        }

        void setLayout(final boolean isFirmProperty, boolean isExistInCart, final String voucherId) {

            boolean isCartFragmentVisible = activity instanceof HomePage && ((HomePage) activity).getCurrentDisplayedFragment() == 3;

            if (isFirmProperty)
                cart.setBackgroundResource(isExistInCart ? isCartFragmentVisible ? R.drawable.remove_from_cart2 :
                        R.drawable.cart2 : R.drawable.cart1);

            if (isCartFragmentVisible) {

                cart.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        LoadingPage.user.deleteVoucherFromCart(voucherId);

                        HomePage homePage = (HomePage) activity;

                        homePage.updateBadges();

                        homePage.fragments[3].setUserVisibleHint(true);

                        Toast.makeText(activity, activity.getString(R.string.toast_voucher_removed_from_cart), Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (activity instanceof HomePage) {

                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        activity.startActivityForResult(new Intent(activity, VoucherInfo.class)
                                .putExtra("VoucherId", voucherId), 0);
                    }
                });
            }


            cart.setVisibility(isFirmProperty ? View.VISIBLE : View.GONE);

            deliveryTag.setVisibility(!isCartFragmentVisible && isFirmProperty ? View.VISIBLE : View.GONE);
        }

        void setPrice(int price) {

            priceTextView.setText(activity.getString(R.string.ticket_voucher_price, price));
        }

        void setValue(int value) {

            voucherValue.setText(new StringBuilder().append(value).append(" ₪"));

            voucherWorth.setText(activity.getString(R.string.ticket_voucher_worth));
        }
    }
}
