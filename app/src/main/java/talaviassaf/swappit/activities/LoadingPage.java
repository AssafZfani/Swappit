package talaviassaf.swappit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.R;
import talaviassaf.swappit.models.User;
import talaviassaf.swappit.utils.GlideApp;

public class LoadingPage extends AppCompatActivity {

    public static FirebaseUser firebaseUser;
    public static User user;

    @BindView(R.id.bg)
    AppCompatImageView bg;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loadingpage);

        ButterKnife.bind(this);

        GlideApp.with(this).load(Uri.parse("file:///assets/animation.gif")).into(bg);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {

        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null && user == null) {

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren())
                                user = dataSnapshot.getValue(User.class);
                            else
                                reference.setValue(user = new User(null, null, null, null));

                            if (user != null)
                                user.cleanListsFromIrrelevantIds();

                            startActivity(new Intent(LoadingPage.this, HomePage.class));

                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else
                    startActivity(new Intent(LoadingPage.this, LoginPage.class));
            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();

        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
