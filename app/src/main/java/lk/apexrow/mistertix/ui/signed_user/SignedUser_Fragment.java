package lk.apexrow.mistertix.ui.signed_user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.util.concurrent.Executor;

import lk.apexrow.mistertix.MainActivity;
import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.SharedPrefManager;
import lk.apexrow.mistertix.model.User;
import lk.apexrow.mistertix.ui.contacct_us.Contat_Us_Activity;
import lk.apexrow.mistertix.ui.map.MapActivity;
import lk.apexrow.mistertix.ui.profile.MyAccount;

public class SignedUser_Fragment extends Fragment {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signed_user_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BiometricManager biometricManager = BiometricManager.from(requireContext());
        Switch fingerprintSwitch = view.findViewById(R.id.faceId_switch);

        fingerprintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Toast.makeText(requireContext(), "Fingerprint authentication is available!", Toast.LENGTH_SHORT).show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(requireContext(), "No fingerprint sensor found!", Toast.LENGTH_SHORT).show();
                        fingerprintSwitch.setChecked(false);
                        return;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(requireContext(), "Fingerprint sensor is not working!", Toast.LENGTH_SHORT).show();
                        fingerprintSwitch.setChecked(false);
                        return;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(requireContext(), "No fingerprints enrolled! Go to settings to add a fingerprint.", Toast.LENGTH_LONG).show();
                        fingerprintSwitch.setChecked(false);
                        return;
                }

                Executor executor = ContextCompat.getMainExecutor(requireContext());
                biometricPrompt = new BiometricPrompt(requireActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(requireContext(), "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(requireContext(), "Authentication Successful!", Toast.LENGTH_SHORT).show();
                        navigateToAdminHome();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(requireContext(), "Fingerprint not recognized. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Admin Fingerprint Login")
                        .setSubtitle("Use your fingerprint to log in as admin")
                        .setNegativeButtonText("Cancel")
                        .build();

                biometricPrompt.authenticate(promptInfo);
            } else {
                SharedPrefManager.getInstance(requireContext()).saveBiometric(false);
            }
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        fingerprintSwitch.setChecked(SharedPrefManager.getInstance(requireContext()).getBiometric());

        TextView contactUs = view.findViewById(R.id.Signed_contactUs);
        contactUs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Contat_Us_Activity.class);
            startActivity(intent);
        });

        TextView signOut = view.findViewById(R.id.Signed_Logout);
        signOut.setOnClickListener(v -> showLogoutConfirmationDialog());

        SharedPreferences sp = requireActivity().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        String userJson = sp.getString("user", null);
        String firstname = sp.getString("firstname", null);
        String lastname = sp.getString("lastname", null);

        if (userJson != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            TextView fname = view.findViewById(R.id.signed_user_name);
            TextView lname = view.findViewById(R.id.signed_user_name2);

            fname.setText(user.getFname() != null ? user.getFname() : firstname);
            lname.setText(user.getLname() != null ? user.getLname() : lastname);
        }

        TextView mapLoadBtn = view.findViewById(R.id.Signed_location);
        mapLoadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });

        // Handle tab selection (Fixed getIntent() issue)
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("selectedTab")) {
            int selectedTab = bundle.getInt("selectedTab", 0);

        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialogInterface, which) -> logout())
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.white));
    }

    private void logout() {
        SharedPreferences sp = requireActivity().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        sp.edit().remove("user").apply();
        sp.edit().remove("googleUserEmail").apply();
        sp.edit().remove("userEmailid").apply();

        loadFragment(new MyAccount());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.userSigned_fragment_design, fragment);
        transaction.commit();
    }

    private void navigateToAdminHome() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }
}
