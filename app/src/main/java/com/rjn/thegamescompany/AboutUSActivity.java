package com.rjn.thegamescompany;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AboutUSActivity extends AppCompatActivity {

    WebView webView;
    TextView textView_appname, textView_version;
    ImageView imageView_logo;
    ProgressDialog pbar;
    private FirebaseAnalytics firebaseAnalytics;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        FindViewById();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setVariables();
    }

    private void FindViewById() {
        imgBack = findViewById(R.id.imgBack);

        webView = findViewById(R.id.webView);
        textView_appname = findViewById(R.id.textView_about_appname);
        textView_version = findViewById(R.id.textView_about_appversion);
        imageView_logo = findViewById(R.id.imageView_about_logo);

    }

    public void setVariables() {

        textView_appname.setText(getResources().getString(R.string.app_name));

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            textView_version.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        imageView_logo.setImageResource(R.mipmap.ic_launcher);

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text = "<html><head>"
                + "<style> body{color: #000 !important;text-align:left}"
                + "</style></head>"
                + "<body>" +
                "<br><b>Disclaimer :</b><br><br>" +
                "The Games Company is a largest collection of Online HTML5 and Flash Games. Our team have manually collected games from different publishers. This app re-directs players to the online game publishers. Hence, we are assuring that we don't have any copyrighted contents and the app is not affiliate or sponsored by any companies. App icon designed by Freepik." +
                "<br><br><b>Publish your games :</b><br><br>" +
                "To publish your games and create large traffics to your games and monetize, simply mail us at:<br>" +
                "manufacturingbiss@gmail.com<br>" +
                "<br>" +
                "<b>Our Team :</b><br>" +
                "<br>" +
                "Hello! We are the team of experts providing you the best concepts of mobile apps with modern designs. Our expert team is best in making everyday utility mobile apps with smarter performance. We are happy that we always had good responses from our users which inspire us to build more and more useful apps.<br>" +
                "<br>" +
                "Write us @ manufacturingbiss@gmail.com<br>" +
                "<br>" +
                "Always happy to assist.<br>" +
                "<br>" +
                "Thank You."
                + "</body></html>";
        webView.loadData(text, mimeType, encoding);
    }
}
