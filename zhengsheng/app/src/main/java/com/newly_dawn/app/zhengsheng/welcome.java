package com.newly_dawn.app.zhengsheng;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


public class welcome extends Activity {
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ShimmerTextView versionNumber= (ShimmerTextView)findViewById(R.id.versionNumber);
        ShimmerTextView zhengshenge= (ShimmerTextView) findViewById(R.id.welcome);
        Shimmer shimmer = new Shimmer();shimmer.start(versionNumber);
        Shimmer zhengshenge_shimmer = new Shimmer();zhengshenge_shimmer.start(zhengshenge);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(welcome.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}
