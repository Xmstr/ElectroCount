package com.xmstr.electrocount;

import android.app.AlarmManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.xmstr.electrocount.fragments.MainFragment;



public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;

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