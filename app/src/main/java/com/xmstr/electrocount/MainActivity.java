package com.xmstr.electrocount;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.xmstr.electrocount.db.CountsDataSource;
import com.xmstr.electrocount.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new MainFragment()).commit();
        }
    }
}