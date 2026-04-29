package lk.apexrow.mistertix.ui.S;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
    }

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public String getUserEmail() {
        return sharedPreferences.getString("loggedInUserEmail", null);
    }

    public void saveUserEmail(String email) {
        sharedPreferences.edit().putString("loggedInUserEmail", email).apply();
    }

    public void saveBiometric(boolean biometricState) {
        sharedPreferences.edit().putBoolean("biometricState", biometricState).apply();
    }

    public  boolean getBiometric() {
        return sharedPreferences.getBoolean("biometricState", false);
    }

    public void clearUserEmail() {
        sharedPreferences.edit().remove("loggedInUserEmail").apply();
    }

}
