package com.example.covidindia;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class data_activity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity dataactivity;
    Spinner statespinner,districtspinner,dailyspinner;
    EditText date_data;
    public Boolean BIAS_ADJUST = false;
    String datestart,dateend;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected String URL = "https://covserver.pythonanywhere.com/";

    public data_activity(Activity activity) {
        // Required empty public constructor
        dataactivity=activity;
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

    public void showCalenderDialog(String a , String b){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                dataactivity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String date= i2+"/"+i1+"/"+i;
                        date_data.setText(date);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        Calendar c = Calendar.getInstance();
        Calendar c_end = Calendar.getInstance();
        if (a != null && b != null){
            int Year = Integer.parseInt(a.split("-")[0]);
            int monthstart = Integer.parseInt(a.split("-")[1]);
            int monthend = Integer.parseInt(b.split("-")[1]);
            int datestart = Integer.parseInt(a.split("-")[2]);
            int dateend = Integer.parseInt(b.split("-")[2]);
            c.set(Year, monthstart-1, datestart);
            c_end.set(Year, monthend-1, dateend);
        }else{
            c.set(2020,0,30);
            c_end.add(Calendar.DATE,-1);
        }
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(c_end.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_activity, container, false);
        statespinner=view.findViewById(R.id.state_spinner);
        districtspinner=view.findViewById(R.id.district_spinner);
        dailyspinner=view.findViewById(R.id.daily_data);
        ArrayAdapter<String> stateadapter= new ArrayAdapter<>(dataactivity,R.layout.spinner_style,getResources().getStringArray(R.array.data_state));
        ArrayAdapter<String> dailyadapter= new ArrayAdapter<>(dataactivity,R.layout.spinner_style,getResources().getStringArray(R.array.daily_name));
        statespinner.setAdapter(stateadapter);
        dailyspinner.setAdapter(dailyadapter);
        setDefaultSpinnername(statespinner,stateadapter,R.layout.deafaultnamespinner,dataactivity);
        setDefaultSpinnername(dailyspinner,dailyadapter,R.layout.deafaultnamespinner_daily,dataactivity);

        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem() != null){
                    String selectedstate= adapterView.getSelectedItem().toString();
                    Log.i("msg",selectedstate);
                    if (!selectedstate.equals("All")){
                        districtspinner.setVisibility(View.VISIBLE);
                        if (BIAS_ADJUST == false){
                            setMarginLeft(adapterView,0.153f);
                            BIAS_ADJUST = true;
                        }
                        final loading Loading = new loading(dataactivity);
                        Loading.startLoading();
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("place_data",selectedstate)
                                .build();
                        RequestBody formBody_date = new FormBody.Builder()
                                .add("state",selectedstate)
                                .build();
                        Request request = new Request.Builder()
                                .url(URL+"filter")
                                .post(formBody)
                                .build();
                        Request request_date = new Request.Builder()
                                .url(URL+"date/state")
                                .post(formBody_date)
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
                                    dataactivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONArray list= new JSONArray(myresponse);
                                                Log.i("msg2",list.getString(0));
                                                ArrayList<String> dis_name = new ArrayList<>();
                                                for (int i=0;i<list.length();i++){
                                                    if (i == 0){
                                                        dis_name.add("All");
                                                    }else {
                                                        dis_name.add(list.getString(i));
                                                    }
                                                }
                                                final ArrayAdapter<String> districtadapter= new ArrayAdapter<>(dataactivity,R.layout.spinner_style,dis_name);
                                                districtspinner.setAdapter(districtadapter);
                                                setDefaultSpinnername(districtspinner,districtadapter,R.layout.deafaultnamespinner_district,dataactivity);
                                                Loading.dissmiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }
                            }
                        });
                        client.newCall(request_date).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (response.isSuccessful()){
                                    final String myresponse_date = response.body().string();
                                    dataactivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONArray date_list = new JSONArray(myresponse_date);
                                                datestart = date_list.getString(0);
                                                dateend = date_list.getString(1);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        if (districtspinner.getVisibility() == View.VISIBLE){
                            districtspinner.setVisibility(View.INVISIBLE);
                            setMarginLeft(adapterView,0.50f);
                            BIAS_ADJUST = false;
                            datestart = null;
                            dateend=null;
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        date_data=view.findViewById(R.id.date_data);
        dailyspinner=view.findViewById(R.id.daily_data);
        date_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalenderDialog(datestart, dateend);
            }
        });
        return  view;
    }

}