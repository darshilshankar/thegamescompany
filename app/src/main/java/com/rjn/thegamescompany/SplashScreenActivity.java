package com.rjn.thegamescompany;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.rjn.thegamescompany.Adapter.HelpPagerAdapter;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.Global.WebService_Tag;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imgSplashScreen;
    public ViewPager pagerHelp;
    private List<Element> arrHelpList = new ArrayList<>();
    private HelpPagerAdapter helpPagerAdapter;

    private boolean isHelpVisible = true;

    private Global_Class globalClass = new Global_Class();

    private boolean isFirstTime = true;
    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        globalClass.init();

        findViewById();

        isHelpVisible = getSharedPreferences("PREF", Context.MODE_PRIVATE).getBoolean("isHelpVisible", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime = false;
                callAPI_version();

                //moveToPage();
            }
        }, 3000);

        //throw new RuntimeException("THIS PIYUSH TEST");
    }

    private void findViewById() {
        imgSplashScreen = findViewById(R.id.imgSplashScreen);
        pagerHelp = findViewById(R.id.pagerHelp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstTime == false)
            callAPI_version();
    }

    private void setHelpData() {
        Element element = null;
        element = new Element();
        element.setImage(String.valueOf(R.drawable.help_one));
        element.setTitle(getResources().getString(R.string.Welcome));
        element.setDescription(getResources().getString(R.string.Des_1));
        element.setBGColor(String.valueOf(getResources().getColor(R.color.C_F70E63)));
        arrHelpList.add(element);

        element = new Element();
        element.setImage(String.valueOf(R.drawable.help_two));
        element.setTitle(getResources().getString(R.string.Challenge));
        element.setDescription(getResources().getString(R.string.Des_2));
        element.setBGColor(String.valueOf(getResources().getColor(R.color.C_FDD012)));
        arrHelpList.add(element);

        element = new Element();
        element.setImage(String.valueOf(R.drawable.help_three));
        element.setTitle(getResources().getString(R.string.Caution));
        element.setDescription(getResources().getString(R.string.Des_3));
        element.setBGColor(String.valueOf(getResources().getColor(R.color.C_6ECAD9)));
        arrHelpList.add(element);

        helpPagerAdapter = new HelpPagerAdapter(SplashScreenActivity.this, arrHelpList, new HelpPagerAdapter.OnHelpImageClickInterface() {
            @Override
            public void onHelpImageClick(int pos) {
                if (pagerHelp.getCurrentItem() == arrHelpList.size() - 1) {
                    getSharedPreferences("PREF", Context.MODE_PRIVATE).edit().putBoolean("isHelpVisible", false).commit();
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                } else {
                    pagerHelp.setCurrentItem(pagerHelp.getCurrentItem() + 1, true);
                }
            }
        });
        pagerHelp.setAdapter(helpPagerAdapter);
    }

    private void callAPI_version() {

        if (globalClass.utility.checkInternetConnection(SplashScreenActivity.this)) {
            String strURL = WebService_Tag.M_API_version + "?" + "version=" + globalClass.utility.getAppVersion(SplashScreenActivity.this) + "&sourcer=ANDROID";

            RequestQueue queue = Volley.newRequestQueue(this);
            Log.i("URL Counter : ", "" + strURL);
            StringRequest request = new StringRequest(Request.Method.GET, strURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //pvProgressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String strVal = jsonObject.has("status") ? jsonObject.getString("status") : "";

                        if (strVal.toUpperCase().equals("FALSE")) {
                            new AlertDialog.Builder(SplashScreenActivity.this).setMessage("A New Version of The Games Company is available. Update the app now for more exciting suprises!")
                                    .setCancelable(false)
                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                        }
                                    }).show();

                        } else {
                            moveToPage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(SplashScreenActivity.this, "", Toast.LENGTH_SHORT).show();
                        moveToPage();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  pvProgressDialog.dismiss();
                    //Toast.makeText(SplashScreenActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    moveToPage();
                }
            });
            queue.add(request);
        }
    }

    public void moveToPage() {
        if (isHelpVisible) {
            imgSplashScreen.setVisibility(View.GONE);
            pagerHelp.setVisibility(View.VISIBLE);
            setHelpData();
        } else {
            String strNAVIGATIONTYPE = getIntent().getStringExtra("NAVIGATIONTYPE");
            Element arrItem = new Gson().fromJson(getIntent().getStringExtra("ARRITEM"), Element.class);

            if (strNAVIGATIONTYPE != null && !strNAVIGATIONTYPE.isEmpty() && arrItem != null) {
                startActivity(new Intent(SplashScreenActivity.this, PlayGameActivity.class)
                        .putExtra("ARRITEM", arrItem));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }

            finish();
        }
    }

}
