package talaviassaf.swappit.fragments.UploadFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.models.Brand;
import talaviassaf.swappit.models.CleanEditText;

public class VoucherType extends Fragment {

    @BindView(R.id.brandsList)
    CleanEditText brandsList;
    @BindView(R.id.chosenBrand)
    AppCompatTextView chosenBrand;
    @BindView(R.id.cleanChosenBrand)
    View cleanChosenBrand;
    @BindView(R.id.uploadTypeRadioGroup)
    RadioGroup radioGroup;

    public static VoucherType newInstance() {

        return new VoucherType();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_type, container, false);

        ButterKnife.bind(this, view);

        setupVoucherType();

        return view;
    }

    @OnClick(R.id.cleanChosenBrand)
    public void clean() {

        chosenBrand.setText("");
    }

    private void setupVoucherType() {

        brandsList.addTextChangedListener(new CleanEditText.CleanTextWatcher(brandsList) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                super.onTextChanged(s, start, before, count);

                cleanChosenBrand.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

                Activity activity = getActivity();

                if (activity != null) {

                    if (s.length() != 0)
                        brandsList.setAdapter(new ArrayAdapter<>(activity,
                                android.R.layout.simple_list_item_1, Brand.getBrandsList(s.toString())));
                    else
                        cleanChosenBrand.setVisibility(View.GONE);
                }


                String brand = brandsList.getText().toString();

                chosenBrand.setText(brand);

                Application.sharedPreferences.edit().putString("Brand", brand).apply();

                brandsList.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        return false;
                    }
                });
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                String voucherType = null;

                switch (checkedId) {

                    case R.id.scrip:
                        voucherType = "Scrip";
                        break;
                    case R.id.magnetCard:
                        voucherType = "Magnet Card";
                        break;
                    case R.id.paper:
                        voucherType = "Paper";
                        break;
                    case -1:
                        Application.sharedPreferences.edit().remove("VoucherType").apply();
                }

                Application.sharedPreferences.edit().putString("VoucherType", voucherType).apply();
            }
        });
    }
}
