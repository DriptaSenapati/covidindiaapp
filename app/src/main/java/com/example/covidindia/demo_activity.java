package com.example.covidindia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteTableLockedException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PrivateKey;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class demo_activity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Activity demoactivity;
    Spinner state_spinner,district_spinner,city_spinner;
    private boolean DISTRICT_BIAS_COUNT = false;
    private boolean CITY_BIAS_COUNT = false;
    private boolean STATE_BIAS_COUNT = false;
    private String URL = "https://covserver.pythonanywhere.com/";
    EditText date_data;
    Button get_data;
    RelativeLayout tablelayout;
    JSONArray data;

    public demo_activity(Activity activity) {
        // Required empty public constructor
        demoactivity = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public void setDefaultSpinnername(Spinner spinner, SpinnerAdapter adapter, int nothingSelectedLayout, Context context){
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter,
                nothingSelectedLayout,context));
    }
    public static void setMarginLeft(View v, float bias) {
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams)v.getLayoutParams();
        params.horizontalBias = bias;
        v.setLayoutParams(params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo_activity, container, false);
        state_spinner =view.findViewById(R.id.state_spinner_demo);
        tablelayout = view.findViewById(R.id.table_contain_layout_demo);
        district_spinner = view.findViewById(R.id.district_spinner_demo);
        city_spinner = view.findViewById(R.id.city_spinner_demo);
        ArrayAdapter<String> stateadapter= new ArrayAdapter<>(demoactivity,R.layout.spinner_style,getResources().getStringArray(R.array.data_state_demo));
        state_spinner.setAdapter(stateadapter);
        setDefaultSpinnername(state_spinner,stateadapter,R.layout.deafaultnamespinner,demoactivity);
        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OkHttpClient client = new OkHttpClient();
                if (adapterView.getSelectedItem() != null){
                    String state = adapterView.getSelectedItem().toString();
                    if (!state.equals("All")){
                        if (STATE_BIAS_COUNT == false){
                            setMarginLeft(state_spinner,0.07f);
                            setMarginLeft(district_spinner,0.7f);
                            STATE_BIAS_COUNT = true;
                        }
                        final loading Loading = new loading(demoactivity);
                        Loading.startLoading();
                        RequestBody formBody = new FormBody.Builder()
                                .add("place_data",state)
                                .build();
                        Request request = new Request.Builder()
                                .url(URL+"filter")
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                                demoactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(demoactivity,"Oops! can't connect to server!",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (response.isSuccessful()){
                                    final String myresponse = response.body().string();
                                    demoactivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONArray list = new JSONArray(myresponse);
                                                ArrayList<String> dis_name = new ArrayList<>();
                                                for (int i=0;i<list.length();i++){
                                                    dis_name.add(list.getString(i));
                                                }
                                                final ArrayAdapter<String> districtadapter= new ArrayAdapter<>(demoactivity,R.layout.spinner_style,dis_name);
                                                district_spinner.setAdapter(districtadapter);
                                                setDefaultSpinnername(district_spinner,districtadapter,R.layout.deafaultnamespinner_district,demoactivity);
                                                district_spinner.setVisibility(View.VISIBLE);
                                                Loading.dissmiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }else{
                                    demoactivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(demoactivity,"Data not found",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });

                    }else{
                        if (district_spinner.getVisibility() == View.VISIBLE){
                            district_spinner.setVisibility(View.INVISIBLE);
                            setMarginLeft(adapterView,0.50f);
                            STATE_BIAS_COUNT = false;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OkHttpClient client = new OkHttpClient();
                if (adapterView.getSelectedItem() != null){
                    String district = adapterView.getSelectedItem().toString();
                    if (!district.equals("All")){
                        if (DISTRICT_BIAS_COUNT == false){
                            setMarginLeft(district_spinner,0.1f);
                            DISTRICT_BIAS_COUNT = true;
                        }
                        final loading Loading = new loading(demoactivity);
                        Loading.startLoading();
                        RequestBody formBody = new FormBody.Builder()
                                .add("place_data",district)
                                .build();
                        Request request = new Request.Builder()
                                .url(URL+"filter")
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                                Toast.makeText(demoactivity,"Oops! Can't connect to the server!",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (response.isSuccessful()){
                                    final String myresponse = response.body().string();
                                    demoactivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONArray list = new JSONArray(myresponse);
                                                ArrayList<String> City_name = new ArrayList<>();
                                                City_name.add("All");
                                                for (int i=0;i<list.length();i++){
                                                    City_name.add(list.getString(i));
                                                }
                                                final ArrayAdapter<String> cityadapter= new ArrayAdapter<>(demoactivity,R.layout.spinner_style,City_name);
                                                city_spinner.setAdapter(cityadapter);
                                                setDefaultSpinnername(city_spinner,cityadapter,R.layout.deafaultnamespinner_city,demoactivity);
                                                Loading.dissmiss();
                                                city_spinner.setVisibility(View.VISIBLE);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }else{
                        if (city_spinner.getVisibility() == View.VISIBLE){
                            city_spinner.setVisibility(View.INVISIBLE);
                            setMarginLeft(adapterView,0.70f);
                            DISTRICT_BIAS_COUNT = false;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        date_data= view.findViewById(R.id.date_data_demo);
        date_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalenderDialog();
            }
        });
        get_data= view.findViewById(R.id.btn_data_submit_demo);
        get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getjsonData();
            }
        });
        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swiperefreshdemo);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.simpleframelayout,new demo_activity(getActivity()),"DEMOGRAPHY_FRAGMENT").commit();
                swipeContainer.setRefreshing(false);
            }
        });
        return  view;
    }
    private JSONArray generatedata(){
        String place,date;
        JSONArray json =null;
        String state = state_spinner.getSelectedItem().toString();
        date = date_data.getText().toString();
        if (date.matches("")){
            date = "All";
        }
        if (!state.equals("All")){
            String district = district_spinner.getSelectedItem().toString();
            if (!district.equals("All")){
                String city = city_spinner.getSelectedItem().toString();
                if (!city.equals("All")){
                    place = city;
                }else{
                    place = district;
                }
            }else{
                place = state;
            }
        }else {
            place = "All";
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("place_data",place)
                .add("date_day",date)
                .build();
        final Request request = new Request.Builder()
                .url(URL+"Demography")
                .post(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                String myresponse = response.body().string();
                JSONArray jsonArray = new JSONArray(myresponse);
                for (int i =0;i<jsonArray.length();i++){
                    String datestr = jsonArray.getJSONObject(i).getString("dateannounced");
                    Date date_demo = new Date(Long.parseLong(datestr));
                    jsonArray.getJSONObject(i).put("dateannounced",new SimpleDateFormat("yyyy-MM-dd").format(date_demo));
                }
                json = jsonArray;
            }else {
                return null;
            }
        } catch(IOException | JSONException e) {
            return null;
        }
        return json;
    }

    private void getjsonData() {

        if (state_spinner.getSelectedItem() == null){
            Toast.makeText(demoactivity,"Please select a state",Toast.LENGTH_LONG).show();
        }else if (district_spinner.getSelectedItem() == null && !state_spinner.getSelectedItem().toString().equals("All")){
            Toast.makeText(demoactivity,"Please select a district",Toast.LENGTH_LONG).show();
        }else if (city_spinner.getSelectedItem() == null && district_spinner.getSelectedItem() != null){
            if (!district_spinner.getSelectedItem().toString().equals("All")){
                Toast.makeText(demoactivity,"Please select a city",Toast.LENGTH_LONG).show();
            }else{
                new backgroundtask().execute();
            }
        }else{
            new backgroundtask().execute();
        }
    }

    public class backgroundtask extends AsyncTask<Void,Void,JSONArray>{
        final loading Loading = new loading(demoactivity);

        @Override
        protected JSONArray doInBackground(Void... voids) {
            JSONArray array=generatedata();
            return array;
        }

        @Override
        protected void onPreExecute() {
            Loading.startLoading();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(JSONArray array) {
            if (array != null){
                try {
                    tablelayout.removeAllViews();
                    new TableMainLayout(demoactivity,tablelayout,array);
                    Loading.dissmiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(demoactivity,"Data not found!",Toast.LENGTH_LONG).show();
                Loading.dissmiss();
            }


        }
    }

    private void showCalenderDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                demoactivity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String date= i2+"/"+(i1+1)+"/"+i;
                        date_data.setText(date);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        Calendar c = Calendar.getInstance();
        Calendar c_end = Calendar.getInstance();
        c.set(2020,0,30);
        c_end.add(Calendar.DATE,-1);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(c_end.getTimeInMillis());
        datePickerDialog.show();
    }
}