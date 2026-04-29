package lk.apexrow.mistertix.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import lk.apexrow.mistertix.R;

public class MapActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactUsDesign), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapFrameLayout, supportMapFragment);
        fragmentTransaction.commit();

        client = LocationServices.getFusedLocationProviderClient(this);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng userLocation=new LatLng(7.453186906590622, 80.42026578424259);

                                if (location != null) {
                                     userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                } else {
                                    Log.e("Location Error", "Location is null");
                                }

                                LatLng latLng = new LatLng(7.48714117952581, 80.36760550301052);
                                Log.i("Suneth",String.valueOf(latLng));


                                googleMap.animateCamera(
                                        CameraUpdateFactory.newCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(latLng)
                                                        .zoom(18)
                                                        .build()
                                        )
                                );
                                googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("Mistertix Cinema")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cinema1))
                                );
                                googleMap.animateCamera(
                                        CameraUpdateFactory.newCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(userLocation)
                                                        .zoom(18)
                                                        .build()
                                        )
                                );
                                googleMap.addMarker(new MarkerOptions()
                                        .position(userLocation)
                                        .title("Me")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                                );
                            } else {
                                Log.e("MistertixMap", "Location is null");
                            }
                        }
                    });
                    googleMap.setMyLocationEnabled(true);
                } else {
                    // Request permissions if not granted
                    ActivityCompat.requestPermissions(MapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
        });
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate(); // Restart activity to apply permission changes
            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
