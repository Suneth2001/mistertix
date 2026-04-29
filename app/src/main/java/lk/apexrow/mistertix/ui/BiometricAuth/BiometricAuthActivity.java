package lk.apexrow.mistertix.ui.BiometricAuth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import lk.apexrow.mistertix.MainActivity;

public class BiometricAuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the device supports biometrics
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showToast("No biometric hardware available");
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showToast("Biometric hardware unavailable");
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                showToast("No biometric data enrolled. Set up Face ID in Settings.");
                finish();
                break;
            default:
                showToast("Biometric authentication is not supported.");
                finish();
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                showToast("Authentication successful");
                startMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                showToast("Authentication failed. Try again.");
                showBiometricPrompt(); // Retry authentication
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                showToast("Error: " + errString);
                finish();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Face ID Required")
                .setSubtitle("Authenticate to access MISTERTIX")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void startMainActivity() {
        Intent intent = new Intent(BiometricAuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close this activity after authentication
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
