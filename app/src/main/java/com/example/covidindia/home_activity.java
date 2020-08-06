package com.example.covidindia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class home_activity extends Fragment {
    MathJaxWebView mathJaxWebView,highdeathwebview,highrecratewebview;
    String tex= "$$\\Tiny T = \\frac{TotalTested}{Population} \\times 100$$";
    String tex_highdeath= "$$\\Tiny T = \\frac{Deceased}{Confirmed} \\times 100$$";
    String tex_highrec= "$$\\Tiny T = \\frac{Recovered}{Confirmed} \\times 100$$";
    Activity myactivity;
    protected String URL = "https://covserver.pythonanywhere.com/";
    PieChart pieChart,pieChart_rec,pieChart_death;
    TextView confirmtext,recovertext,activetext,deathtext,dash_date,confirmcarddatatext,recovercarddatatext,deathcarddatatext,uptext,uptextrec,
    uptextdeath,uptextactive,fratiodata,highdeathratedata,highrecratedata,highconspikedata,highrecspikedata,highdeathspikedata;
    private SwipeRefreshLayout swipeContainer;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home_activity(Activity activity) {
        // Required empty public constructor
        myactivity=activity;
    }

    public PieChart setPieProperty(PieChart m){
        m.setUsePercentValues(false);
        m.setDrawHoleEnabled(false);
        m.getDescription().setEnabled(false);
        m.setTouchEnabled(true);
        m.setDrawEntryLabels(false);
        m.getLegend().setEnabled(false);
        return m;
    }
    public void DoNetworkTask(){
        final loading Loading = new loading(myactivity);
        Loading.startLoading();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                myactivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(myactivity,"Oops! Can't connect to server",Toast.LENGTH_LONG).show();
                        Loading.dissmiss();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myresponse = response.body().string();
                    myactivity.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(myresponse);
                                confirmtext.setText(jsonObject.getString("conf_number"));
                                recovertext.setText(jsonObject.getString("recover_number"));
                                activetext.setText(jsonObject.getString("active_number"));
                                deathtext.setText(jsonObject.getString("death_number"));
                                dash_date.setText("Till "+jsonObject.getString("date_1")+")");
                                confirmcarddatatext.setText(jsonObject.getString("number_high_conf")+" in "+jsonObject.getString("state_high_conf"));
                                recovercarddatatext.setText(jsonObject.getString("number_high_recover")+" in "+jsonObject.getString("state_high_recover"));
                                deathcarddatatext.setText(jsonObject.getString("number_high_death")+" in "+jsonObject.getString("state_high_death"));
                                uptext.setText(jsonObject.getString("up_text_conf"));
                                uptextrec.setText(jsonObject.getString("up_text_recover"));
                                uptextactive.setText(jsonObject.getString("up_text_active"));
                                uptextdeath.setText(jsonObject.getString("up_text_death"));
                                fratiodata.setText(jsonObject.getString("number_high_conftest")+" in " + jsonObject.getString("state_high_conftest"));
                                highdeathratedata.setText(jsonObject.getString("number_high_deathrate")+" in "+jsonObject.getString("state_high_deathrate"));
                                highrecratedata.setText(jsonObject.getString("number_high_recoverrate")+" in "+jsonObject.getString("state_high_recoverrate"));
                                highconspikedata.setText(jsonObject.getString("high_occur")+" in "+jsonObject.getString("high_occur_date"));
                                highrecspikedata.setText(jsonObject.getString("high_occur_rec")+" in "+jsonObject.getString("high_occur_date_rec"));
                                highdeathspikedata.setText(jsonObject.getString("high_occur_death")+" in "+jsonObject.getString("high_occur_death_date"));
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
                    final String myresponse = Objects.requireNonNull(response.body()).string();
                    myactivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(myresponse);
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
                                MyMarkerView mv = new MyMarkerView(myactivity, R.layout.marker_layout);
                                MyMarkerView mv_rec = new MyMarkerView(myactivity, R.layout.marker_layout);
                                MyMarkerView mv_death = new MyMarkerView(myactivity, R.layout.marker_layout);
                                pieChart.setMarker(mv);
                                pieChart_rec.setMarker(mv_rec);
                                pieChart_death.setMarker(mv_death);
                                pieChart.invalidate();
                                pieChart_rec.invalidate();
                                pieChart_death.invalidate();
                                Loading.dissmiss();
                                swipeContainer.setRefreshing(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    myactivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(myactivity,"Oops! Can't connect to server",Toast.LENGTH_LONG).show();
                            Loading.dissmiss();
                        }
                    });

                }
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_activity, container, false);
        confirmtext = view.findViewById(R.id.confirmtext);
        recovertext = view.findViewById(R.id.recovertext);
        activetext = view.findViewById(R.id.activetext);
        swipeContainer=view.findViewById(R.id.swipeView);
        deathtext = view.findViewById(R.id.deathtext);
        dash_date=view.findViewById(R.id.dash_date);
        uptext=view.findViewById(R.id.uptext);
        uptextrec=view.findViewById(R.id.uptextrec);
        uptextactive=view.findViewById(R.id.uptextactive);
        uptextdeath=view.findViewById(R.id.uptextdeath);
        confirmcarddatatext=view.findViewById(R.id.confirmcardDatatext);
        recovercarddatatext=view.findViewById(R.id.recovercardDatatext);
        deathcarddatatext=view.findViewById(R.id.deathcardDatatext);
        pieChart = view.findViewById(R.id.piechartConfirm);
        pieChart_rec = view.findViewById(R.id.piechartrecover);
        pieChart_death = view.findViewById(R.id.piechartDeath);
        fratiodata=view.findViewById(R.id.fratiodata);
        highdeathratedata=view.findViewById(R.id.highdeathratedata);
        highrecratedata=view.findViewById(R.id.highrecratedata);
        highconspikedata=view.findViewById(R.id.highconspikedata);
        highrecspikedata=view.findViewById(R.id.highrecspikedata);
        highdeathspikedata=view.findViewById(R.id.highdeathspikedata);
        mathJaxWebView =(MathJaxWebView)view.findViewById(R.id.webviewFration);
        highdeathwebview=(MathJaxWebView)view.findViewById(R.id.highdeathwebview);
        highrecratewebview=(MathJaxWebView)view.findViewById(R.id.highrecratewebview);
        mathJaxWebView.getSettings().setJavaScriptEnabled(true);
        highdeathwebview.getSettings().setJavaScriptEnabled(true);
        highrecratewebview.getSettings().setJavaScriptEnabled(true);
        mathJaxWebView.setText(tex);
        highdeathwebview.setText(tex_highdeath);
        highrecratewebview.setText(tex_highrec);
        DoNetworkTask();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DoNetworkTask();
            }
        });
        return  view;
    }
}