package talaviassaf.swappit.fragments.UploadFragments;

import android.app.DatePickerDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.models.CleanEditText;

public class VoucherDetails extends Fragment {

    @BindView(R.id.expDate)
    CleanEditText expDate;
    @BindView(R.id.barcode)
    CleanEditText barcode;
    @BindView(R.id.cvv)
    CleanEditText cvv;
    @BindView(R.id.voucherCapture)
    View voucherCapture;

    public static VoucherDetails newInstance() {

        return new VoucherDetails();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_details, container, false);

        ButterKnife.bind(this, view);

        setupVoucherDetails();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && getView() != null) {

            boolean hasMagnetCardSelected = Application.sharedPreferences.getString("VoucherType", "").equalsIgnoreCase("Magnet Card");

            expDate.setHint(getString(R.string.upload_details_exp_date) + getString(hasMagnetCardSelected ? R.string.the_magnet_card : R.string.the_voucher));

            barcode.setHint(getString(hasMagnetCardSelected ? R.string.upload_details_barcode_magnet_card : R.string.upload_details_barcode_voucher));

            cvv.setVisibility(hasMagnetCardSelected ? View.VISIBLE : View.GONE);

            Application.sharedPreferences.edit().putString("Cvv", hasMagnetCardSelected ?
                    Application.sharedPreferences.getString("Cvv", "") : "000").apply();
        }
    }

    public void voucherCapture() {

        BitmapDrawable drawable = new BitmapDrawable(getResources(), ThumbnailUtils.extractThumbnail
                (BitmapFactory.decodeFile(Application.sharedPreferences.getString("Image", null)), 150, 150));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            voucherCapture.setBackground(drawable);
        else
            voucherCapture.setBackgroundDrawable(drawable);
    }

    private void setupVoucherDetails() {

        final Calendar calendar = Calendar.getInstance();

        expDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                expDate.setText(new StringBuilder().append(dayOfMonth).append("/").append(month + 1).append("/").append(year));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), datePickerDialog);
                datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.dialog_confirm), datePickerDialog);

                datePickerDialog.show();
            }
        });

        PersonalZone.setupEditText(expDate, "ExpDate");
        PersonalZone.setupEditText(barcode, "Barcode");
        PersonalZone.setupEditText(cvv, "Cvv");
    }
}
