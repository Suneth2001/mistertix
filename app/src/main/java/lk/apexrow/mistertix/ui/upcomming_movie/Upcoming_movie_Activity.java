package lk.apexrow.mistertix.ui.upcomming_movie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.apexrow.mistertix.R;

public class Upcoming_movie_Activity extends AppCompatActivity {

    public static final int PERMISSION_REQ_CODE = 100;
    private ImageView movieImageView;
    private Uri selectedImageUri = null; // Store selected image URI
    private  String  imageUrl;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_movie);

        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.UpComming_Movie_activity_design), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Spinner spinnerLanguage = findViewById(R.id.UpComing_movie_spinner_language);
        Spinner spinnerGenre = findViewById(R.id.UpComing_movie_spinner_genre);

        List<String> upcomingLanguage = new ArrayList<>();
        List<String> upcomingGenre = new ArrayList<>();

        upcomingLanguage.add("--Select Language--");
        upcomingGenre.add("--Select Genre--");

        // Fetch languages from Firestore
        firestore.collection("language").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            upcomingLanguage.add(document.getString("name"));
                        }
                        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(Upcoming_movie_Activity.this, R.layout.spinner_items_any, upcomingLanguage);
                        languageAdapter.setDropDownViewResource(R.layout.spinner_items_any);
                        spinnerLanguage.setAdapter(languageAdapter);
                    } else {
                        Toast.makeText(Upcoming_movie_Activity.this, "Failed to fetch languages: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Failed to fetch languages", task.getException());
                    }
                });

        // Fetch genres from Firestore
        firestore.collection("genre").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            upcomingGenre.add(document.getString("name"));
                        }
                        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(Upcoming_movie_Activity.this, R.layout.spinner_items_any, upcomingGenre);
                        genreAdapter.setDropDownViewResource(R.layout.spinner_items_any);
                        spinnerGenre.setAdapter(genreAdapter);
                    } else {
                        Toast.makeText(Upcoming_movie_Activity.this, "Failed to fetch genres: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Failed to fetch genres", task.getException());
                    }
                });

        // Initialize UI Components
        movieImageView = findViewById(R.id.UpComing_movie_image_view);
        Button imageSelectButton = findViewById(R.id.btn_select_image_upcoming);


        // Set click listener for selecting image
        imageSelectButton.setOnClickListener(v -> requestStoragePermission());

        // Save movie details


        Button addMovieUpcomingBtn = findViewById(R.id.UpComing_movie_btn_add_movie);
        addMovieUpcomingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText movieNameEditText = findViewById(R.id.UpComing_movie_name);
                EditText movieDurationEditText = findViewById(R.id.UpComing_movie_duration);


                String movieName = movieNameEditText.getText().toString().trim();
                String movieDuration = movieDurationEditText.getText().toString().trim();
                String language = spinnerLanguage.getSelectedItem().toString();
                String genre = spinnerGenre.getSelectedItem().toString();

                if (selectedImageUri ==null) {
                    Toast.makeText(Upcoming_movie_Activity.this, "Please select an image", Toast.LENGTH_SHORT).show();

                } else if (movieName.isEmpty()) {
                    movieNameEditText.setError("Enter the Movie Name");

                } else if (language.equals("--Select Language--")) {
                    Toast.makeText(Upcoming_movie_Activity.this, "Select a Language", Toast.LENGTH_SHORT).show();

                } else if (genre.equals("--Select Genre--")) {
                    Toast.makeText(Upcoming_movie_Activity.this, "Select a Genre", Toast.LENGTH_SHORT).show();

                } else if (movieDuration.isEmpty()) {
                    movieDurationEditText.setError("Enter the Movie Duration");
                } else{

                    uploadImageToFirebase(selectedImageUri, movieName, movieDuration, language, genre);
                    reseatFieldUpcoming();
                }
            }
        });
    }

    private void saveMovieToFirestore(String movieName, String movieDuration, String language, String genre, String imageUrl) {
        HashMap<String, Object> movie = new HashMap<>();
        movie.put("name", movieName);
        movie.put("duration", movieDuration);
        movie.put("language", language);
        movie.put("genre", genre);
        movie.put("imageUrl", imageUrl);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("upcoming_movies")
                .add(movie)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(Upcoming_movie_Activity.this, "Movie Added Successfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Upcoming_movie_Activity.this, "Failed to Add Movie: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQ_CODE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            } else {
                openGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            Button imageSelectButton = findViewById(R.id.btn_select_image_upcoming);
            imageSelectButton.setEnabled(false);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        movieImageView.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(Upcoming_movie_Activity.this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
                        Log.e("Gallery", "Failed to get image URI");
                    }
                }
            });

    private void uploadImageToFirebase(Uri imageUri, String movieName, String movieDuration, String language, String genre) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            Log.e("FirebaseStorage", "Image URI is null");
            return;
        }

        Log.i("FirebaseStorage", "Uploading image: " + imageUri.toString());

        final StorageReference storageRef = storage.getReference().child("movie_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("FirebaseStorage", "Image uploaded successfully");
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                Log.d("FirebaseStorage", "Download URL: " + imageUrl);

                                // Save movie details to Firestore only after getting the image URL
                                saveMovieToFirestore(movieName, movieDuration, language, genre, imageUrl);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Upcoming_movie_Activity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseStorage", "Failed to get download URL", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Upcoming_movie_Activity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseStorage", "Image upload failed", e);
                });
    }

    private void reseatFieldUpcoming(){
        EditText movieNameEditText = findViewById(R.id.UpComing_movie_name);
        EditText movieDurationEditText = findViewById(R.id.UpComing_movie_duration);
        Spinner spinnerLanguage = findViewById(R.id.UpComing_movie_spinner_language);
        Spinner spinnerGenre = findViewById(R.id.UpComing_movie_spinner_genre);


        movieImageView.setImageURI(null);

        spinnerLanguage.setSelection(0);
        spinnerGenre.setSelection(0);
        movieNameEditText.setText("");
        movieDurationEditText.setText("");


    }


}