package lk.apexrow.mistertix.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import lk.apexrow.mistertix.ui.search.SearchActivity;

public class HomeFragment extends Fragment {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<Movie> movieListHome = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private ImageView searchMovie;
    private RecyclerView recyclerViewMovie;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerViewMovie = view.findViewById(R.id.recycleviewhome);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(movieListHome);
        recyclerViewMovie.setAdapter(movieAdapter);
        loadMovies();

        searchMovie= view.findViewById(R.id.search_home_btn);
        searchMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadMovies() {

        movieListHome.clear();
        firestore.collection("movies").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                movieListHome.add(new Movie(
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

                              Log.i("YoutubeLink",document.getString("youtubeLink"));
                            }

                            // Notify adapter after data is fetched
                            movieAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}


class MovieViewHolder extends RecyclerView.ViewHolder {
    public TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8;
    public ImageView imageView, imageView2;

    public LinearLayout linearLayout;

    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);
        textView1 = itemView.findViewById(R.id.movie_title);
        textView2 = itemView.findViewById(R.id.movie_language);
        textView3 = itemView.findViewById(R.id.movie_duration);
        textView4 = itemView.findViewById(R.id.showtime1);
        textView5 = itemView.findViewById(R.id.showtime2);
        textView6 = itemView.findViewById(R.id.showtime3);
        textView7 = itemView.findViewById(R.id.showtime4);
        textView8 = itemView.findViewById(R.id.showtime5);
        imageView = itemView.findViewById(R.id.movie_image);
        imageView2 = itemView.findViewById(R.id.play_button);
        linearLayout =itemView.findViewById(R.id.movie_recycle_view_design);



    }
}



class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private ArrayList<Movie> movieArrayList;

    public MovieAdapter(ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieArrayList.get(position);

        holder.textView1.setText(movie.getName());
        holder.textView2.setText(movie.getLanguage());
        holder.textView3.setText(movie.getDuration());
        holder.textView4.setText(movie.getShowtime1());
        holder.textView5.setText(movie.getShowtime2());
        holder.textView6.setText(movie.getShowtime3());
        holder.textView7.setText(movie.getShowtime4());
        holder.textView8.setText(movie.getShowtime5());


        String imageUrl = movie.getImage();
        Log.i("FirebaseImage", "Image URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.rounded_border_3)
                    .error(R.drawable.rounded_border_3)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.rounded_border_3);
        }

        // **Fix Play Button Click Issue**
        holder.imageView2.setOnClickListener(v -> {
            String youtubeLink = movie.getYoutubeLink();
            if (youtubeLink != null && !youtubeLink.isEmpty()) {
                Intent intent = new Intent(v.getContext(), YouTubeActivity.class);
                intent.putExtra("youtubeLink", youtubeLink);
                v.getContext().startActivity(intent);
            } else {
                Log.e("YouTubeError", "No YouTube link available for this movie.");
            }
        });
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
}




