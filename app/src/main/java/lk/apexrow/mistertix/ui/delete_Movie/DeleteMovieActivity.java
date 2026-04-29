package lk.apexrow.mistertix.ui.delete_Movie;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.Movie;

public class DeleteMovieActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private ArrayList<Movie> filteredList = new ArrayList<>();
    private DeleteMovieAdapter adapter;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_movie);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView back = findViewById(R.id.back_search_delete);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewDelete);
        searchView = findViewById(R.id.delete_search_text_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DeleteMovieAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);

        loadMovies();

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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            movieList.add(new Movie(
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
                        filteredList.addAll(movieList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterMovies(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(movieList);
        } else {
            for (Movie movie : movieList) {
                if (movie.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(movie);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    class DeleteMovieAdapter extends RecyclerView.Adapter<DeleteMovieAdapter.MovieViewHolder> {
        private final ArrayList<Movie> movieArrayList;
        private final Context context;

        public DeleteMovieAdapter(ArrayList<Movie> movieArrayList, Context context) {
            this.movieArrayList = movieArrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_search, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
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

            // Handle Delete Button Click
            holder.deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Movie")
                        .setMessage("Are you sure you want to delete this movie?")
                        .setPositiveButton("OK", (dialog, which) -> deleteMovieFromFirestore(movie, position))
                        .setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(android.R.color.white));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(android.R.color.white));
                });

                dialog.show();
            });


            
        }

        private void deleteMovieFromFirestore(Movie movie, int position) {
            FirebaseFirestore.getInstance().collection("movies")
                    .whereEqualTo("name", movie.getName())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection("movies")
                                        .document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            movieArrayList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, movieArrayList.size());
                                            Toast.makeText(context, "Movie Deleted", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return movieArrayList.size();
        }

        class MovieViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTitle, textViewLanguage, textViewDuration;
            ImageView imageView;
            Button deleteButton;
            LinearLayout linearLayout;

            public MovieViewHolder(View itemView) {
                super(itemView);
                textViewTitle = itemView.findViewById(R.id.delete_search_movie_title);
                textViewLanguage = itemView.findViewById(R.id.delete_search_movie_language);
                textViewDuration = itemView.findViewById(R.id.delete_Searchmovie_duration);
                imageView = itemView.findViewById(R.id.delete_search_movie_image);
                deleteButton = itemView.findViewById(R.id.deletemoviebtn);
                linearLayout = itemView.findViewById(R.id.delete_serach_design_Linear);
            }
        }
    }
}
