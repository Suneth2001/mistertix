package lk.apexrow.mistertix.ui.seat_Booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lk.apexrow.mistertix.R;

public class SeatViewActivity extends AppCompatActivity {

    private GridLayout seatsGrid;
    private Set<String> selectedSeats, bookedSeats;
    private TextView selectedSeatsCount, movieName, showTime, date;
    private TextView showtime1, showtime2, showtime3, showtime4, showtime5;
    private Button confirmButton;
    private TextView calculatePrice;
    private String title, selectedShowtime,language,genre;

    private String movieshowtime1, movieshowtime2, movieshowtime3, movieshowtime4, movieshowtime5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_view);

        // Initialize UI Elements
        movieName = findViewById(R.id.movie_name_seat_booking_bottom);
        showTime = findViewById(R.id.Select_Time_and_date_seat_booking_bottom);
        date = findViewById(R.id.Date_seat_booking);
        seatsGrid = findViewById(R.id.seatsGrid);
        confirmButton = findViewById(R.id.confirmButton);
        selectedSeatsCount = findViewById(R.id.selecting_seat_count);
        calculatePrice = findViewById(R.id.seat_booking_calculate_price_bottom);
        showtime1 = findViewById(R.id.booking_showtime1);
        showtime2 = findViewById(R.id.booking_showtime2);
        showtime3 = findViewById(R.id.booking_showtime3);
        showtime4 = findViewById(R.id.booking_showtime4);
        showtime5 = findViewById(R.id.booking_showtime5);


        selectedSeats = new HashSet<>();
        bookedSeats = new HashSet<>();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seat_view_activity_design), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent seatViewIntent = getIntent();
        if (seatViewIntent != null) {
            title = seatViewIntent.getStringExtra("movieName");
            selectedShowtime = seatViewIntent.getStringExtra("selectedShowtime");
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            movieshowtime1 = seatViewIntent.getStringExtra("showtime1");
            movieshowtime2 = seatViewIntent.getStringExtra("showtime2");
            movieshowtime3 = seatViewIntent.getStringExtra("showtime3");
            movieshowtime4 = seatViewIntent.getStringExtra("showtime4");
            movieshowtime5 = seatViewIntent.getStringExtra("showtime5");
            language = seatViewIntent.getStringExtra("language");
            genre = seatViewIntent.getStringExtra("genre");



            showtime1.setText(movieshowtime1);
            showtime2.setText(movieshowtime2);
            showtime3.setText(movieshowtime3);
            showtime4.setText(movieshowtime4);
            showtime5.setText(movieshowtime5);


            movieName.setText(title);
            showTime.setText(selectedShowtime);
            date.setText(selectedDate);


            fetchBookedSeats(title, selectedShowtime, selectedDate);
        }

        // Set up confirm button
        setupConfirmButton();
    }

    private void generateSeats() {
        int rows = 8; // A to H
        int cols = 10; // 1 to 10
        seatsGrid.removeAllViews();

        for (int i = 0; i < rows; i++) {
            for (int l = 0; l < cols; l++) {

                View seatView = LayoutInflater.from(this).inflate(R.layout.seat_item_layout, seatsGrid, false);
                View seat = seatView.findViewById(R.id.seatView);


                final String seatId = String.format("%c%d", (char) ('A' + i), l + 1);
                seat.setTag(seatId);

                if (bookedSeats.contains(seatId)) {

                    seat.setBackgroundResource(R.drawable.seat_unavailable);
                    seat.setClickable(false);
                } else {

                    seat.setOnClickListener(v -> toggleSeatSelection(v, seatId));
                }


                seatsGrid.addView(seatView);
            }
        }
    }

    private void toggleSeatSelection(View seatView, String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seatView.setBackgroundResource(R.drawable.seat_available);
        } else {
            selectedSeats.add(seatId);
            seatView.setBackgroundResource(R.drawable.seat_selected);
        }

        updateConfirmButtonText();
        setCalculatePrice();

        StringBuilder seatsString = new StringBuilder();
        for (String seat : selectedSeats) {
            seatsString.append(seat).append(", ");
        }

        if (seatsString.length() > 0) {
            seatsString.setLength(seatsString.length() - 2);
        }

        Toast.makeText(this, "Selected seats: " + seatsString, Toast.LENGTH_LONG).show();
    }

    private void updateConfirmButtonText() {
        String buttonText = String.format("Confirm Seats (%d)", selectedSeats.size());
        confirmButton.setText(buttonText);
        String selectSeatText = String.format("Regular (%d)", selectedSeats.size());
        selectedSeatsCount.setText(selectSeatText);
    }

    private void setCalculatePrice() {
        int seatCount = selectedSeats.size();
        double totalPrice = seatCount * 700.00;
        String calPrice = String.format("%.2f Rs", totalPrice);
        calculatePrice.setText(calPrice);
    }

    private void setupConfirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToPayment();
                updateConfirmButtonText();
            }
        });
    }

    private void proceedToPayment() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate Total Price
        double totalPrice = selectedSeats.size() * 700.00;
        totalPrice = Math.round(totalPrice * 100.0) / 100.0;  // Ensures 2 decimal places

        // Start Payment Activity
        Intent intentAll = new Intent(this, lk.apexrow.mistertix.ui.booking_Payment.Bookin_payment_Activity.class);
        intentAll.putStringArrayListExtra("selectedSeats", new ArrayList<>(selectedSeats));
        intentAll.putExtra("totalPrice", totalPrice);
        intentAll.putExtra("movieName", title);
        intentAll.putExtra("selectedShowtime", selectedShowtime);
        intentAll.putStringArrayListExtra("seats", new ArrayList<>(selectedSeats));
        intentAll.putExtra("language", language);
        intentAll.putExtra("genre", genre);

        startActivity(intentAll);
    }



    private void fetchBookedSeats(String movie, String showtime, String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .whereEqualTo("Movie_name", movie)
                .whereEqualTo("showtime", showtime)
                .whereEqualTo("currentDate", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        List<String> seats = (List<String>) document.get("seats");
                        if (seats != null) {
                            bookedSeats.addAll(seats);
                        }
                    }
                    generateSeats(); // Generate seat grid after fetching booked seats
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching booked seats", e));
    }
}