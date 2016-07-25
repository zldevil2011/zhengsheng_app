package com.newly_dawn.app.zhengsheng;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ShimmerTextView versionNumber= (ShimmerTextView)findViewById(R.id.versionNumber);
        ShimmerTextView zhengshenge= (ShimmerTextView) findViewById(R.id.welcome);

        Shimmer shimmer = new Shimmer();shimmer.start(versionNumber);
        Shimmer zhengshenge_shimmer = new Shimmer();zhengshenge_shimmer.start(zhengshenge);
    }
}
