package com.example.covidindia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Animation topAnim,textAnim,startbtnAnim;
    ImageView coronaimg;
    TextView welcmetext;
    Button start_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        textAnim=AnimationUtils.loadAnimation(this,R.anim.text_animation);
        startbtnAnim=AnimationUtils.loadAnimation(this,R.anim.start_anim_btn);

        coronaimg=findViewById(R.id.corona_img);
        welcmetext=findViewById(R.id.welcome);
        start_btn=findViewById(R.id.start_btn);
        coronaimg.setAnimation(topAnim);
        welcmetext.setAnimation(textAnim);
        start_btn.setAnimation(startbtnAnim);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}