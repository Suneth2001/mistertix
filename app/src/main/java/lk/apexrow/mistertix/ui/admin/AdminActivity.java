package lk.apexrow.mistertix.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lk.apexrow.mistertix.MainActivity;
import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    Toast backToast;
    private long backPressedTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.bottom_navigation_admin);

        // Find the NavHostFragment using getSupportFragmentManager()
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_admin_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Configure the AppBar with top-level destinations
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_Dashboard_admin, R.id.nav_Movie_admin, R.id.nav_payments_admin)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigationAdmin, navController);
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000> System.currentTimeMillis()) {
            if (backToast != null) {
                backToast.cancel();
            }
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish();


        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
