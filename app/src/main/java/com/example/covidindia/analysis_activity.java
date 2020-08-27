package com.example.covidindia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class analysis_activity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Activity analysisactivity;
    ProgressBar loading,loading_radio,loading_rec_rate,loading_dec_rate,loading_age;
    RadioGroup radioGroup;
    RadioButton bar_radio_btn;

    protected String URL = "https://covserver.pythonanywhere.com/";

    public analysis_activity(Activity activity) {
        // Required empty public constructor
        analysisactivity=activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_analysis_activity, container, false);
        loading = view.findViewById(R.id.chart_graph_load);
        loading_radio = view.findViewById(R.id.radio_bar_load);
        loading_rec_rate = view.findViewById(R.id.rec_rate_chart_load);
        loading_dec_rate= view.findViewById(R.id.dec_rate_chart_load);
        loading_age = view.findViewById(R.id.age_chart_load);
        try {
            dostategraph(view);
            doradiobargraph(view);
            dorecrategraph(view);
            dodecrategraph(view);
            doagechart(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swiperefreshanalysis);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.simpleframelayout,new analysis_activity(getActivity()),"ANALYSIS_FRAGMENT").commit();
                swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }

    private void doagechart(View view) {
        if (loading_age.getVisibility() == View.GONE){
            loading_age.setVisibility(View.VISIBLE);
        }
        final BarChart myChart = view.findViewById(R.id.age_chart);
        myChart.setDrawBarShadow(false);
        myChart.setDrawValueAboveBar(false);
        myChart.setPinchZoom(false);
        myChart.getDescription().setEnabled(false);
        myChart.getLegend().setEnabled(false);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL+"statistics/age_bar_chart")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONArray myresponse = new JSONArray(response.body().string());
                        ArrayList<BarEntry> yval = new ArrayList<>();
                        final ArrayList<String> Xval = new ArrayList<>();
                        for (int i =0; i< myresponse.length();i++){
                            JSONObject obj = myresponse.getJSONObject(i);
                            Xval.add(obj.getString("age"));
                            yval.add(new BarEntry(i, Float.parseFloat(obj.getString("count"))));
                        }
                        final BarDataSet set = new BarDataSet(yval,"Age");
                        set.setColors(ColorTemplate.MATERIAL_COLORS);
                        set.setValueTextSize(0f);
                        analysisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading_age.setVisibility(View.GONE);
                                myChart.setVisibility(View.VISIBLE);
                                myChart.setData(new BarData(set));
                                myChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Xval));
                                myChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                myChart.getAxisRight().setEnabled(false);
                                myChart.getAxisLeft().setAxisMinimum(0f);
                                myChart.getXAxis().setLabelRotationAngle(-30f);
                                myChart.animateXY(700,800);
                                MarkerviewChart mv = new MarkerviewChart(analysisactivity,R.layout.marker_layout,Xval,"bar");
                                myChart.setMarker(mv);
                                myChart.invalidate();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void dodecrategraph(View view) {
        if (loading_dec_rate.getVisibility() == View.GONE){
            loading_dec_rate.setVisibility(View.VISIBLE);
        }
        MathJaxWebView katexView1;
        katexView1 = (MathJaxWebView) view.findViewById(R.id.dec_rate_chart_formula);
        String text = "$$ \\small D=\\frac{Deceased}{Confirmed}\\times100 $$";
        katexView1.getSettings().setJavaScriptEnabled(true);
        katexView1.setText(text);
        final HorizontalBarChart myChart = view.findViewById(R.id.dec_rate_chart);
        myChart.setDrawBarShadow(false);
        myChart.setDrawValueAboveBar(false);
        myChart.setPinchZoom(false);
        myChart.getDescription().setEnabled(false);
        myChart.getLegend().setEnabled(false);
        OkHttpClient client = new OkHttpClient();
        final RequestBody formBody = new FormBody.Builder()
                .add("rate","deceased")
                .build();
        Request request = new Request.Builder()
                .url(URL+"rec_dec_rate")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONArray myresponse = new JSONArray(response.body().string());
                        final ArrayList<String> Xaxes = new ArrayList<>();
                        ArrayList<BarEntry> yval = new ArrayList<>();
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i =myresponse.length()-1; i>=0;i--){
                            JSONObject obj = myresponse.getJSONObject(i);
                            Xaxes.add(obj.getString("STATE/UT"));
                            yval.add(new BarEntry(myresponse.length()-i-1, Float.parseFloat(obj.getString("Death_rate"))));
                            colors.add(Color.argb(myresponse.length()+i*2,0, 153, 204));
                        }
                        final BarDataSet set = new BarDataSet(yval,"Deceased Rate");
                        set.setColors(colors);
                        set.setValueTextSize(0f);
                        analysisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading_dec_rate.setVisibility(View.GONE);
                                myChart.setVisibility(View.VISIBLE);
                                myChart.setData(new BarData(set));
                                myChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Xaxes));
                                myChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                myChart.animateXY(700,800);
                                MarkerviewChart mv = new MarkerviewChart(analysisactivity,R.layout.marker_layout,Xaxes,"bar");
                                myChart.setMarker(mv);
                                myChart.invalidate();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void dorecrategraph(View view) {
        if (loading_rec_rate.getVisibility() == View.GONE){
            loading_rec_rate.setVisibility(View.VISIBLE);
        }
        MathJaxWebView katexView1;
        katexView1 = (MathJaxWebView) view.findViewById(R.id.rec_rate_chart_formula);
        String text = "$$ \\small R=\\frac{Recovered}{Confirmed}\\times100 $$";
        katexView1.getSettings().setJavaScriptEnabled(true);
        katexView1.setText(text);
        final HorizontalBarChart myChart = view.findViewById(R.id.rec_rate_chart);
        myChart.setDrawBarShadow(false);
        myChart.setDrawValueAboveBar(false);
        myChart.setPinchZoom(false);
        myChart.getDescription().setEnabled(false);
        myChart.getLegend().setEnabled(false);
        OkHttpClient client = new OkHttpClient();
        final RequestBody formBody = new FormBody.Builder()
                .add("rate","recovered")
                .build();
        Request request = new Request.Builder()
                .url(URL+"rec_dec_rate")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONArray myresponse = new JSONArray(response.body().string());
                        final ArrayList<String> Xaxes = new ArrayList<>();
                        ArrayList<BarEntry> yval = new ArrayList<>();
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i =myresponse.length()-1; i>=0;i--){
                            JSONObject obj = myresponse.getJSONObject(i);
                            Xaxes.add(obj.getString("STATE/UT"));
                            yval.add(new BarEntry(myresponse.length()-i-1, Float.parseFloat(obj.getString("recovery_rate"))));
                            colors.add(Color.argb(myresponse.length()+i*2,0, 153, 204));
                        }
                        final BarDataSet set = new BarDataSet(yval,"Recovery Rate");
                        set.setColors(colors);
                        set.setValueTextSize(0f);
                        analysisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading_rec_rate.setVisibility(View.GONE);
                                myChart.setVisibility(View.VISIBLE);
                                myChart.setData(new BarData(set));
                                myChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Xaxes));
                                myChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                myChart.animateXY(700,800);
                                MarkerviewChart mv = new MarkerviewChart(analysisactivity,R.layout.marker_layout,Xaxes,"bar");
                                myChart.setMarker(mv);
                                myChart.invalidate();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void doradiobargraph(final View view) throws JSONException {
        MathJaxWebView katexView1,katexView2;
        katexView1 = (MathJaxWebView)view.findViewById(R.id.radio_bar_chart_caption_1_formula);
        katexView2 = (MathJaxWebView)view.findViewById(R.id.radio_bar_chart_caption_2_formula);
        katexView1.getSettings().setJavaScriptEnabled(true);
        katexView2.getSettings().setJavaScriptEnabled(true);
        String text = "$$ \\small F=\\frac{Confirmed}{Tested}\\times100 $$";
        String text2 = "$$ \\small T=\\frac{TestedPopulation}{Population}\\times1000 $$";
        katexView1.setText(text);
        katexView2.setText(text2);
        radioGroup = view.findViewById(R.id.radiogrp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                bar_radio_btn = view.findViewById(i);
                String text = bar_radio_btn.getText().toString();
                drawRadiobarChart(text,view);


            }
        });
        drawRadiobarChart(null,view);
    }

    private void drawRadiobarChart(String text, final View view) {
        final String formdata;
        final BarChart myChart = view.findViewById(R.id.radio_bar_chart);
        myChart.setDrawBarShadow(false);
        myChart.setDrawValueAboveBar(false);
        myChart.setPinchZoom(false);
        myChart.getDescription().setEnabled(false);
        myChart.getLegend().setEnabled(false);
        final ProgressBar loading = view.findViewById(R.id.radio_bar_load);
        if (loading.getVisibility() == View.GONE){
            loading.setVisibility(View.VISIBLE);
            myChart.setVisibility(View.GONE);
        }
        if (text != null){
            formdata = (text.equals("F-Ratio")) ? "true" : "false";
        }else{
            bar_radio_btn = view.findViewById(radioGroup.getCheckedRadioButtonId());
            String CheckedText = bar_radio_btn.getText().toString();
            if (CheckedText.equals("F-Ratio")){
                formdata = "true";
            }else {
                formdata = "false";
            }
            text= CheckedText;
        }
        OkHttpClient client = new OkHttpClient();
        final RequestBody formBody = new FormBody.Builder()
                .add("ratio_data",formdata)
                .build();
        Request request = new Request.Builder()
                .url(URL+"tested")
                .post(formBody)
                .build();
        final String finalText = text;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.isSuccessful()){
                    try {
                        JSONArray myresponsedata = new JSONArray(response.body().string());
                        ArrayList<BarEntry> e = new ArrayList<>();
                        final ArrayList<String> Xval = new ArrayList<>();
                        ArrayList<Integer> color = new ArrayList<>();
                        for (int i =0;i<myresponsedata.length();i++){
                            JSONObject obj = myresponsedata.getJSONObject(i);
                            if (formdata.equals("true")){
                                e.add(new BarEntry(i, Float.parseFloat(obj.getString("F"))));
                            }else {
                                e.add(new BarEntry(i, Float.parseFloat(obj.getString("test_pop_ratio"))));
                            }
                            Xval.add(obj.getString("state"));
                            color.add(Color.argb(myresponsedata.length()+i*2,0,255,255));
                        }
                        final BarDataSet barDataSet = new BarDataSet(e, finalText);
                        barDataSet.setValueTextSize(0f);
                        barDataSet.setColors(color);
                        analysisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myChart.setData(new BarData(barDataSet));
                                myChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Xval));
                                myChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                MarkerviewChart mv = new MarkerviewChart(analysisactivity,R.layout.marker_layout,Xval,"bar");
                                myChart.setMarker(mv);
                                myChart.getAxisLeft().setAxisMinimum(0f);
                                myChart.animateXY(700,800);
                                myChart.getXAxis().setLabelRotationAngle(-30);
                                myChart.getAxisRight().setEnabled(false);
                                loading.setVisibility(View.GONE);
                                myChart.setVisibility(View.VISIBLE);
                                myChart.invalidate();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void dostategraph(View view) throws JSONException {
        Spinner statespinner = view.findViewById(R.id.state_graph_state_list);
        Spinner dailyspinner = view.findViewById(R.id.state_graph_daily_spinner);
        Spinner condition = view.findViewById(R.id.state_graph_condition_spinner);
        SeekBar seekBar = view.findViewById(R.id.chart_graph_seekbar);
        final TextView seekdata = view.findViewById(R.id.chart_graph_seekbar_data);
        ArrayAdapter<String> stateadapter= new ArrayAdapter<>(analysisactivity,R.layout.spinner_style,getResources().getStringArray(R.array.data_state));
        ArrayAdapter<String> dailyadapter= new ArrayAdapter<>(analysisactivity,R.layout.spinner_style,getResources().getStringArray(R.array.daily_name));
        ArrayAdapter<String> conditionadapter= new ArrayAdapter<>(analysisactivity,R.layout.spinner_style,getResources().getStringArray(R.array.condition_name));
        statespinner.setAdapter(stateadapter);
        dailyspinner.setAdapter(dailyadapter);
        condition.setAdapter(conditionadapter);
        final JSONObject data = new JSONObject();
        data.put("silder_data",seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekdata.setText(String.valueOf(i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    data.put("silder_data",seekBar.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (data.length() == 4){
                    try {
                        loading.setVisibility(View.VISIBLE);
                        drawChartGraph(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String state=adapterView.getSelectedItem().toString();
                try {
                    data.put("state_data",state);
                    if (data.length() == 4){
                        loading.setVisibility(View.VISIBLE);
                        drawChartGraph(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dailyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String daily = adapterView.getSelectedItem().toString();
                try {
                    data.put("daily_data",daily);
                    if (data.length() == 4){
                        loading.setVisibility(View.VISIBLE);
                        drawChartGraph(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String condition = adapterView.getSelectedItem().toString();
                try {
                    data.put("condition_data",condition);
                    if (data.length() == 4){
                        loading.setVisibility(View.VISIBLE);
                        drawChartGraph(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void drawChartGraph(final JSONObject values) throws JSONException{
        final String daily;
        final LineChart myChart = getView().findViewById(R.id.state_linechart);
        if (myChart.getVisibility() == View.VISIBLE){
            myChart.setVisibility(View.GONE);
        }
        if (values.getString("daily_data").equals("Yes")){
             daily="Daily";
        }else{
            daily = "Cumulative";
        }
        myChart.setDragEnabled(false);
        myChart.setScaleEnabled(false);
        myChart.getDescription().setEnabled(false);
//        myChart.getLegend().setXOffset(50f);
        Log.i("msg",values.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("state_data",values.getString("state_data"))
                .add("daily_data",values.getString("daily_data"))
                .add("condition_data",values.getString("condition_data"))
                .add("silder_data",values.getString("silder_data"))
                .build();
        Request request = new Request.Builder()
                .url(URL+"statistics/corona_graph")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONArray responseobj = new JSONArray(response.body().string());
                        final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        List<String> labellist= new ArrayList<String>(){{add("Confirmed");add("Recovered");add("Death");}};
                        List<Integer> color = new ArrayList<Integer>(){{add(Color.BLUE);add(Color.RED);add(Color.GREEN);}};
                        final ArrayList<String> Xaxes = new ArrayList<>();
                        if (values.getString("condition_data").equals("Together")){
                            for (int i=0;i<responseobj.length();i++){
                                ArrayList<Entry> yval = new ArrayList<>();
                                JSONArray array = new JSONArray(responseobj.getString(i));
                                float x=0;
                                JSONObject total = array.getJSONObject(array.length()-1);
                                Iterator<String> keys = total.keys();
                                keys.next();
                                while (keys.hasNext()){
                                    String key = keys.next();
                                    if (Xaxes.size() < total.length()){
                                        Xaxes.add(key);
                                    }
                                    String value = total.getString(key);
                                    yval.add(new Entry(x,Float.parseFloat(value)));
                                    x=x+1;
                                }
                                LineDataSet set = new LineDataSet(yval,values.getString("state_data")+" "+daily+" "+labellist.get(i));
                                set.setDrawCircles(false);
                                set.setValueTextSize(0f);
                                set.setColor(color.get(i));
                                dataSets.add(set);

                            }
                        }else{
                            ArrayList<Entry> yval = new ArrayList<>();
                            float x=0;
                            JSONObject p_data = responseobj.getJSONObject(responseobj.length()-1);
                            Iterator<String> keys = p_data.keys();
                            keys.next();
                            while (keys.hasNext()){
                                String key = keys.next();
                                if (Xaxes.size() < p_data.length()){
                                    Xaxes.add(key);
                                }
                                String value = p_data.getString(key);
                                yval.add(new Entry(x,Float.parseFloat(value)));
                                x=x+1;
                            }
                            LineDataSet set = new LineDataSet(yval,values.getString("state_data")+daily+values.getString("condition_data"));
                            set.setDrawCircles(false);
                            set.setValueTextSize(0f);
                            set.setColor(color.get(1));
                            dataSets.add(set);

                        }


                        analysisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myChart.setData(new LineData(dataSets));
                                myChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Xaxes));
                                myChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//                                myChart.getXAxis().setLabelRotationAngle(-30);
                                myChart.getLegend().setWordWrapEnabled(true);
                                myChart.getXAxis().setTextSize(7f);
                                myChart.getAxisRight().setEnabled(false);
                                myChart.setBackgroundColor(getResources().getColor(R.color.graphback));
                                myChart.setTouchEnabled(true);
                                MarkerviewChart mv = new MarkerviewChart(analysisactivity,R.layout.marker_layout,Xaxes,"line");
                                myChart.setMarker(mv);
                                myChart.getAxisLeft().setAxisMinimum(0f);
                                loading.setVisibility(View.GONE);
                                myChart.setVisibility(View.VISIBLE);
                                myChart.animateXY(700,800);
                                myChart.invalidate();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}