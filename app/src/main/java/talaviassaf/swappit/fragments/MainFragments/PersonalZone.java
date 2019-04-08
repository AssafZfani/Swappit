package talaviassaf.swappit.fragments.MainFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.fragments.BackgroundFragments.Container;
import talaviassaf.swappit.fragments.BackgroundFragments.Registration;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Dialog;
import talaviassaf.swappit.utils.ExpandableListAdapter;

public class PersonalZone extends Fragment {

    @BindView(R.id.personalZoneList)
    ExpandableListView personalZoneList;

    private Activity activity;
    private ViewPager viewPager;

    public static PersonalZone newInstance() {

        return new PersonalZone();
    }

    public static void setupEditText(final CleanEditText editText, final String key) {

        editText.addTextChangedListener(new CleanEditText.CleanTextWatcher(editText) {

            @Override
            public void afterTextChanged(Editable s) {

                super.afterTextChanged(s);

                String text = s.toString();

                switch (key) {

                    case "Address":
                        if (s.length() > 1 && !text.contains(", ") &&
                                !text.equalsIgnoreCase(Application.sharedPreferences.getString("Address", "")))
                            Registration.initAddressAndCity(editText, s.toString());
                        break;
                    case "Barcode":
                    case "Cvv":
                    case "ExpDate":
                        Application.sharedPreferences.edit().putString(key, editText.getText().toString()).apply();
                        break;
                }
            }
        });
    }

    public static View fillDetails(final View view) {

        final Activity activity = (Activity) view.getContext();

        final CleanEditText email, firstName, lastName, address, apartment, postcode;

        email = view.findViewById(R.id.email);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        address = view.findViewById(R.id.address);
        apartment = view.findViewById(R.id.apartment);
        postcode = view.findViewById(R.id.postcode);

        setupEditText(email, "Email");
        setupEditText(firstName, "FirstName");
        setupEditText(lastName, "LastName");
        setupEditText(address, "Address");
        setupEditText(apartment, "Apartment");
        setupEditText(postcode, "Postcode");

        email.setText(LoadingPage.firebaseUser.getEmail());

        String name = LoadingPage.firebaseUser.getDisplayName();

        if (name != null) {

            int index = name.lastIndexOf(' ');

            firstName.setText(name.substring(0, index));

            lastName.setText(name.substring(index + 1, name.length()));
        }

        address.setText(Application.sharedPreferences.getString("Address", ""));

        apartment.setText(Application.sharedPreferences.getString("Apartment", "0"));

        postcode.setText(Application.sharedPreferences.getString("Postcode", "0"));

        final ExpandableListView personalZoneList = ((Activity) view.getContext()).findViewById(R.id.personalZoneList);

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String[] details = {email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                        address.getText().toString(), apartment.getText().toString(), postcode.getText().toString()};

                if (Registration.isValidData(activity, details)) {

                    LoadingPage.firebaseUser.updateEmail(details[0]);

                    Registration.saveData(null, details);

                    Toast.makeText(activity, activity.getString(R.string.toast_personal_details_updated_successfully), Toast.LENGTH_SHORT).show();

                    personalZoneList.collapseGroup(0);
                }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                personalZoneList.collapseGroup(0);
            }
        });

        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_personal_zone, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        viewPager = activity == null ? null : ((Container) ((HomePage) activity).fragments[4]).viewPager;

        setupPersonalZone();

        return view;
    }

    @OnClick({R.id.contactUs, R.id.aboutUs})
    public void options(View view) {

        switch (view.getId()) {

            case R.id.contactUs:
                viewPager.setCurrentItem(1);
                break;
            case R.id.aboutUs:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.swappitgroup.com")));
                break;
        }
    }

    private void setupPersonalZone() {

        personalZoneList.setAdapter(new ExpandableListAdapter(activity, false));

        personalZoneList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                boolean check = false;

                if (!HomePage.isUnregisteredUser) {

                    if (groupPosition == 1)
                        viewPager.setCurrentItem(2);

                } else if (check = groupPosition < 2)
                    new Dialog(activity, groupPosition == 0 ? "PersonalDetails" : "MyVouchers");

                return check || groupPosition == 1;
            }
        });
    }
}
