package lk.apexrow.mistertix.ui.booking_Payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lk.apexrow.mistertix.MainActivity;
import lk.apexrow.mistertix.R;
import lk.apexrow.mistertix.model.User;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Bookin_payment_Activity extends AppCompatActivity {
    private String title, showTime, language, genre;
    private TextView date, movieName, selectseat, selectedShowtime, total_price;
    private EditText email, fname, lname, phone;
    private Button confirmButton;
    private static final int PAYHERE_REQUEST = 11001;
    private List<String> selectedSeats = Arrays.asList("A1", "B1", "C3");
    private String bookingId, formattedDate, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookin_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SharedPreferences paymentPreferences = getSharedPreferences("lk.apexrow.mistertix.userdata", Context.MODE_PRIVATE);
        String userJson = paymentPreferences.getString("user", null);
        userEmail = paymentPreferences.getString("email", "unknown_user");

        fname = findViewById(R.id.firstname_payment);
        lname = findViewById(R.id.lastname_payment);
        email = findViewById(R.id.email_payment);
        phone = findViewById(R.id.mobile_payment);

        if (userJson != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            fname.setText(user.getFname());
            lname.setText(user.getLname());
            email.setText(user.getEmail());
            phone.setText(user.getMobile());
        }

        movieName = findViewById(R.id.movie_name_seat_booking_bottompayment);
        selectedShowtime = findViewById(R.id.Select_Time_and_date_seat_booking_bottom_payment);
        date = findViewById(R.id.Date_seat_booking);
        total_price = findViewById(R.id.seat_booking_calculate_price_bottom_payment);
        selectseat = findViewById(R.id.calculate_ticket_count_payment);
        confirmButton = findViewById(R.id.confirmButton_payment);

        Intent intentAll = getIntent();
        if (intentAll != null) {
            title = intentAll.getStringExtra("movieName");
            showTime = intentAll.getStringExtra("selectedShowtime");
            double totalPrice = intentAll.getDoubleExtra("totalPrice", 0.0);
            selectedSeats = intentAll.getStringArrayListExtra("selectedSeats");
            language = intentAll.getStringExtra("language");
            genre = intentAll.getStringExtra("genre");

            formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            date.setText(formattedDate);
            movieName.setText(title);
            selectedShowtime.setText(showTime);
            total_price.setText(String.format("%.2f", totalPrice));

            if (selectedSeats != null) {
                selectseat.setText(String.valueOf(selectedSeats.size()));
            } else {
                selectedSeats = new ArrayList<>();
                selectseat.setText("0");
            }
        }

        confirmButton.setOnClickListener(v -> initiatePayment());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initiatePayment() {
        double amount = Double.parseDouble(total_price.getText().toString());

        InitRequest req = new InitRequest();
        req.setMerchantId("1221751");
        req.setCurrency("LKR");
        req.setAmount(amount);
        req.setOrderId(UUID.randomUUID().toString());
        req.setItemsDescription("Movie Ticket");

        req.getCustomer().setFirstName(fname.getText().toString());
        req.getCustomer().setLastName(lname.getText().toString());
        req.getCustomer().setEmail(email.getText().toString());
        req.getCustomer().setPhone(phone.getText().toString());

        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        req.getItems().add(new Item(null, "Movie Ticket", 1, amount));

        Intent intent = new Intent(Bookin_payment_Activity.this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST);

        bookingId = UUID.randomUUID().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && resultCode == RESULT_OK && data != null) {
            String jsonResponse = data.getStringExtra(PHConstants.INTENT_EXTRA_RESULT);
            Log.d("Payment", "Payment Response: " + jsonResponse);
            saveOrderToFirebase();
            sendInvoice();
        } else {
            Log.d("Payment", "Payment failed or cancelled.");
        }
    }

    private void saveOrderToFirebase() {
        Map<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("userEmail", email.getText().toString());
        bookingMap.put("BookingID", bookingId);
        bookingMap.put("currentDate", formattedDate);
        bookingMap.put("Movie_name", title);
        bookingMap.put("showtime", showTime);
        bookingMap.put("total_price", total_price.getText().toString());
        bookingMap.put("seats", selectedSeats);
        bookingMap.put("language", language);
        bookingMap.put("genre", genre);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings").document(bookingId).set(bookingMap)
                .addOnSuccessListener(unused -> Log.i("Mistertix App", "Order saved successfully"))
                .addOnFailureListener(e -> Log.i("Mistertix App", "Error saving Booking: " + e.getMessage()));
        Intent intent = new Intent(Bookin_payment_Activity.this, MainActivity.class);
        startActivity(intent);

    }

    private void sendInvoice() {
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            JSONObject invoiceData = new JSONObject();
            invoiceData.put("firstName", fname.getText().toString());
            invoiceData.put("lastName", lname.getText().toString());
            invoiceData.put("email", email.getText().toString());
            invoiceData.put("bookingId", bookingId);
            invoiceData.put("totalPrice", total_price.getText().toString());
            invoiceData.put("mobile", phone.getText().toString().trim());
            invoiceData.put("showtime", selectedShowtime.getText().toString().trim());
            invoiceData.put("date", formattedDate);
            invoiceData.put("movieName", movieName.getText().toString().trim());

            JSONArray seatArray = new JSONArray(selectedSeats);

            invoiceData.put("seats", seatArray);

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(invoiceData.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url("https://9e1f-112-134-244-47.ngrok-free.app/Mistertix/SendEmail")
                    .post(requestBody)
                    .build();

            new Thread(() -> {
                try (Response response = okHttpClient.newCall(request).execute()) {
                    Log.i("Invoice", "Response: " + response.body().string());
                } catch (Exception e) {
                    Log.e("Invoice", "Failed to send invoice", e);
                }
            }).start();
        } catch (Exception e) {
            Log.e("Invoice", "Error creating JSON", e);
        }
    }

}
