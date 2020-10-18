package com.rjn.thegamescompany;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rjn.thegamescompany.Global.Enum_Games;

public class MainActivity extends AppCompatActivity {

    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        finish();

        try {
            String strTEST = getIntent().getStringExtra("TEST");
            if (strTEST != null && !strTEST.isEmpty())
                Toast.makeText(this, strTEST, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(new Intent(MainActivity.this, NavigationActivity.class).putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.DASHBOARD));

    }
}
