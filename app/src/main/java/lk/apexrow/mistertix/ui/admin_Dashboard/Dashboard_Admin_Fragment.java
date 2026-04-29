package lk.apexrow.mistertix.ui.admin_Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import lk.apexrow.mistertix.R;

public class Dashboard_Admin_Fragment extends Fragment {
double totalSalesAmount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard__admin_, container, false);
    }
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    TextView totalMovies,totalSales ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calculateMovies();
        calculateSales();
        totalMovies= view.findViewById(R.id.firebase_total_Movies);
       totalSales = view.findViewById(R.id.firebase_total_sale);

        firestore.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        PieChart pieChart = view.findViewById(R.id.pieChart1);
                        HashMap<String, Integer> languageCountMap = new HashMap<>();

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String language = documentSnapshot.getString("language");

                            if (language != null) {
                                languageCountMap.put(language, languageCountMap.getOrDefault(language, 0) + 1);
                            }
                        }

                        if (languageCountMap.isEmpty()) {
                            Log.i("MisterTix", "No data available for pie chart.");
                            return;
                        }

                        ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();
                        ArrayList<Integer> colors = new ArrayList<>();
                        Random random = new Random();

                        for (Map.Entry<String, Integer> entry : languageCountMap.entrySet()) {
                            pieEntryArrayList.add(new PieEntry(entry.getValue(), entry.getKey()));

                            // Generate random colors
                            int color = Color.rgb(
                                    random.nextInt(256),
                                    random.nextInt(256),
                                    random.nextInt(256)
                            );
                            colors.add(color);
                        }

                        PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "");
                        pieDataSet.setColors(colors);

                        pieDataSet.setValueTextSize(18);
                        pieDataSet.setValueTextColor(Color.WHITE); // Set PieChart values to white

                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);
                        pieChart.setCenterText("Movies by Language");
                        pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.theme_color));
                        pieChart.setCenterTextSize(18);
                        pieChart.animateY(2000, Easing.EaseInCirc);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.invalidate(); // Refresh the chart

                        // Set legend text color to white
                        Legend legend = pieChart.getLegend();
                        legend.setTextColor(Color.WHITE);
                    } else {
                        Log.e("MisterTix", "Firestore query failed.");
                    }
                });


    }

    private void calculateMovies() {
        AtomicInteger sales = new AtomicInteger(0);
        firestore.collection("movies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            totalMovies.setText(String.valueOf(count));

                        } else {
                            totalMovies.setText("Error loading data");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void calculateSales() {
        firestore.collection("bookings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                             totalSalesAmount = 0.0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double amount = Double.parseDouble(document.getString("total_price"));

                                if (amount != 0) {
                                    totalSalesAmount += amount;
                                }
                            }

                            totalSales.setText("LKR. " + String.format("%.2f", totalSalesAmount));
                        } else {
                            totalSales.setText("Error loading data");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        totalSales.setText("Failed to retrieve data");
                        Log.e("MisterTix", "Error fetching sales data", e);
                    }
                });
    }



}
