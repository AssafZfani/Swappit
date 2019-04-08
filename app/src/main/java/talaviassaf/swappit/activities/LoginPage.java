package talaviassaf.swappit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;

public class LoginPage extends HideKeyboard {

    @BindView(R.id.phone)
    AppCompatEditText phoneEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginpage);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.signIn)
    public void signIn() {

        final String phone = phoneEditText.getText().toString();

        if (phone.length() != 10 || !phone.startsWith("05"))
            Toast.makeText(this, getString(R.string.toast_phone) + (phone.isEmpty() ? "" : getString(R.string.toast_correctly1)), Toast.LENGTH_SHORT).show();
        else
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Application.sharedPreferences.edit().putString("PhoneNumber", phone).apply();

                    Toast.makeText(LoginPage.this, getString(R.string.toast_login_succeeded), Toast.LENGTH_LONG).show();
                }
            });

        /*

            PhoneAuthProvider.getInstance().verifyPhoneNumber(phone.replaceFirst("0", "+972"), 60, TimeUnit.SECONDS, this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {

                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            Application.sharedPreferences.edit().putString("PhoneNumber", phone).apply();

                                            Toast.makeText(LoginPage.this, getString(R.string.toast_login_succeeded), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {

                            Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        */
    }

    @OnClick(R.id.support)
    public void support() {

        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.fromParts
                ("mailto", "support@swappitgroup.com", null))
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.login_page_mail_title)), getString(R.string.login_page_send_mail)));
    }
}
