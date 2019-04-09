package talaviassaf.swappit.fragments.UploadFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.models.CircularSeekBar;
import talaviassaf.swappit.models.CleanEditText;

public class VoucherWorth extends Fragment {

    @BindView(R.id.value)
    CleanEditText value;
    @BindView(R.id.circularSeekBar)
    CircularSeekBar circularSeekBar;
    @BindView(R.id.discount)
    AppCompatTextView discount;
    @BindView(R.id.price)
    AppCompatTextView price;

    public static VoucherWorth newInstance() {

        return new VoucherWorth();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_worth, container, false);

        ButterKnife.bind(this, view);

        setupVoucherWorth();

        return view;
    }

    private void setupVoucherWorth() {

        value.addTextChangedListener(new CleanEditText.CleanTextWatcher(value) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                super.onTextChanged(s, start, before, count);

                String valueText = s.toString();

                if (!valueText.isEmpty()) {

                    int priceText = (100 - circularSeekBar.getProgress()) * Integer.parseInt(valueText) / 100;

                    price.setText(new StringBuilder(NumberFormat.getIntegerInstance().format(priceText) + " ₪"));

                    Application.sharedPreferences.edit().putInt("Value", Integer.parseInt(valueText))
                            .putInt("Discount", circularSeekBar.getProgress()).putInt("Price", priceText).apply();
                } else {

                    price.setText("");

                    Application.sharedPreferences.edit().remove("Value").remove("Discount").remove("Price").apply();
                }
            }
        });

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {

            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {

                discount.setText(new StringBuilder().append(progress).append("%"));

                String valueText = value.getText().toString();

                if (!valueText.isEmpty()) {

                    int priceValue = (100 - progress) * Integer.parseInt(valueText) / 100;

                    price.setText(new StringBuilder(NumberFormat.getIntegerInstance().format(priceValue) + " ₪"));

                    Application.sharedPreferences.edit().putInt("Value", Integer.parseInt(valueText))
                            .putInt("Discount", circularSeekBar.getProgress()).putInt("Price", priceValue).apply();
                } else
                    Application.sharedPreferences.edit().remove("Value").remove("Discount").remove("Price").apply();
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

                if (seekBar.getProgress() < 10) {

                    Toast.makeText(getActivity(), getString(R.string.toast_discount), Toast.LENGTH_SHORT).show();

                    seekBar.setProgress(10);
                }
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        circularSeekBar.setProgress(10);
    }
}
