package lk.apexrow.mistertix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import lk.apexrow.mistertix.ui.seat_Booking.SeatViewActivity;
import lk.apexrow.mistertix.ui.signIn.SignInActivity;

public class Movie_Info_Activity extends AppCompatActivity {

    private ImageView movieImage, movieYoutubeLink;
    private TextView movieTitle, movieLanguage, movieDuration, movieGenre, movieDescription, movieReleaseDate;
    private TextView movieShowtime1, movieShowtime2, movieShowtime3, movieShowtime4, movieShowtime5;

    private String title, showtime1, showtime2, showtime3, showtime4, showtime5, language, genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        // Initialize UI elements
        movieImage = findViewById(R.id.movie_info_imageView);
        movieYoutubeLink = findViewById(R.id.movie_info_Youtube_image);
        movieTitle = findViewById(R.id.title_movie_info);
        movieLanguage = findViewById(R.id.language_movie_info);
        movieDuration = findViewById(R.id.duration_movie_info);
        movieGenre = findViewById(R.id.genre_movie_info);
        movieDescription = findViewById(R.id.starring_movie_info);
        movieReleaseDate = findViewById(R.id.release_date_movie_info);
        movieShowtime1 = findViewById(R.id.showtime_movie_Info);
        movieShowtime2 = findViewById(R.id.showtime_movie_Info2);
        movieShowtime3 = findViewById(R.id.showtime_movie_Info3);
        movieShowtime4 = findViewById(R.id.showtime_movie_Info4);
        movieShowtime5 = findViewById(R.id.showtime_movie_Info5);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("movieName");
            language = intent.getStringExtra("movieLanguage");
            String duration = intent.getStringExtra("movieDuration");
            genre = intent.getStringExtra("movieGenre");
            String description = intent.getStringExtra("movieDescription");
            String releaseDate = intent.getStringExtra("movieReleaseDate");
            String imageUrl = intent.getStringExtra("movieImage");
            String youtubeLink = intent.getStringExtra("movieYoutubeLink");
            showtime1 = intent.getStringExtra("movieShowtime1");
            showtime2 = intent.getStringExtra("movieShowtime2");
            showtime3 = intent.getStringExtra("movieShowtime3");
            showtime4 = intent.getStringExtra("movieShowtime4");
            showtime5 = intent.getStringExtra("movieShowtime5");

            // Set data to views
            movieTitle.setText(title);
            movieLanguage.setText(language);
            movieDuration.setText(duration);
            movieGenre.setText(genre);
            movieDescription.setText(description);
            movieReleaseDate.setText(releaseDate);
            movieShowtime1.setText(showtime1);
            movieShowtime2.setText(showtime2);
            movieShowtime3.setText(showtime3);
            movieShowtime4.setText(showtime4);
            movieShowtime5.setText(showtime5);

            // Load movie image
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(movieImage);
            } else {
                movieImage.setImageResource(R.drawable.rounded_border_2);
            }

            // YouTube Button Click Listener
            movieYoutubeLink.setOnClickListener(v -> {
                if (youtubeLink != null && !youtubeLink.isEmpty()) {
                    Intent youtubeIntent = new Intent(Movie_Info_Activity.this, YouTubeActivity.class);
                    youtubeIntent.putExtra("youtubeLink", youtubeLink);
                    startActivity(youtubeIntent);
                } else {
                    Log.e("YouTubeError", "No YouTube link available for this movie.");
                }
            });
// Showtime Click Listeners
            View.OnClickListener showtimeClickListener = v -> {
                String selectedShowtime = ((TextView) v).getText().toString(); // Get selected showtime
                SharedPreferences sharedPreferences = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);

                String googleUserEmail = sharedPreferences.getString("googleUserEmail", null);
                String userJson = sharedPreferences.getString("user", null);

                // If no user is found, redirect to SignInActivity
                if ((userJson == null || userJson.trim().isEmpty()) && (googleUserEmail == null || googleUserEmail.trim().isEmpty())) {
                    Intent signInIntent = new Intent(Movie_Info_Activity.this, SignInActivity.class);
                    signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(this, "Please Sign In Before Enrolling", Toast.LENGTH_SHORT).show();
                    startActivity(signInIntent);
                    return;
                }

                Intent intent1 = new Intent(Movie_Info_Activity.this, SeatViewActivity.class);
                intent1.putExtra("movieName", movieTitle.getText().toString());
                intent1.putExtra("selectedShowtime", selectedShowtime);
                intent1.putExtra("showtime1", showtime1);
                intent1.putExtra("showtime2", showtime2);
                intent1.putExtra("showtime3", showtime3);
                intent1.putExtra("showtime4", showtime4);
                intent1.putExtra("showtime5", showtime5);
                intent1.putExtra("language", language);
                intent1.putExtra("genre", genre);


                startActivity(intent1);
            };


            movieShowtime1.setOnClickListener(showtimeClickListener);
            movieShowtime2.setOnClickListener(showtimeClickListener);
            movieShowtime3.setOnClickListener(showtimeClickListener);
            movieShowtime4.setOnClickListener(showtimeClickListener);
            movieShowtime5.setOnClickListener(showtimeClickListener);


        }
    }


}

