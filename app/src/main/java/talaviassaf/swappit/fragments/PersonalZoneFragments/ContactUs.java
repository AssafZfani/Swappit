package talaviassaf.swappit.fragments.PersonalZoneFragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.HomePage;
import talaviassaf.swappit.activities.LoadingPage;
import talaviassaf.swappit.fragments.BackgroundFragments.Container;
import talaviassaf.swappit.fragments.MainFragments.PersonalZone;
import talaviassaf.swappit.models.CleanEditText;
import talaviassaf.swappit.models.Dialog;

import static android.util.Patterns.EMAIL_ADDRESS;

public class ContactUs extends Fragment {

    @BindView(R.id.fullName)
    CleanEditText fullName;
    @BindView(R.id.phone)
    CleanEditText phone;
    @BindView(R.id.email)
    CleanEditText email;
    @BindView(R.id.subject)
    CleanEditText subject;
    @BindView(R.id.content)
    CleanEditText content;

    private Activity activity;

    public static ContactUs newInstance() {

        return new ContactUs();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_zone_contact_us, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        setupContactUs();

        return view;
    }

    @OnClick(R.id.send)
    public void send() {

        String[] details = {email.getText().toString(), fullName.getText().toString(), phone.getText().toString(),
                subject.getText().toString(), content.getText().toString()};

        if (isValidData(details)) {

            new Dialog(activity, "ContactUs");

            ((Container) ((HomePage) activity).fragments[4]).viewPager.setCurrentItem(0);
        }
    }

    private boolean isValidData(String[] details) {

        if (!EMAIL_ADDRESS.matcher(details[0]).matches())
            Toast.makeText(activity, getString(R.string.toast_email) + (details[0].isEmpty() ? "" : getString(R.string.toast_correctly2)), Toast.LENGTH_SHORT).show();
        else if (details[1].length() < 2)
            Toast.makeText(activity, getString(R.string.toast_full_name) + (details[1].isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else if (details[2].length() != 10 || !details[2].startsWith("0"))
            Toast.makeText(activity, getString(R.string.toast_phone) + (details[2].isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else if (details[3].length() < 5)
            Toast.makeText(activity, getString(R.string.toast_subject) + (details[3].isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else if (details[4].length() < 5)
            Toast.makeText(activity, getString(R.string.toast_content) + (details[4].isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else
            return true;

        return false;
    }

    private void setupContactUs() {

        PersonalZone.setupEditText(fullName, "FullName");
        PersonalZone.setupEditText(phone, "Phone");
        PersonalZone.setupEditText(email, "Email");
        PersonalZone.setupEditText(subject, "Subject");
        PersonalZone.setupEditText(content, "Content");

        fullName.setText(LoadingPage.firebaseUser.getDisplayName());
        phone.setText(Application.sharedPreferences.getString("PhoneNumber", ""));
        email.setText(LoadingPage.firebaseUser.getEmail());
    }
}
