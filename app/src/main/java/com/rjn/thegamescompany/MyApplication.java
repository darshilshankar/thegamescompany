package com.rjn.thegamescompany;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //Fabric.with(this, new Crashlytics());
        //Crashlytics.log("Start logging!");

        OneSignal.startInit(this)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
