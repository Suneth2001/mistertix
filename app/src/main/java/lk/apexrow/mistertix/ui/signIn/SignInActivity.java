package lk.apexrow.mistertix.ui.signIn;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lk.apexrow.mistertix.MainActivity;
import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.User;
import lk.apexrow.mistertix.ui.admin.AdminActivity;
import lk.apexrow.mistertix.ui.signUp.Sign_Up_Activity;

public class SignInActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 100;
    private static final String TAG = "SignInActivity";
    private EditText email, password;
    private SharedPreferences sharedPreferences;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth auth;
    private String GoogleUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId("1077682158948-lmkgomu6tl026v255mpu5f2sl7q09o9j.apps.googleusercontent.com")
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();

        email = findViewById(R.id.emailPhone);
        password = findViewById(R.id.password_login);
        Button loginbtn = findViewById(R.id.loginButton);
        TextView signUpText = findViewById(R.id.sign_in_register_text);
        ImageButton googleLogin = findViewById(R.id.imageButtonGoogle);


        sharedPreferences = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);

        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, Sign_Up_Activity.class));
            finish();
        });

        loginbtn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            if (validateInputs(emailInput, passwordInput)) {
                checkLogin(emailInput, passwordInput);
            }
        });

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAuthentication();
            }
        });
    }

    private boolean validateInputs(String emailInput, String passwordInput) {
        if (emailInput.isEmpty()) {
            email.setError("Email cannot be empty");
            return false;
        }
        if (passwordInput.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    private void checkLogin(String emailInput, String passwordInput) {
        String userJson = sharedPreferences.getString("user", "");

        if (!userJson.isEmpty()) {
            User storedUser = new Gson().fromJson(userJson, new TypeToken<User>() {}.getType());
            if (storedUser != null && storedUser.getEmail().equals(emailInput) && storedUser.getPassword().equals(passwordInput)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else if (emailInput.equals("admin@gmail.com") && passwordInput.equals("admin")) {
            SharedPreferences sharedPreferences1 = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("userEmailid", emailInput);
            editor.apply();
            Intent intent = new Intent(SignInActivity.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {

            Toast.makeText(this, "No user found. Please sign up.", Toast.LENGTH_SHORT).show();
        }
    }

//    private void loadFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.userSigned_fragment_design, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    private void googleAuthentication() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getMessage());
                    }
                })
                .addOnFailureListener(this, e -> Log.e(TAG, "Google Sign-In Failed: " + e.getMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    authenticateWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In Error: " + e.getMessage());
            }
        }
    }

    private void authenticateWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Log.i(TAG, "Google Sign-In Success: " + user.getEmail());

                            // Extract user details
                            String emailInput = user.getEmail();
                            String name = user.getDisplayName();
                            String firstName = "", lastName = "";

                            if (name != null && name.contains(" ")) {
                                firstName = name.split(" ")[0];
                                lastName = name.split(" ")[1];
                            } else {
                                firstName = name != null ? name : "Unknown";
                            }

                            // Initialize other fields properly
                            String mobileInput = "";
                            String dobInput = "";
                            String passwordInput = "";
                            String title = "";
                            String countryName = "";

                            // Create user object
                            User googleUser = new User(firstName, lastName, emailInput, countryName, mobileInput, dobInput, passwordInput, title);

                            // Save to SharedPreferences
                            SharedPreferences sharedPreferences2 = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            String userJson = new Gson().toJson(googleUser);
                            editor.putString("user", userJson);
                            editor.putString("googleUserEmail", emailInput); // Store email for comparison
                            editor.apply();

                            // Navigate to MainActivity directly
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();  // Finish SignInActivity so the user can't go back to login
                        }
                    } else {
                        Log.e(TAG, "Google Authentication Failed", task.getException());
                        Toast.makeText(SignInActivity.this, "Google Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
