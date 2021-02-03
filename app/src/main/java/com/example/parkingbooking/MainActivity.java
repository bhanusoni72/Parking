package com.example.parkingbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Runnable r=new Runnable() {
            @Override
            public void run() {
                long time=System.currentTimeMillis()+2000;
                while(System.currentTimeMillis()<time){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent=new Intent(MainActivity.this,Registration.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        };
        Thread th=new Thread(r);
        th.start();
    }
}