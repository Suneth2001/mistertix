package lk.javainstitute.mrgangani.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lk.javainstitute.mrgangani.R;
import lk.javainstitute.mrgangani.activity.HomeActivity;

public class AdminDashboardFragment extends Fragment {
    BarChart barChart;
    PieChart pieChart;

    FirebaseFirestore firestore;

    TextView totalSales, totalProducts, totalCustomers;

    Button advancedMenu, logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();

        barChart = view.findViewById(R.id.bar_chart);
        pieChart = view.findViewById(R.id.pie_chart);
        totalSales = view.findViewById(R.id.totalSales);
        totalProducts = view.findViewById(R.id.totalProducts);
        totalCustomers = view.findViewById(R.id.totalCustomers);
        logoutButton = view.findViewById(R.id.adminLogoutButton);

        calculateSales();
        calculateProducts();
        calculateCustomers();
        loadBarchart();
        loadPiechart();


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("lk.javainstitute.mrgangani.data", Context.MODE_PRIVATE);

                sharedPreferences.edit().clear().apply();

                Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_LONG).show();

                startActivity(new Intent(requireContext(), HomeActivity.class));
                getActivity().finish();
            }
        });
    }


    private void calculateSales() {
        AtomicInteger sales = new AtomicInteger(0);
        firestore.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String amount = document.getString("amount");
                                sales.addAndGet(Integer.parseInt(amount));
                            }
                            totalSales.setText("Rs." + String.valueOf(sales.get()));

                        } else {
                            totalSales.setText("Error loading data");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void calculateProducts() {
        firestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();

                            totalProducts.setText(String.valueOf(count));

                        } else {
                            totalProducts.setText("Error loading data");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void calculateCustomers() {
        AtomicInteger sales = new AtomicInteger(0);
        firestore.collection("customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            totalCustomers.setText(String.valueOf(count));

                        } else {
                            totalCustomers.setText("Error loading data");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void loadBarchart() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        HashMap<String, Integer> monthlySales = new HashMap<>();

        for (int i = 0; i < 12; i++) {
            monthlySales.put(String.format("%02d", i + 1), 0);
        }

        firestore.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()
                            ) {
                                String date = documentSnapshot.getString("date");
                                Integer amount = Integer.parseInt(documentSnapshot.getString("amount"));

                                String month = date.split("-")[1];

                                monthlySales.put(month, monthlySales.getOrDefault(month, 0) + amount);
                            }
                            updateBarchart(monthlySales);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateBarchart(HashMap<String, Integer> monthlySales) {
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < 12; i++) {
            String month = String.format("%02d", i + 1);
            Integer sales = monthlySales.getOrDefault(month, 0);

            barEntryArrayList.add(new BarEntry(i, sales));
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Monthly Sales");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.invalidate();


        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return months[(int) value];
            }
        });
    }

    private void loadPiechart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(110);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        HashMap<String, Integer> flavorSales = new HashMap<>();

        firestore.collection("orders")
                .get()
                .addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful() && orderTask.getResult() != null) {

                        for (QueryDocumentSnapshot orderDoc : orderTask.getResult()) {
                            HashMap<String, Object> products = (HashMap<String, Object>) orderDoc.get("products");

                            if (products != null) {
                                for (Map.Entry<String, Object> entry : products.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        HashMap<String, Object> productData = (HashMap<String, Object>) entry.getValue();

                                        String flavor = (String) productData.get("flavor");
                                        String quantityStr = (String) productData.get("quantity");

                                        if (flavor != null && quantityStr != null) {
                                            try {
                                                int quantity = Integer.parseInt(quantityStr); // Convert quantity to integer
                                                flavorSales.put(flavor, flavorSales.getOrDefault(flavor, 0) + quantity);
                                            } catch (NumberFormatException e) {
                                                Log.e("FirestoreError", "Invalid quantity format: " + quantityStr);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        updatePiechart(flavorSales);
                    }
                });

    }


    private void updatePiechart(HashMap<String, Integer> flavorSales) {
        ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : flavorSales.entrySet()
        ) {
            pieEntryArrayList.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntryArrayList, "Sales by Flavor");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}