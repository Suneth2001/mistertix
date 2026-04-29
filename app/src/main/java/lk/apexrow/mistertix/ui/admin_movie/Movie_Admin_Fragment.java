package lk.apexrow.mistertix.ui.admin_movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.ui.addMovie.AddMovieActivity;
import lk.apexrow.mistertix.ui.delete_Movie.DeleteMovieActivity;
import lk.apexrow.mistertix.ui.upcomming_movie.Upcoming_movie_Activity;


public class Movie_Admin_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie__admin_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView= view.findViewById(R.id.Movie_add);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), AddMovieActivity.class);
                startActivity(intent);
            }
        });
        TextView upcoming= view.findViewById(R.id.UpComing_movie);
        upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), Upcoming_movie_Activity.class);
                startActivity(intent);
            }
        });
        TextView deletemovie= view.findViewById(R.id.Movie_Delete);
        deletemovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), DeleteMovieActivity.class);
                startActivity(intent);
            }
        });
    }
}