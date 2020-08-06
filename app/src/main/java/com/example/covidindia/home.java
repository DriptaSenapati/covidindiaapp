package com.example.covidindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Fragment fragment=null;
    FragmentTransaction ft;
    FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_home);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        fragment=new home_activity(home.this);
        fm=getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.simpleframelayout,fragment,"HOME_FRAGMENT");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fm=getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (item.getItemId()){
            case R.id.data:
                fragment=fm.findFragmentByTag("HOME_DATA");
                if(fragment == null){
                    fragment = new data_activity(home.this);
                    drawerLayout.closeDrawers();
                    ft.replace(R.id.simpleframelayout,fragment,"HOME_DATA");
                }else{
                    drawerLayout.closeDrawers();
                }
                break;
            case R.id.home:
                fragment=fm.findFragmentByTag("HOME_FRAGMENT");
                if (fragment == null){
                    fragment=new home_activity(home.this);
                    drawerLayout.closeDrawers();
                    ft.replace(R.id.simpleframelayout,fragment,"HOME_FRAGMENT");
                }else{
                    drawerLayout.closeDrawers();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }



        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        return true;
    }
}