package lk.apexrow.mistertix.ui.signUp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.CountryData;
import lk.apexrow.mistertix.model.User;
import lk.apexrow.mistertix.ui.signIn.SignInActivity;

public class Sign_Up_Activity extends AppCompatActivity {

    private EditText fname, lname, email, mobile, dob, password;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private Switch agreebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        fname = findViewById(R.id.signup_fname);
        lname = findViewById(R.id.signup_lname);
        email = findViewById(R.id.signup_emailPhone);
        mobile = findViewById(R.id.signup_mobile);
        dob = findViewById(R.id.dob_input);
        password = findViewById(R.id.signup_password);
        spinner = findViewById(R.id.spinner_country);
        radioGroup = findViewById(R.id.radioGroup);
        agreebtn=findViewById(R.id.switch1);


        dob.setOnClickListener(v -> showDatePickerDialog());

        Button createAccount = findViewById(R.id.signup_loginButton);
        createAccount.setOnClickListener(v -> registerUser());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_fragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinner = findViewById(R.id.spinner_country);
        ArrayList<CountryData> arrayList = new ArrayList<>();


        arrayList.add(new CountryData(R.drawable.srilanka, "Sri Lanka", 94));
        arrayList.add(new CountryData(R.drawable.australia, "Australia", 61));
        arrayList.add(new CountryData(R.drawable.india, "India", 91));
        arrayList.add(new CountryData(R.drawable.unitedstates, "United States", 1));
//        arrayList.add(new CountryData(R.drawable., "United Kingdom", 44));
//        arrayList.add(new CountryData(R.drawable.canada, "Canada", 1));
//        arrayList.add(new CountryData(R.drawable.germany, "Germany", 49));
//        arrayList.add(new CountryData(R.drawable.france, "France", 33));
        arrayList.add(new CountryData(R.drawable.japan, "Japan", 81));
//        arrayList.add(new CountryData(R.drawable.china, "China", 86));
//        arrayList.add(new CountryData(R.drawable.brazil, "Brazil", 55));
//        arrayList.add(new CountryData(R.drawable.south_africa, "South Africa", 27));
//        arrayList.add(new CountryData(R.drawable.russia, "Russia", 7));
//        arrayList.add(new CountryData(R.drawable.italy, "Italy", 39));
//        arrayList.add(new CountryData(R.drawable.spain, "Spain", 34));
//        arrayList.add(new CountryData(R.drawable.mexico, "Mexico", 52));

        // Set adapter
        CountryAdapter arrayAdapter = new CountryAdapter(
                this,
                R.layout.country_spinner_item1,
                arrayList
        );
        arrayAdapter.setDropDownViewResource(R.layout.country_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        TextView textView = findViewById(R.id.signup_registerText);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_Up_Activity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dob.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.setOnShowListener(dialog -> {
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.white));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.white));
        });

        datePickerDialog.show();
    }

    private void registerUser() {
        String firstName = fname.getText().toString().trim();
        String lastName = lname.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String mobileInput = mobile.getText().toString().trim();
        String dobInput = dob.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String countryInput = spinner.getSelectedItem().toString().trim();
        String agreeInput = agreebtn.getText().toString().trim();

        if (!validateInputs(firstName, lastName, emailInput, mobileInput, dobInput, passwordInput)) {
            return;
        }

        String title = (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) ? "Mr" :
                (radioGroup.getCheckedRadioButtonId() == R.id.radioButton2) ? "Mrs" : "Miss/Ms";

        CountryData selectedCountry = (CountryData) spinner.getSelectedItem();
        String countryName = selectedCountry != null ? selectedCountry.getCountryname() : "";

        User user = new User(firstName, lastName, emailInput, countryName, mobileInput, dobInput, passwordInput, title);

        // Save user data in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = new Gson().toJson(user);
        editor.putString("user", userJson);
        editor.apply();

        Log.d("JSON", userJson);
        Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to Sign In Activity
        Intent i = new Intent(Sign_Up_Activity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }

    private boolean validateInputs(String firstName, String lastName, String email, String mobile, String dob, String password) {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mobile.isEmpty() || !mobile.matches("\\d{7,15}")) {
            Toast.makeText(this, "Enter a valid mobile number (7-15 digits)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dob.isEmpty()) {
            Toast.makeText(this, "Please select a date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!agreebtn.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    class CountryAdapter extends ArrayAdapter<CountryData> {
        private final List<CountryData> countryDataList;
        private final int layout;

        public CountryAdapter(@NonNull Context context, int resource, @NonNull List<CountryData> objects) {
            super(context, resource, objects);
            this.countryDataList = objects;
            this.layout = resource;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        private View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.country_imageView);
            TextView countryTextView = convertView.findViewById(R.id.spinner_text_view);

            CountryData countryData = countryDataList.get(position);
            imageView.setImageResource(countryData.getFlagResourceId());
            countryTextView.setText(countryData.getCountryname());

            return convertView;
        }
    }


}