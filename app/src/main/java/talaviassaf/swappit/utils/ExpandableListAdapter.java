package talaviassaf.swappit.utils;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.models.Voucher;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Activity activity;
    private final boolean isMyVouchers;
    @BindView(R.id.price)
    AppCompatTextView price;
    @BindView(R.id.discount)
    AppCompatTextView discount;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    private ArrayList<String> myVouchers;

    public ExpandableListAdapter(Activity activity, boolean isMyVouchers) {

        this.activity = activity;

        this.isMyVouchers = isMyVouchers;

        if (isMyVouchers)
            this.myVouchers = LoadingPage.user.getMyVouchers();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (isMyVouchers) {

            final View view = View.inflate(activity, R.layout.model_child, null);

            ButterKnife.bind(this, view);

            Application.vouchersRef.child(myVouchers.get(childPosition)).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final Voucher voucher = dataSnapshot.getValue(Voucher.class);

                    if (voucher != null) {

                        price.setText(activity.getString(R.string.personal_zone_my_vouchers_price_after_discount, voucher.getPrice()));

                        int discountVal = voucher.getDiscount();

                        discount.setText(new StringBuilder(discountVal).append("%"));

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                discount.setText(new StringBuilder(progress).append("%"));

                                price.setText(activity.getString(R.string.personal_zone_my_vouchers_price_after_discount, (100 - progress) * voucher.getValue() / 100));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                                if (seekBar.getProgress() < 10) {

                                    Toast.makeText(activity, activity.getString(R.string.toast_discount), Toast.LENGTH_SHORT).show();

                                    seekBar.setProgress(10);
                                }
                            }
                        });

                        seekBar.setProgress(discountVal);

                        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                LoadingPage.user.deleteVoucherFromMyVouchers(voucher.getId());

                                /*

                                Application.vouchersRef.child(voucherId).setValue(null);

                                notifyDataSetChanged();

                                */

                                if (getGroupCount() == 0) {

                                    View parent = (View) view.getParent().getParent();

                                    parent.findViewById(R.id.updateVouchers).setVisibility(View.GONE);

                                    parent.findViewById(R.id.emptyCartMessage1).setVisibility(View.VISIBLE);

                                    parent.findViewById(R.id.emptyCartMessage2).setVisibility(View.VISIBLE);
                                }

                                notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return view;
        }

        View view = View.inflate(activity, android.R.layout.simple_expandable_list_item_1, null);

        switch (groupPosition) {

            case 0:
                return HomePage.isUnregisteredUser ? view : PersonalZone.fillDetails
                        (View.inflate(activity, R.layout.personal_zone_fields, null));
            case 1:
                return view;
            default:
                final View settingsView = View.inflate(activity, R.layout.personal_zone_settings, null);

                settingsView.findViewById(R.id.changeLocale).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        new Dialog(activity, "Soon");
                    }
                });

                ToggleButton notifications, preciousBrands;

                notifications = settingsView.findViewById(R.id.notifications);

                notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Application.sharedPreferences.edit().putBoolean("Notifications", isChecked).apply();
                    }
                });

                notifications.setChecked(Application.sharedPreferences.getBoolean("Notifications", true));

                preciousBrands = settingsView.findViewById(R.id.preciousBrands);

                preciousBrands.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Application.sharedPreferences.edit().putBoolean("PreciousBrands", isChecked).apply();
                    }
                });

                preciousBrands.setChecked(Application.sharedPreferences.getBoolean("PreciousBrands", true));

                return settingsView;
        }
    }

    @Override
    public String getGroup(int groupPosition) {

        return activity.getString(groupPosition == 0 ? R.string.main_personal_zone_personal_details : groupPosition == 1 ? R.string.main_personal_zone_my_vouchers : R.string.main_personal_zone_settings);
    }

    @Override
    public int getGroupCount() {

        return isMyVouchers ? myVouchers.size() : 3;
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(activity).inflate(R.layout.model_group, parent, false);

        convertView.setBackgroundResource(groupPosition == 0 ? R.drawable.top :
                (groupPosition + 1) == getGroupCount() ? R.drawable.bottom : R.color.transparent_white);

        AppCompatButton button = (AppCompatButton) convertView;

        button.setText(getGroup(groupPosition));

        final Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.arrow_black);

        Drawable tempDrawable = !isExpanded ? drawable : new LayerDrawable(new Drawable[]{drawable}) {

            @Override
            public void draw(Canvas canvas) {

                canvas.save();

                if (drawable != null)
                    canvas.rotate(180, drawable.getBounds().width() / 2, drawable.getBounds().height() / 2);

                super.draw(canvas);

                canvas.restore();
            }

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, tempDrawable, null);
        else
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, tempDrawable, null);

        return button;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
