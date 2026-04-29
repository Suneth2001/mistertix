package lk.apexrow.mistertix.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.User;
import lk.apexrow.mistertix.ui.signUp.Sign_Up_Activity;
import lk.apexrow.mistertix.ui.signed_user.SignedUser_Fragment;

public class MyAccount extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout first
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        // Load SharedPreferences
        SharedPreferences sp = requireActivity().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        String userJson = sp.getString("user", null);
        sp.edit().remove("user");

        SharedPreferences spGoogle = requireActivity().getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        String userGoogle = sp.getString("googleUserEmail", null);


        if (userGoogle != null) {
            loadFragment(new SignedUser_Fragment());
        }

        if (userJson != null && !userJson.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            Log.d("DEBUG", "User found: " + user.getEmail());
            loadFragment(new SignedUser_Fragment()); // Load Signed User Fragment
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.btnLogin_or_SignUp);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Sign_Up_Activity.class);
            startActivity(intent);
        });


    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.commit();
    }
}
