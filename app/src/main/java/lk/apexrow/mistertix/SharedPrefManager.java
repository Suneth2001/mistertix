package lk.apexrow.mistertix;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "lk.apexrow.mistertix.userdata";
    private static final String KEY_BIOMETRIC = "biometricState";
    private static final String KEY_USER_EMAIL = "loggedInUserEmail";

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public String getUser() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void saveUserEmail(String email) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public void saveBiometric(boolean biometricState) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC, biometricState).apply();
    }

    public boolean getBiometric() {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC, false);
    }

    public void clearUserEmail() {
        sharedPreferences.edit().remove(KEY_USER_EMAIL).apply();
    }
}
