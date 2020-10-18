package com.rjn.thegamescompany;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

public class LoadGameActivity extends AppCompatActivity {

    private WebView webView;
    private CardView progressLayout;
    private AdView adView;
    private FirebaseAnalytics firebaseAnalytics;
    private TextView gameName;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        hideStatusBar();
        webView = findViewById(R.id.webView);
        progressLayout = findViewById(R.id.progressCard);
        adView = findViewById(R.id.adView);
        gameName = findViewById(R.id.gameName);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        webView.setWebViewClient(new webViewClient());
        WebSettings webSettings = webView.getSettings();
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        // Enable plugins
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // Increase the priority of the rendering thread to high
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // Enable application caching
        webSettings.setAppCacheEnabled(false);
        // Enable HTML5 local storage and make it persistent
        webSettings.setDomStorageEnabled(true);
        //webSettings.setDatabaseEnabled(true);
        //webSettings.setDatabasePath("/data/data/" + PlayGameActivity.this.getPackageName() + "/databases/");

        // Clear spurious cache data
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        Intent intent = getIntent();

        if (savedInstanceState == null)
        {
            webView.loadUrl( intent.getStringExtra("URL"));
            webView.requestFocus();
            Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bottom_up);

            progressLayout.startAnimation(bottomUp);

            new CountDownTimer(8000, 1000) {
                public void onTick(long millisUntilFinished) {
                    gameName.setText("Starting your game in " + millisUntilFinished / 1000+ " seconds..." );
                }

                public void onFinish() {
                    Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_down);
                    progressLayout.startAnimation(bottomDown);
                    progressLayout.setVisibility(View.GONE);
                }
            }.start();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    private class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("URL LOADING", "" + url);
            view.loadUrl(url);
            if (url != null && url.startsWith("market://")) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else if (url.startsWith("http") || url.startsWith("https")) {
                return true;
            } else if (Uri.parse(url).getHost().length() == 0 || Uri.parse(url).getHost().endsWith("html5rocks.com")) {
                return false;
            } else {
                webView.stopLoading();
                webView.goBack();
                Toast.makeText(LoadGameActivity.this, "Unknown Link, unable to handle", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {

            if (webView.canGoBack() && (!webView.getUrl().contains("famobi")
                    && !webView.getUrl().contains("html5games")
                    && !webView.getUrl().contains("gamepix"))) {
                webView.goBack();
            } else {
                new AlertDialog.Builder(LoadGameActivity.this).setMessage("Are you sure, you want to exit the game?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (NavigationActivity.counterDisplayAd == 5) {
                                    NavigationActivity.counterDisplayAd = 1;
                                    if (NavigationActivity.mInterstitialAd != null && NavigationActivity.mInterstitialAd.isLoaded()) {
                                        NavigationActivity.mInterstitialAd.show();
                                    } else {
                                        NavigationActivity.mInterstitialAd = new InterstitialAd(LoadGameActivity.this);
                                        NavigationActivity.mInterstitialAd.setAdUnitId("ca-app-pub-7202906887840059/6064226269");
                                        //NavigationActivity.mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                                        NavigationActivity.mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                    }
                                } else {
                                    NavigationActivity.counterDisplayAd++;
                                }

                                        finish();
                                        webView.destroy();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

        }

    private void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
