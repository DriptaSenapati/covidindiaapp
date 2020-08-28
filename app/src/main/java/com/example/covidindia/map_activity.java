package com.example.covidindia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class map_activity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    protected String URL = "https://covserver.pythonanywhere.com/";
    Activity mapactivity;
    WebView mapview;
    JSONArray stateData,latLng;
    public map_activity(Activity activity) {
        // Required empty public constructor
        mapactivity = activity;
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
        View view = inflater.inflate(R.layout.fragment_map_activity, container, false);
        try {
            getData(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swiperefreshmap);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.simpleframelayout,new map_activity(getActivity()),"MAP_FRAGMENT").commit();
                swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }
    @SuppressLint("SetJavaScriptEnabled")
    public void getData(final View view) throws JSONException{
        final loading Loading = new loading(mapactivity);
        Loading.startLoading();
        OkHttpClient client = new OkHttpClient();
        final RequestBody formBody = new FormBody.Builder()
                .add("state_data","All")
                .add("district_data","none")
                .add("date_data","All")
                .add("daily_data","No")
                .build();
        Request request = new Request.Builder()
                .url(URL+"State")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()){
                    mapactivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Loading.dissmiss();
                                stateData = new JSONArray(response.body().string());
                                String latlng = "[{\"STATE\\/UT\":\"Andaman and Nicobar Islands\",\"CODE\":\"an\",\"LATITUDE\":11.7401,\"LONGITUDE\":92.6586},{\"STATE\\/UT\":\"Andhra Pradesh\",\"CODE\":\"ap\",\"LATITUDE\":15.9129,\"LONGITUDE\":79.74},{\"STATE\\/UT\":\"Arunachal Pradesh\",\"CODE\":\"ar\",\"LATITUDE\":28.218,\"LONGITUDE\":94.7278},{\"STATE\\/UT\":\"Assam\",\"CODE\":\"as\",\"LATITUDE\":26.2006,\"LONGITUDE\":92.9376},{\"STATE\\/UT\":\"Bihar\",\"CODE\":\"br\",\"LATITUDE\":25.0961,\"LONGITUDE\":85.3131},{\"STATE\\/UT\":\"Chandigarh\",\"CODE\":\"ch\",\"LATITUDE\":30.7333,\"LONGITUDE\":76.7794},{\"STATE\\/UT\":\"Chhattisgarh\",\"CODE\":\"ct\",\"LATITUDE\":21.2787,\"LONGITUDE\":81.8661},{\"STATE\\/UT\":\"Dadra and Nagar Haveli\",\"CODE\":\"dn\",\"LATITUDE\":20.1809,\"LONGITUDE\":73.0169},{\"STATE\\/UT\":\"Daman and Diu\",\"CODE\":\"dd\",\"LATITUDE\":20.4283,\"LONGITUDE\":72.8397},{\"STATE\\/UT\":\"Delhi\",\"CODE\":\"dl\",\"LATITUDE\":28.7041,\"LONGITUDE\":77.1025},{\"STATE\\/UT\":\"Goa\",\"CODE\":\"ga\",\"LATITUDE\":15.2993,\"LONGITUDE\":74.124},{\"STATE\\/UT\":\"Gujarat\",\"CODE\":\"gj\",\"LATITUDE\":22.2587,\"LONGITUDE\":71.1924},{\"STATE\\/UT\":\"Haryana\",\"CODE\":\"hr\",\"LATITUDE\":29.0588,\"LONGITUDE\":76.0856},{\"STATE\\/UT\":\"Himachal Pradesh\",\"CODE\":\"hp\",\"LATITUDE\":31.1048,\"LONGITUDE\":77.1734},{\"STATE\\/UT\":\"Jammu and Kashmir\",\"CODE\":\"jk\",\"LATITUDE\":33.7782,\"LONGITUDE\":76.5762},{\"STATE\\/UT\":\"Jharkhand\",\"CODE\":\"jh\",\"LATITUDE\":23.6102,\"LONGITUDE\":85.2799},{\"STATE\\/UT\":\"Karnataka\",\"CODE\":\"ka\",\"LATITUDE\":15.3173,\"LONGITUDE\":75.7139},{\"STATE\\/UT\":\"Kerala\",\"CODE\":\"kl\",\"LATITUDE\":10.8505,\"LONGITUDE\":76.2711},{\"STATE\\/UT\":\"Ladakh\",\"CODE\":\"la\",\"LATITUDE\":34.1526,\"LONGITUDE\":77.577},{\"STATE\\/UT\":\"Lakshadweep\",\"CODE\":\"ld\",\"LATITUDE\":10.5667,\"LONGITUDE\":72.6417},{\"STATE\\/UT\":\"Madhya Pradesh\",\"CODE\":\"mp\",\"LATITUDE\":22.9734,\"LONGITUDE\":78.6569},{\"STATE\\/UT\":\"Maharashtra\",\"CODE\":\"mh\",\"LATITUDE\":19.7515,\"LONGITUDE\":75.7139},{\"STATE\\/UT\":\"Manipur\",\"CODE\":\"mn\",\"LATITUDE\":24.6637,\"LONGITUDE\":93.9063},{\"STATE\\/UT\":\"Meghalaya\",\"CODE\":\"ml\",\"LATITUDE\":25.467,\"LONGITUDE\":91.3662},{\"STATE\\/UT\":\"Mizoram\",\"CODE\":\"mz\",\"LATITUDE\":23.1645,\"LONGITUDE\":92.9376},{\"STATE\\/UT\":\"Nagaland\",\"CODE\":\"nl\",\"LATITUDE\":26.1584,\"LONGITUDE\":94.5624},{\"STATE\\/UT\":\"Odisha\",\"CODE\":\"or\",\"LATITUDE\":20.9517,\"LONGITUDE\":85.0985},{\"STATE\\/UT\":\"Puducherry\",\"CODE\":\"py\",\"LATITUDE\":11.9416,\"LONGITUDE\":79.8083},{\"STATE\\/UT\":\"Punjab\",\"CODE\":\"pb\",\"LATITUDE\":31.1471,\"LONGITUDE\":75.3412},{\"STATE\\/UT\":\"Rajasthan\",\"CODE\":\"rj\",\"LATITUDE\":27.0238,\"LONGITUDE\":74.2179},{\"STATE\\/UT\":\"Sikkim\",\"CODE\":\"sk\",\"LATITUDE\":27.533,\"LONGITUDE\":88.5122},{\"STATE\\/UT\":\"Tamil Nadu\",\"CODE\":\"tn\",\"LATITUDE\":18.1124,\"LONGITUDE\":79.0193},{\"STATE\\/UT\":\"Telangana\",\"CODE\":\"tg\",\"LATITUDE\":11.1271,\"LONGITUDE\":78.6569},{\"STATE\\/UT\":\"Tripura\",\"CODE\":\"tr\",\"LATITUDE\":23.9408,\"LONGITUDE\":91.9882},{\"STATE\\/UT\":\"Uttar Pradesh\",\"CODE\":\"up\",\"LATITUDE\":26.8467,\"LONGITUDE\":80.9462},{\"STATE\\/UT\":\"Uttarakhand\",\"CODE\":\"ut\",\"LATITUDE\":30.0668,\"LONGITUDE\":79.0193},{\"STATE\\/UT\":\"West Bengal\",\"CODE\":\"wb\",\"LATITUDE\":22.9868,\"LONGITUDE\":87.855}]";
                                latLng =new JSONArray(latlng);
                                mapview = view.findViewById(R.id.mapwebview);
                                mapview.getSettings().setJavaScriptEnabled(true);
                                mapview.setWebChromeClient(new WebChromeClient());
                                mapview.addJavascriptInterface(new webappinterface(mapactivity,stateData,latLng),"Android");
                                mapview.loadUrl("file:///android_asset/map/mapbox.html");


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}