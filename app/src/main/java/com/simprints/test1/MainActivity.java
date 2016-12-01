package com.simprints.test1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simprints.test1.models.Person;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActiveAndroid.initialize(this);
        Stetho.initializeWithDefaults(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("Login", null);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("THIS", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("THIS", "onAuthStateChanged:signed_out");
                }

                updateUI(user);
            }
        };

        mDatabase.child("people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Person person = postSnapshot.getValue(Person.class);
                    Log.d("THIS", person.username);
                    person.save();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {

            String personId = String.format(Locale.UK, "Person %s", new Random().nextInt(10 + 1));
            Person user = new Person("Name", "Email");
            mDatabase.child("people").child(personId).setValue(user);

        } else if (i == R.id.email_sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            mAuth.signOut();
            updateUI(null);
        }
    }

    private String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsicHJlbWl1bV9hY2NvdW50Ijp0cnVlfSwidWlkIjoiNzAxODMxZGUtY2VjYi00OWQ2LWI4NzgtMzZhNDIyZDgzZWVkIiwiaWF0IjoxNDgwNTUzOTA4LCJleHAiOjE0ODA1NTc1MDgsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwiaXNzIjoiZmlyZWJhc2VhdXRoQHRlc3QtZjliY2IuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZWF1dGhAdGVzdC1mOWJjYi5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSJ9.MudEtgIO_hri_oTLjE0SPDez14GG-k35AWYH707ET0JEA31tUOAVgAimM55E2ioveBNmpsMJ4Uxv3airURhR9UJQkzGYHnj-Sr76SDtvVf-8kH9pS_aMlP3Kl_IcXh9LpuWcByLfiGREkfhmNpqnNT7uZtVAh_GWeV-8b9baAh78Y-FSOFG5xfZzZ7mLuAhBkKt8QO-DUNlmQGdw-ohEaJ22APClOSy16tah7WxDzZJ6XUjGrCcxGF-518SGZssfrbU_j9qXBnCBhWb1egTB-LKVDBxBBvsJcleH0dhn79jQdg6fLgQvAWY6iY5BJu1m-wvtAMw05O0tPq9m91BSGw";

    private void signIn() {
        Log.d("THIS", "signIn");

        // [START sign_in_with_email]
        mAuth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("THIS", "signInWithEmail:failed", task.getException());
                    Toast.makeText(getApplicationContext(), R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                    mStatusTextView.setText(R.string.auth_failed);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }
}
