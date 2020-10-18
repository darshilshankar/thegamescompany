package com.rjn.thegamescompany;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rjn.thegamescompany.Global.Enum_Games;
import com.rjn.thegamescompany.Global.GoToFragment;
import com.rjn.thegamescompany.Global.Utility;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout loutFavourites, loutPublishers;

    private Enum_Games.NavigationType navigationType;

    public static TextView txtTotalGames;

    private ImageView imgFeedBack, imgRateUs, imgAboutUs, imgMoreApps;
    private TextView txtPrivacy;

    public static InterstitialAd mInterstitialAd;
    public static int counterDisplayAd = 1;
    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        try {
            navigationType = (Enum_Games.NavigationType) getIntent().getSerializableExtra("NAVIGATIONTYPE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mInterstitialAd = new InterstitialAd(NavigationActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7202906887840059/6064226269");
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                // Code to be executed when the interstitial ad is closed.
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        FindViewById();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new GoToFragment().gotoFragment(NavigationActivity.this, navigationType);

        loutFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                startActivity(new Intent(NavigationActivity.this, CategoryActivity.class)
                        .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.FAVORITEGAMES)
                        .putExtra("TITLE", "Favourites")
                        .putExtra("ID", ""));
            }
        });

        loutPublishers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                startActivity(new Intent(NavigationActivity.this, CategoryActivity.class)
                        .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.PUBLISHERALL)
                        .putExtra("TITLE", getResources().getString(R.string.dashboard_Publisher))
                        .putExtra("ID", ""));
            }
        });

        imgFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.goToGmailFeedBack(NavigationActivity.this);
            }
        });
        imgRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.rateUSApp(NavigationActivity.this);
            }
        });
        imgAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationActivity.this, AboutUSActivity.class));
            }
        });
        imgMoreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.moreApps(NavigationActivity.this);
            }
        });

        txtPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1Y0H7BQ3guXaVYNHUNLTglCktIg-TYoTdksUVObhQx5E/edit?usp=sharing"));
                startActivity(intent);
            }
        });
    }

    private void FindViewById() {
        loutFavourites = findViewById(R.id.loutFavourites);
        loutPublishers = findViewById(R.id.loutPublishers);

        txtTotalGames = findViewById(R.id.txtTotalGames);

        imgFeedBack = findViewById(R.id.imgFeedBack);
        imgRateUs = findViewById(R.id.imgRateUs);
        imgAboutUs = findViewById(R.id.imgAboutUs);
        imgMoreApps = findViewById(R.id.imgMoreApps);
        txtPrivacy = findViewById(R.id.txtPrivacy);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_AllGames) {
            startActivity(new Intent(NavigationActivity.this, CategoryActivity.class)
                    .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.SEARCHGAMEFILTER)
                    .putExtra("TITLE", "Search")
                    .putExtra("ID", ""));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
