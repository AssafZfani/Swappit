package talaviassaf.swappit.fragments.PurchaseFragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import java.lang.reflect.Field;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.models.CleanEditText;

public class PaymentSystems extends Fragment {

    @BindView(R.id.paymentSystem)
    public RadioGroup paymentSystem;
    @BindView(R.id.viewSwitcher)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.id)
    CleanEditText id;
    @BindView(R.id.creditNumber)
    CleanEditText creditNumber;
    @BindView(R.id.cvv)
    CleanEditText cvv;
    @BindView(R.id.email)
    CleanEditText email;
    @BindView(R.id.password)
    CleanEditText password;
    @BindView(R.id.month)
    NumberPicker month;
    @BindView(R.id.year)
    NumberPicker year;

    public static PaymentSystems newInstance() {

        return new PaymentSystems();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.purchase_payment_systems, container, false);

        ButterKnife.bind(this, view);

        setupPaymentSystems();

        return view;
    }

    private void designNumberPicker(NumberPicker numberPicker) {

        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        for (Field field : numberPicker.getClass().getDeclaredFields()) {

            if (field.getName().equalsIgnoreCase("mSelectionDivider")) {

                field.setAccessible(true);

                try {
                    field.set(numberPicker, new ColorDrawable(Color.parseColor("#EB464C")));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.getName().equalsIgnoreCase("mSelectionDividersDistance")) {

                field.setAccessible(true);

                try {
                    field.set(numberPicker, 100);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < numberPicker.getChildCount(); i++) {

            if (numberPicker.getChildAt(i) instanceof EditText) {

                try {

                    for (Field field : NumberPicker.class.getDeclaredFields()) {

                        if (field.getName().equalsIgnoreCase("mSelectorWheelPaint")) {

                            field.setAccessible(true);

                            ((Paint) field.get(numberPicker)).setColor(Color.WHITE);

                            ((EditText) numberPicker.getChildAt(i)).setTextColor(Color.WHITE);

                            numberPicker.invalidate();
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupEditText(final CleanEditText editText, final String key) {

        editText.addTextChangedListener(new CleanEditText.CleanTextWatcher(editText) {

            @Override
            public void afterTextChanged(Editable s) {

                super.afterTextChanged(s);

                Application.sharedPreferences.edit().putString(key, editText.getText().toString()).apply();
            }
        });
    }

    private void setupPaymentSystems() {

        paymentSystem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.paypal)
                    viewSwitcher.showNext();
                else if (checkedId == R.id.creditCard)
                    viewSwitcher.showPrevious();
            }
        });

        setupEditText(id, "Id");

        setupEditText(creditNumber, "CreditNumber");

        setupEditText(cvv, "CreditCvv");

        setupEditText(email, "PaypalEmail");

        setupEditText(password, "PaypalPassword");

        designNumberPicker(month);

        designNumberPicker(year);

        final Calendar calendar = Calendar.getInstance();

        month.setMaxValue(12);

        month.setMinValue(calendar.get(Calendar.MONTH) + 1);

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                Application.sharedPreferences.edit().putInt("Year", newVal).apply();
            }
        });

        final int currentYear = calendar.get(Calendar.YEAR);

        year.setMaxValue(currentYear + 10);

        year.setMinValue(currentYear);

        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                month.setMinValue(newVal == currentYear ? (calendar.get(Calendar.MONTH) + 1) : 1);

                Application.sharedPreferences.edit().putInt("Year", newVal).apply();
            }
        });
    }
}
