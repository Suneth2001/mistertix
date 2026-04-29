package lk.apexrow.mistertix.ui.addMovie;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.apexrow.mistertix.R;

public class AddMovieActivity extends AppCompatActivity {
    public static final int PERMISSION_REQ_CODE = 100;
    private ImageView movieImageView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_movie);

        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Add_Movie_activity_design), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Language Spinner
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Spinner spinner = findViewById(R.id.spinner_language);

        List<String> addMovieLanguage = new ArrayList<>();
        addMovieLanguage.add("--Select Language--");

        firestore.collection("language").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addMovieLanguage.add(document.getString("name"));
                            }
                        }
                    }
                });
        ArrayAdapter<String> languageAdapterAddMovie = new ArrayAdapter<>(
                this,
                R.layout.spinner_items_any,
                addMovieLanguage
        );
        languageAdapterAddMovie.setDropDownViewResource(R.layout.spinner_items_any);
        spinner.setAdapter(languageAdapterAddMovie);

        //Genre Spinner
        Spinner spinnerGenre = findViewById(R.id.spinner_genre);
        List<String> arrayGenreList = new ArrayList<>();
        arrayGenreList.add("--Select Genre--");

        firestore.collection("genre").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrayGenreList.add(document.getString("name"));
                            }
                        }
                    }
                });
        ArrayAdapter<String> genreAdperAddMovie = new ArrayAdapter(
                this, R.layout.spinner_items_any,
                arrayGenreList
        );

        genreAdperAddMovie.setDropDownViewResource(R.layout.spinner_items_any);
        spinnerGenre.setAdapter(genreAdperAddMovie);


        // Initialize UI Components
        movieImageView = findViewById(R.id.movie_image_view);
        Button imageSelectButton = findViewById(R.id.btn_select_image);

        // Set click listener for selecting image
        imageSelectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });


        Button addMoviebtn = findViewById(R.id.btn_add_movie);
        addMoviebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText movieName = findViewById(R.id.movie_name);
                EditText movieReleaseDate = findViewById(R.id.movie_release_date);
                EditText movieDuration = findViewById(R.id.movie_Duration);
                EditText movieDescription = findViewById(R.id.movie_description);
                EditText movieYoutubeLink = findViewById(R.id.movie_youtube_link);
                EditText showtime1 = findViewById(R.id.showtime1);
                EditText showtime2 = findViewById(R.id.showtime2);
                EditText showtime3 = findViewById(R.id.showtime3);
                EditText showtime4 = findViewById(R.id.showtime4);
                EditText showtime5 = findViewById(R.id.showtime5);

                Spinner spinnerLanguage = findViewById(R.id.spinner_language);
                Spinner spinnerGenre = findViewById(R.id.spinner_genre);

                String language = spinnerLanguage.getSelectedItem().toString();
                String genre = spinnerGenre.getSelectedItem().toString();
                String movieNameString = movieName.getText().toString();
                String movieReleaseDateString = movieReleaseDate.getText().toString();
                String movieTimeString = movieDuration.getText().toString();
                String movieDescriptionString = movieDescription.getText().toString();
                String movieYoutubeLinkString = movieYoutubeLink.getText().toString();
                String showtime1String = showtime1.getText().toString();
                String showtime2String = showtime2.getText().toString();
                String showtime3String = showtime3.getText().toString();
                String showtime4String = showtime4.getText().toString();
                String showtime5String = showtime5.getText().toString();


                if (selectedImageUri == null) {
                    Toast.makeText(AddMovieActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                } else if (movieNameString.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
                } else if (movieReleaseDateString.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a movie release date", Toast.LENGTH_SHORT).show();
                } else if (movieTimeString.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a movie Duration", Toast.LENGTH_SHORT).show();
                } else if (language.equals("--Select Language--")) {
                    Toast.makeText(AddMovieActivity.this, "Please select a language", Toast.LENGTH_SHORT).show();
                } else if (genre.equals("--Select Genre--")) {
                    Toast.makeText(AddMovieActivity.this, "Please select a genre", Toast.LENGTH_SHORT).show();
                } else if (movieDescriptionString.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a movie description", Toast.LENGTH_SHORT).show();
                } else if (movieYoutubeLinkString.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a movie youtube link", Toast.LENGTH_SHORT).show();
                } else if (showtime1String.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a showtime1", Toast.LENGTH_SHORT).show();
                } else if (showtime2String.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a showtime2", Toast.LENGTH_SHORT).show();
                } else if (showtime3String.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a showtime3", Toast.LENGTH_SHORT).show();
                } else if (showtime4String.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a showtime4", Toast.LENGTH_SHORT).show();
                } else if (showtime5String.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please enter a showtime5", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImageToFirebaseAddMovie(selectedImageUri, movieNameString, movieReleaseDateString, movieTimeString, language,
                            movieDescriptionString, movieYoutubeLinkString, genre, showtime1String, showtime2String, showtime3String, showtime4String, showtime5String);
                    resetfield();
                }

            }
        });


    }

    private void saveMovieToFirestore(String movieName, String movieReleaseDate, String movieDuration, String language, String movieDescription,String movieYoutubeLink,  String genre, String imageUrl, String showtime1, String showtime2, String showtime3, String showtime4, String showtime5) {
        HashMap<String, Object> addmovie = new HashMap<>();
        addmovie.put("name", movieName);
        addmovie.put("releaseDate", movieReleaseDate);
        addmovie.put("duration", movieDuration);
        addmovie.put("description", movieDescription);
        addmovie.put("youtubeLink", movieYoutubeLink);
        addmovie.put("language", language);
        addmovie.put("genre", genre);
        addmovie.put("imageUrl", imageUrl);
        addmovie.put("showtime1", showtime1);
        addmovie.put("showtime2", showtime2);
        addmovie.put("showtime3", showtime3);
        addmovie.put("showtime4", showtime4);
        addmovie.put("showtime5", showtime5);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("movies")
                .add(addmovie)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddMovieActivity.this, "Movie added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddMovieActivity.this, "Failed to add movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Request storage permission based on Android version
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

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            Button imageSelectButton = findViewById(R.id.btn_select_image);
            imageSelectButton.setEnabled(false);
        }
    }
    // Open gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // Handle gallery selection result
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        movieImageView.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(AddMovieActivity.this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
                        Log.e("Gallery", "Failed to get image URI");
                    }
                }
            });

    private void uploadImageToFirebaseAddMovie(Uri imageUri, String movieName, String movieReleaseDate, String movieDuration, String language, String movieDescription, String movieYoutubeLink, String genre, String showtime1, String showtime2, String showtime3, String showtime4, String showtime5) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            Log.e("FirebaseStorage", "Image URI is null");
            return;
        }

        Log.i("FirebaseStorage", "Uploading image: " + imageUri.toString());

        final StorageReference storageRef = storage.getReference().child("movie_images_addmovie/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("FirebaseStorage", "Image uploaded successfully");


                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                Log.d("FirebaseStorage", "Download URL: " + imageUrl);

                                // Save movie details to Firestore only after getting the image URL
                                saveMovieToFirestore(movieName, movieReleaseDate, movieDuration, language, movieDescription, movieYoutubeLink, genre, imageUrl, showtime1, showtime2, showtime3, showtime4, showtime5);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddMovieActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseStorage", "Failed to get download URL", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMovieActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseStorage", "Image upload failed", e);
                });
    }
    private void resetfield(){
        EditText movieName = findViewById(R.id.movie_name);
        EditText movieReleaseDate = findViewById(R.id.movie_release_date);
        EditText movieDuration = findViewById(R.id.movie_Duration);
        EditText movieDescription = findViewById(R.id.movie_description);
        EditText movieYoutubeLink = findViewById(R.id.movie_youtube_link);
        EditText showtime1 = findViewById(R.id.showtime1);
        EditText showtime2 = findViewById(R.id.showtime2);
        EditText showtime3 = findViewById(R.id.showtime3);
        EditText showtime4 = findViewById(R.id.showtime4);
        EditText showtime5 = findViewById(R.id.showtime5);
        Spinner spinnerLanguage = findViewById(R.id.spinner_language);
        Spinner spinnerGenre = findViewById(R.id.spinner_genre);
        movieImageView.setImageURI(null);
        movieName.setText("");
        movieReleaseDate.setText("");
        movieDuration.setText("");
        movieDescription.setText("");
        movieYoutubeLink.setText("");
        showtime1.setText("");
        showtime2.setText("");
        showtime3.setText("");
        showtime4.setText("");
        showtime5.setText("");
        spinnerLanguage.setSelection(0);
        spinnerGenre.setSelection(0);



    }

}
