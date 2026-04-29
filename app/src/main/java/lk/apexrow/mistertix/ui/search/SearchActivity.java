package lk.apexrow.mistertix.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.apexrow.mistertix.Movie_Info_Activity;
import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.YouTubeActivity;
import lk.apexrow.mistertix.model.Movie;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ArrayList<Movie> movieListSearch = new ArrayList<>();
    private ArrayList<Movie> filteredList = new ArrayList<>();
    private MovieAdapterSearch movieAdapterSearch;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Ensure the correct layout is used

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView back= findViewById(R.id.back_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        finish();
            }
        });


        recyclerView = findViewById(R.id.recycle_View_Search);
        searchView = findViewById(R.id.search_text_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list and set it to RecyclerView
        movieAdapterSearch = new MovieAdapterSearch(filteredList);
        recyclerView.setAdapter(movieAdapterSearch);

        // Load movies from Firestore
        loadMovies();

        // SearchView filtering logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMovies(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMovies(newText);
                return false;
            }
        });
    }

    private void loadMovies() {
        firestore.collection("movies").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            movieListSearch.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                movieListSearch.add(new Movie(
                                        document.getString("name"),
                                        document.getString("youtubeLink"),
                                        document.getString("description"),
                                        document.getString("releaseDate"),
                                        document.getString("showtime5"),
                                        document.getString("showtime4"),
                                        document.getString("showtime3"),
                                        document.getString("showtime2"),
                                        document.getString("showtime1"),
                                        document.getString("imageUrl"),
                                        document.getString("genre"),
                                        document.getString("duration"),
                                        document.getString("language")
                                ));
                            }
                            filteredList.clear();
                            filteredList.addAll(movieListSearch);
                            movieAdapterSearch.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void filterMovies(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(movieListSearch);
        } else {
            for (Movie movie : movieListSearch) {
                if (movie.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(movie);
                }
            }
        }
        movieAdapterSearch.notifyDataSetChanged();
    }

    // Adapter Class for RecyclerView
    class MovieAdapterSearch extends RecyclerView.Adapter<MovieAdapterSearch.MovieViewHolderSearch> {
        private ArrayList<Movie> movieArrayList;

        public MovieAdapterSearch(ArrayList<Movie> movieArrayList) {
            this.movieArrayList = movieArrayList;
        }

        @NonNull
        @Override
        public MovieViewHolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.search_design, parent, false);
            return new MovieViewHolderSearch(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolderSearch holder, int position) {
            Movie movie = movieArrayList.get(position);

            holder.textViewTitle.setText(movie.getName());
            holder.textViewLanguage.setText(movie.getLanguage());
            holder.textViewDuration.setText(movie.getDuration());

            String imageUrl = movie.getImage();
            Log.i("FirebaseImage", "Image URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.rounded_border_3)
                        .error(R.drawable.rounded_border_3)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.rounded_border_3);
            }

            // Open YouTube Activity on play button click
            holder.imageViewPlay.setOnClickListener(v -> {
                String youtubeLink = movie.getYoutubeLink();
                if (youtubeLink != null && !youtubeLink.isEmpty()) {
                    Intent intent = new Intent(v.getContext(), YouTubeActivity.class);
                    intent.putExtra("youtubeLink", youtubeLink);
                    v.getContext().startActivity(intent);
                } else {
                    Log.e("YouTubeError", "No YouTube link available for this movie.");
                }
            });

            // Open Movie Info Activity on movie item click
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Movie_Info_Activity.class);
                    intent.putExtra("movieName", movie.getName());
                    intent.putExtra("movieLanguage", movie.getLanguage());
                    intent.putExtra("movieDuration", movie.getDuration());
                    intent.putExtra("movieGenre", movie.getGenre());
                    intent.putExtra("movieDescription", movie.getDescription());
                    intent.putExtra("movieReleaseDate", movie.getReleaseDate());
                    intent.putExtra("movieImage", movie.getImage());
                    intent.putExtra("movieYoutubeLink", movie.getYoutubeLink());
                    intent.putExtra("movieShowtime1", movie.getShowtime1());
                    intent.putExtra("movieShowtime2", movie.getShowtime2());
                    intent.putExtra("movieShowtime3", movie.getShowtime3());
                    intent.putExtra("movieShowtime4", movie.getShowtime4());
                    intent.putExtra("movieShowtime5", movie.getShowtime5());

                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieArrayList.size();
        }

        // ViewHolder Class
        class MovieViewHolderSearch extends RecyclerView.ViewHolder {
            TextView textViewTitle, textViewLanguage, textViewDuration;
            ImageView imageView, imageViewPlay;
            LinearLayout linearLayout;

            public MovieViewHolderSearch(View itemView) {
                super(itemView);
                textViewTitle = itemView.findViewById(R.id.search_movie_title);
                textViewLanguage = itemView.findViewById(R.id.search_movie_language);
                textViewDuration = itemView.findViewById(R.id.Searchmovie_duration);
                imageView = itemView.findViewById(R.id.search_movie_image);
                imageViewPlay = itemView.findViewById(R.id.search_play_button);
                linearLayout = itemView.findViewById(R.id.serach_design_Linear);
            }
        }
    }
}
