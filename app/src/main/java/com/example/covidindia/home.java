package com.example.covidindia;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class home extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    protected String URL = "https://covserver.pythonanywhere.com/";
    PieChart pieChart,pieChart_rec,pieChart_death;
    TextView confirmtext,recovertext,activetext,deathtext,dash_date,confirmcarddatatext,recovercarddatatext,deathcarddatatext;

    public PieChart setPieProperty(PieChart m){
        m.setUsePercentValues(false);
        m.setDrawHoleEnabled(false);
        m.getDescription().setEnabled(false);
        m.setTouchEnabled(true);
        m.setDrawEntryLabels(false);
        m.getLegend().setEnabled(false);
        return m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final loading Loading = new loading(home.this);
        Loading.startLoading();

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_home);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myresponse = response.body().string();
                    home.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(myresponse);
                                confirmtext = findViewById(R.id.confirmtext);
                                recovertext = findViewById(R.id.recovertext);
                                activetext = findViewById(R.id.activetext);
                                deathtext = findViewById(R.id.deathtext);
                                dash_date=findViewById(R.id.dash_date);
                                confirmcarddatatext=findViewById(R.id.confirmcardDatatext);
                                recovercarddatatext=findViewById(R.id.recovercardDatatext);
                                deathcarddatatext=findViewById(R.id.deathcardDatatext);
                                confirmtext.setText(jsonObject.getString("conf_number"));
                                recovertext.setText(jsonObject.getString("recover_number"));
                                activetext.setText(jsonObject.getString("active_number"));
                                deathtext.setText(jsonObject.getString("death_number"));
                                dash_date.setText("Till "+jsonObject.getString("date_1")+")");
                                confirmcarddatatext.setText(jsonObject.getString("number_high_conf")+" in "+jsonObject.getString("state_high_conf"));
                                recovercarddatatext.setText(jsonObject.getString("number_high_recover")+" in "+jsonObject.getString("state_high_recover"));
                                deathcarddatatext.setText(jsonObject.getString("number_high_death")+" in "+jsonObject.getString("state_high_death"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
        OkHttpClient client_chart = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");
        JSONObject bodydata =new JSONObject();
        try {
            bodydata.put("state_data","All");
            bodydata.put("district_data","none");
            bodydata.put("date_data","All");
            bodydata.put("daily_data","No");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = new FormBody.Builder()
                .add("state_data","All")
                .add("district_data","none")
                .add("date_data","All")
                .add("daily_data","No")
                .build();
        Request request_chart = new Request.Builder()
                .url(URL+"State")
                .post(formBody)
                .build();
        client_chart.newCall(request_chart).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myresponse = response.body().string();
                        home.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jsonArray = new JSONArray(myresponse);
                                    pieChart = findViewById(R.id.piechartConfirm);
                                    pieChart_rec = findViewById(R.id.piechartrecover);
                                    pieChart_death = findViewById(R.id.piechartDeath);
                                    pieChart = setPieProperty(pieChart);
                                    pieChart_rec = setPieProperty(pieChart_rec);
                                    pieChart_death = setPieProperty(pieChart_death);
                                    //                        pieChart.setExtraOffsets(5,10,5,5);
                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                                    ArrayList<PieEntry> pieEntries_rec = new ArrayList<>();
                                    ArrayList<PieEntry> pieEntries_death = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length() - 1; i++) {
                                        JSONObject json = jsonArray.getJSONObject(i);
                                        pieEntries.add(new PieEntry(Float.parseFloat(json.getString("Total Confirmed")), json.getString("STATE/UT")));
                                        pieEntries_rec.add(new PieEntry(Float.parseFloat(json.getString("Total Recovered")), json.getString("STATE/UT")));
                                        pieEntries_death.add(new PieEntry(Float.parseFloat(json.getString("Total Death")), json.getString("STATE/UT")));
                                    }
                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "Confirmed cases in India");
                                    PieDataSet pieDataSet_rec = new PieDataSet(pieEntries_rec, "Recovered cases in India");
                                    PieDataSet pieDataSet_death = new PieDataSet(pieEntries_death, "Deceased cases in India");
                                    pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                    pieDataSet_rec.setColors(ColorTemplate.MATERIAL_COLORS);
                                    pieDataSet_death.setColors(ColorTemplate.MATERIAL_COLORS);
                                    PieData data = new PieData(pieDataSet);
                                    PieData data_rec = new PieData(pieDataSet_rec);
                                    PieData data_death = new PieData(pieDataSet_death);
                                    data.setValueTextSize(0f);
                                    data_rec.setValueTextSize(0f);
                                    data_death.setValueTextSize(0f);
                                    pieChart.setData(data);
                                    pieChart_rec.setData(data_rec);
                                    pieChart_death.setData(data_death);
                                    MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.marker_layout);
                                    MyMarkerView mv_rec = new MyMarkerView(getApplicationContext(), R.layout.marker_layout);
                                    MyMarkerView mv_death = new MyMarkerView(getApplicationContext(), R.layout.marker_layout);
                                    pieChart.setMarker(mv);
                                    pieChart_rec.setMarker(mv_rec);
                                    pieChart_death.setMarker(mv_death);
                                    pieChart.invalidate();
                                    pieChart_rec.invalidate();
                                    pieChart_death.invalidate();
                                    Loading.dissmiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        Log.i("msg4", "Failed");
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }


}