package lk.apexrow.mistertix.ui.movies;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.Carousel_Image;
import lk.apexrow.mistertix.model.Movie;
import lk.apexrow.mistertix.model.UpCommingMovies;


public class MovieFragment extends Fragment {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerViewNowshowing, recyclerViewUpComing;
    private ArrayList<Movie> nowShowingMovies = new ArrayList<>();
    private ArrayList<UpCommingMovies> upCommingMoviesArrayList = new ArrayList<>();
    private MovieAdapterNowShowing movieAdapterNowShowing;
    private int currentPosition = 0;
    private Handler carouselHandler = new Handler();
    private RecyclerView recyclerViewCarouserl;
    private Runnable carouselRunnable;
    private MovieAdapterUpComing movieAdapterUpComing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewCarouserl);

        //carosel View

        ArrayList<Carousel_Image> carouselImages = new ArrayList<>();
        carouselImages.add(new Carousel_Image(R.drawable.download));
        carouselImages.add(new Carousel_Image(R.drawable.images));
        carouselImages.add(new Carousel_Image(R.drawable.venom));

        recyclerViewCarouserl = view.findViewById(R.id.carousel_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCarouserl.setLayoutManager(layoutManager);
        CaroselAdapter caroselAdapter = new CaroselAdapter(carouselImages);
        recyclerViewCarouserl.setAdapter(caroselAdapter);

        startAutoScroll(carouselImages.size());

        //Coming Soon View
        recyclerViewUpComing = view.findViewById(R.id.coming_movie_view_recycleView);
        recyclerViewUpComing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        movieAdapterUpComing = new MovieAdapterUpComing(upCommingMoviesArrayList);
        recyclerViewUpComing.setAdapter(movieAdapterUpComing);
        upcomingMovies();

        //Now Showing View
        recyclerViewNowshowing = view.findViewById(R.id.now_showing_view_recycleView);
        recyclerViewNowshowing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        movieAdapterNowShowing = new MovieAdapterNowShowing(nowShowingMovies);
        recyclerViewNowshowing.setAdapter(movieAdapterNowShowing);
        nowShowingMovies();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
        }
    }
    private void startAutoScroll(int size) {
        if (recyclerViewCarouserl == null || size == 0) return;
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition >= size) {
                    currentPosition = 0;
                }
                smoothScrollToPosition(currentPosition);
                currentPosition++;
                carouselHandler.postDelayed(this, 4000); // Repeat every 2000ms
            }
        };
        carouselHandler.postDelayed(carouselRunnable, 4000);
    }

    private void smoothScrollToPosition(int position) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(recyclerViewCarouserl.getContext()) {
            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_START; // Ensures smooth snapping
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 200f / displayMetrics.densityDpi; // Adjust this value for a slower scroll
            }
        };
        smoothScroller.setTargetPosition(position);
        recyclerViewCarouserl.getLayoutManager().startSmoothScroll(smoothScroller);
    }


    private void nowShowingMovies() {
        nowShowingMovies.clear();

        firestore.collection("movies").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nowShowingMovies.add(new Movie(
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
                            movieAdapterNowShowing.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void upcomingMovies() {
        upCommingMoviesArrayList.clear();

        firestore.collection("upcoming_movies").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                upCommingMoviesArrayList.add(new UpCommingMovies(
                                        document.getString("name"),
                                        document.getString("language"),
                                        document.getString("genre"),
                                        document.getString("duration"),
                                        document.getString("imageUrl")
                                ));
                            }
                            movieAdapterUpComing.notifyDataSetChanged();
                        }
                    }
                });
    }
}


class CaroselViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public CaroselViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView_carousel);
    }
}

class CaroselAdapter extends RecyclerView.Adapter<CaroselViewHolder> {
    public ArrayList<Carousel_Image> carouselImagesList;

    public CaroselAdapter(ArrayList<Carousel_Image> carouselImagesList) {
        this.carouselImagesList = carouselImagesList;
    }

    @NonNull
    @Override
    public CaroselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.carousel_view, null);
        CaroselViewHolder caroselViewHolder = new CaroselViewHolder(view);
        return caroselViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CaroselViewHolder holder, int position) {
        holder.imageView.setImageResource(carouselImagesList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return this.carouselImagesList.size();
    }
}


class MovieViewHolderNowShowing extends RecyclerView.ViewHolder {
    public TextView textView1, textView2, textView3;
    public ImageView imageView;

    public MovieViewHolderNowShowing(@NonNull View itemView) {
        super(itemView);
        textView1 = itemView.findViewById(R.id.movie_name_NowShowing);
        textView2 = itemView.findViewById(R.id.language_textView_nowShowing);
        textView3 = itemView.findViewById(R.id.movie_time_nowShowing);
        imageView = itemView.findViewById(R.id.imageView_nowShowing);
    }
}

class MovieAdapterNowShowing extends RecyclerView.Adapter<MovieViewHolderNowShowing> {

    public ArrayList<Movie> movieArrayList;

    public MovieAdapterNowShowing(ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
    }

    @NonNull
    @Override
    public MovieViewHolderNowShowing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.now_showing_movies, null);
        MovieViewHolderNowShowing movieViewHolderNowShowing = new MovieViewHolderNowShowing(view);
        return movieViewHolderNowShowing;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolderNowShowing holder, int position) {
        Movie movie = movieArrayList.get(position);

        holder.textView1.setText(movieArrayList.get(position).getName());
        holder.textView2.setText(movieArrayList.get(position).getLanguage());
        holder.textView3.setText(movieArrayList.get(position).getDuration());

        String imageUrl = movie.getImage();
        Log.i("FirebaseImagenowShowing", "Imag URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.rounded_border_2)
                    .error(R.drawable.rounded_border_2)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.rounded_border_2);
        }
    }

    @Override
    public int getItemCount() {
        return this.movieArrayList.size();
    }
}


class UpComingViewHolder extends RecyclerView.ViewHolder {
    public TextView textView1, textView2, textView3;
    public ImageView imageView;

    public UpComingViewHolder(@NonNull View itemView) {
        super(itemView);
        textView1 = itemView.findViewById(R.id.movie_name_comingSoon);
        textView2 = itemView.findViewById(R.id.language_textView_comingSoon);
        textView3 = itemView.findViewById(R.id.movie_time_comingSoon);
        imageView = itemView.findViewById(R.id.imageView_comingSoon);

    }
}

class MovieAdapterUpComing extends RecyclerView.Adapter<UpComingViewHolder> {

    public ArrayList<UpCommingMovies> upCommingMoviesArrayList;

    public MovieAdapterUpComing(ArrayList<UpCommingMovies> upCommingMoviesArrayList) {
        this.upCommingMoviesArrayList = upCommingMoviesArrayList;
    }

    @NonNull
    @Override
    public UpComingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.coming_soon_movies, null);
        UpComingViewHolder upComingViewHolder = new UpComingViewHolder(view);
        return upComingViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull UpComingViewHolder holder, int position) {
        UpCommingMovies upCommingMovies = upCommingMoviesArrayList.get(position);

        holder.textView1.setText(upCommingMoviesArrayList.get(position).getName());
        holder.textView2.setText(upCommingMoviesArrayList.get(position).getLanguage());
        holder.textView3.setText(upCommingMoviesArrayList.get(position).getDuration());

        String imageUrl = upCommingMovies.getImage();
        Log.i("FirebaseImageUpComing", "Imag URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.rounded_border_2)
                    .error(R.drawable.rounded_border_2)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.rounded_border_2);
        }


    }

    @Override
    public int getItemCount() {
        return this.upCommingMoviesArrayList.size();
    }
}