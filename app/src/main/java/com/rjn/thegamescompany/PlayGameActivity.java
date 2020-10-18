package com.rjn.thegamescompany;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.rjn.thegamescompany.Custom.PVCustomHorizontalBar;
import com.rjn.thegamescompany.Custom.PVProgressDialog;
import com.rjn.thegamescompany.Dialog.PVPuffinDialog;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.Global.Model_Fav_Games;
import com.rjn.thegamescompany.Global.RealmController;
import com.rjn.thegamescompany.Global.Utility;
import com.rjn.thegamescompany.Global.WebService_Tag;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlayGameActivity extends AppCompatActivity {

    private ImageView imgBack;
    private ImageView imgPlayGame;
    private TextView txtGameName, txtPublisherName, txtCategoryName;
    private PVCustomHorizontalBar pvCustomHorizontalBar;
    int len = 0;
    //private LikeButton btnLike;
    private ImageView imgFav;
    private TextView txtDescription;

    private SwipeRefreshLayout loutSwipeRefresh;

    private Element arrItem;
    private Element arrDetailItem;
    private Global_Class globalClass;

    //private RoundCornerProgressBar pbRoundCorner;
    private Realm realm;
    RealmController realmController;

    private LinearLayout loutFavourite, loutPlay, loutShare;
    public static String strGameURL = "";
    private AdView adView;
    FirebaseAnalytics firebaseAnalytics;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        realmController = RealmController.with(PlayGameActivity.this);
        realm = realmController.getRealm();

        globalClass = new Global_Class();
        globalClass.init();

        try {
            arrItem = (Element) getIntent().getSerializableExtra("ARRITEM");
        } catch (Exception e) {
            e.printStackTrace();
        }

        FindViewById();

        //chompProgressView.setImageDrawableChomp(R.drawable.bg_round_black_corner);
        Log.d("Test",arrItem.getUrl());
        if (arrItem != null) {
            callApi_Counter();
            callAPI_get_application_details();
        }

        // setData();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

      /*  if (strGameUrl != null && !strGameUrl.isEmpty() && strGameUrl.startsWith("http")) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }, 2000);
        } else {
            Toast.makeText(this, "Game path not valid other game play and enjoy.", Toast.LENGTH_SHORT).show();
        }*/

        loutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callAPI_get_application_details();
            }
        });



        //webView.requestFocus();

        //webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

       /* pbRoundCorner.setOnProgressChangedListener(new BaseRoundCornerProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int viewId, float progress, boolean isPrimaryProgress, boolean isSecondaryProgress) {
                Message message = new Message();

                Bundle bundle = new Bundle();
                float pro = progress;
                float num = pbRoundCorner.getMax();
                float result = (pro / num) * 100;

                bundle.putFloat("per", result);
                message.setData(bundle);
                message.what = 0;
                handler.sendMessage(message);
            }
        });*/

       /* pvCustomHorizontalBar.setMax(100);
        pvCustomHorizontalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

        RealmResults<Model_Fav_Games> resultLike = realm.where(Model_Fav_Games.class)
                .equalTo("id", arrItem.getId())
                .or()
                .equalTo("name", arrItem.getName())
                .findAll();
        imgFav.setImageResource((resultLike != null && resultLike.size() != 0) ? R.drawable.ic_selected_fav : R.drawable.ic_unselected_fav_black);
        loutFavourite.setTag((resultLike != null && resultLike.size() != 0) ? true : false);
        //btnLike.setLiked((resultLike != null && resultLike.size() != 0) ? true : false);
        loutFavourite.setEnabled(false);
        /*imgFav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                    addShortcut();
                    realmController.addFavGame(arrDetailItem);
                }
                //RealmResults<Model_Fav_Games> results = realm.where(Model_Fav_Games.class).findAll();
                //Log.i("SIZE : ", "" + results.size());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                    if (realmController.removeFavGame(arrDetailItem)) {
                        setResult(Activity.RESULT_OK, new Intent());
                    } else {
                        Toast.makeText(PlayGameActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        btnLike.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
            }
        });*/

        loutFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = (boolean) loutFavourite.getTag();
                if (isSelected == false) {
                    if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                        loutFavourite.setTag(true);
                        addShortcut();
                        realmController.addFavGame(arrDetailItem);
                        imgFav.setImageResource(R.drawable.ic_selected_fav);
                    }
                } else {
                    if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                        if (realmController.removeFavGame(arrDetailItem)) {
                            imgFav.setImageResource(R.drawable.ic_unselected_fav_black);
                            loutFavourite.setTag(false);
                            setResult(Activity.RESULT_OK, new Intent());
                        } else {
                            Toast.makeText(PlayGameActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        loutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                    loutSwipeRefresh.setEnabled(false);
                    realmController.addRecentGameGame(arrDetailItem);
                    if (arrDetailItem.getHtml_flash().toUpperCase().equals("HTML")) {
                        Intent intent = new Intent(PlayGameActivity.this,LoadGameActivity.class);
                        intent.putExtra("URL",arrDetailItem.getUrl());
                        startActivity(intent);
                    } else {
                        Intent intent;
                        boolean isAppInstalledFree = Utility.appInstalledOrNot(PlayGameActivity.this, "com.cloudmosa.puffinFree");
                        boolean isAppInstalledPro = Utility.appInstalledOrNot(PlayGameActivity.this, "com.cloudmosa.puffin");
                        if (isAppInstalledPro) {
                            intent = getPackageManager().getLaunchIntentForPackage("com.cloudmosa.puffin");
                            //String url = "https://games.inbox.lv/mini/game/strip-or-die/?language=en";
                            intent.setData(Uri.parse(arrDetailItem.getUrl()));
                            startActivity(intent);
                        } else if (isAppInstalledFree) {
                            intent = getPackageManager().getLaunchIntentForPackage("com.cloudmosa.puffinFree");
                            //String url = "https://games.inbox.lv/mini/game/strip-or-die/?language=en";
                            intent.setData(Uri.parse(arrDetailItem.getUrl()));
                            startActivity(intent);
                        } else {
                            new PVPuffinDialog(PlayGameActivity.this, arrDetailItem.getUrl());
                        }

                    }
                }
            }
        });
        loutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.utility.checkInternetConnection(PlayGameActivity.this) && arrDetailItem != null) {
                    String shareBody = getResources().getString(R.string.app_name) + "\n\n"
                            + "Hey! I loved playing \'\'" + arrDetailItem.getName() + "\'\' game on The Games Company." + "\n\n"
                            + "To enjoy more thousands of exciting games, click now : https://play.google.com/store/apps/details?id=com.rjn.thegamescompany\n\n"
                            + "Thanks.";
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share"));
                }
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }


    private void setData() {
        if (arrDetailItem != null) {
            strGameURL = arrDetailItem.getUrl();

            txtGameName.setText(arrDetailItem.getName());
            txtPublisherName.setText(arrDetailItem.getPname());
            txtCategoryName.setText(arrDetailItem.getCname());
            if (!globalClass.isEmpty(arrDetailItem.getImage())) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(false ? 100 : 15));
                Glide.with(PlayGameActivity.this).load(WebService_Tag.MAIN_URL + "assets/app_icon/" + arrDetailItem.getImage().replaceAll(" ", "%20")).apply(requestOptions).into(imgPlayGame);
            } else {
                imgPlayGame.setImageResource(0);
            }

            txtDescription.setText(Html.fromHtml("\" <b>" + arrDetailItem.getName() + "</b> is an awesome " + arrDetailItem.getCname() + " game by " + arrDetailItem.getPname() + ". Play now and beat the highscore ! \""));
        }
    }

    private void FindViewById() {
        imgBack = findViewById(R.id.imgBack);


        loutSwipeRefresh = findViewById(R.id.loutSwipeRefresh);

        //pbRoundCorner = findViewById(R.id.pbRoundCorner);

        imgPlayGame = findViewById(R.id.imgPlayGame);
        txtGameName = findViewById(R.id.txtGameName);
        txtPublisherName = findViewById(R.id.txtPublisherName);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryName.setSelected(true);
        pvCustomHorizontalBar = findViewById(R.id.pvCustomHorizontalBar);
        txtDescription = findViewById(R.id.txtDescription);

        imgFav = findViewById(R.id.imgFav);
        //btnLike = findViewById(R.id.btnLike);


        loutFavourite = findViewById(R.id.loutFavourite);
        loutPlay = findViewById(R.id.loutPlay);
        loutShare = findViewById(R.id.loutShare);
        adView = findViewById(R.id.adView);
    }

    private void callApi_Counter() {

        if (globalClass.utility.checkInternetConnection(PlayGameActivity.this)) {

            //final PVProgressDialog pvProgressDialog = new PVProgressDialog(PlayGameActivity.this);
            //pvProgressDialog.show();

            int gameCounter = 1;

            if ((!globalClass.isEmpty(arrItem.getCounter()))) {
                int counter = Integer.parseInt(arrItem.getCounter());
                Log.i("Game Counter : ", "" + counter);
                gameCounter = counter++;
                Log.i("Game Counter Increase: ", "" + counter);
            }

            String strURL = WebService_Tag.M_API_counter + "?" + "app_counter=" + gameCounter + "&app_id=" + arrItem.getId();

            RequestQueue queue = Volley.newRequestQueue(this);
            Log.i("URL Counter : ", "" + strURL);
            StringRequest request = new StringRequest(Request.Method.GET, strURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //pvProgressDialog.dismiss();
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PlayGameActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  pvProgressDialog.dismiss();
                    Toast.makeText(PlayGameActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }

    }

    private void callAPI_get_application_details() {

        if (globalClass.utility.checkInternetConnection(PlayGameActivity.this)) {

            final PVProgressDialog pvProgressDialog = new PVProgressDialog(PlayGameActivity.this);
            pvProgressDialog.show();

            String strURL = WebService_Tag.M_API_get_application_details + "?app_id=" + arrItem.getId();

            RequestQueue queue = Volley.newRequestQueue(this);
            Log.i("URL Application Detail:", "" + strURL);
            StringRequest request = new StringRequest(Request.Method.GET, strURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pvProgressDialog.dismiss();
                    try {
                        loutFavourite.setEnabled(true);
                        JSONObject jsonObject = new JSONObject(response);

                        Element element = new Element();
                        element.setId(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_id));
                        element.setName(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_name));
                        element.setCate_id(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_cate_id));
                        element.setIcon(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_icon));

                        element.setImage(element.getIcon());

                        element.setEmail(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_email));
                        element.setPub_id(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_pub_id));
                        element.setHtml_flash(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_html_flash));
                        element.setUrl(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_url));
                        element.setCounter(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_counter));
                        element.setPosition_order(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_position_order));
                        //element.setPname(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_pname));
                        //element.setCname(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_cname));

                        element.setPname(element.getPub_id());
                        element.setCname(element.getCate_id());

                        arrDetailItem = element;

                        setData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        onBackPressed();
                        Toast.makeText(PlayGameActivity.this, getResources().getString(R.string.Msg_Na_Game), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pvProgressDialog.dismiss();
                    Toast.makeText(PlayGameActivity.this, "" + getResources().getString(R.string.Msg_Error_Server), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }
        loutSwipeRefresh.setRefreshing(false);
    }

/*
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
//                    seekbar_percent.setText("snailbar percent :       "
//                            + msg.getData().getFloat("per") + "%");
                    break;
                case 1:
                    if (pbRoundCorner.getProgress() < 100) {
                        if (pbRoundCorner.getProgress() < 20) {
                            len += 2;
                            handler.sendEmptyMessageDelayed(1, 500);
                        } else if (pbRoundCorner.getProgress() > 21 && pbRoundCorner.getProgress() < 26) {
                            len += 1;
                            handler.sendEmptyMessageDelayed(1, 1000);
                        } else {
                            len += 2;
                            handler.sendEmptyMessageDelayed(1, 50);
                        }
                        pbRoundCorner.setProgress(len);
                    } else if (pbRoundCorner.getProgress() >= 100) {
                        loutPlayGameMain.setVisibility(View.GONE);
                        loutGame.setVisibility(View.VISIBLE);

                        hideStatusBar();

                        loadGameURL();
                    }
                    break;
            }
        }
    };
*/

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addShortcut() {
        String shortLabel = arrItem.getId();
        String shortcutId = shortLabel.replaceAll("\\s+", "").toLowerCase() + "_shortcut";

        Gson gson = new Gson();
        String jsonString = gson.toJson(arrItem);
        Bitmap bitmap = ((BitmapDrawable) imgPlayGame.getDrawable()).getBitmap();

        Intent intent = new Intent(PlayGameActivity.this, SplashScreenActivity.class)
                .putExtra("NAVIGATIONTYPE", "PLAYGAME")
                .putExtra("ARRITEM", jsonString)
                .setAction(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            List<ShortcutInfo> mShortcutInfos = new ArrayList<ShortcutInfo>();

            ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo.Builder shortcutBuilder = new ShortcutInfo.Builder(PlayGameActivity.this, shortcutId)
                    .setShortLabel(arrDetailItem.getName())
                    .setLongLabel(arrDetailItem.getName())
                    .setIcon(Icon.createWithBitmap(bitmap))
                    .setIntent(intent);

            ShortcutInfo shortcut = shortcutBuilder.build();
            mShortcutInfos.add(shortcut);
            mShortcutManager.setDynamicShortcuts(mShortcutInfos);

            getApplicationContext().sendBroadcast(intent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mShortcutManager.isRequestPinShortcutSupported()) {
                    ShortcutInfo pinShortcutInfo = new ShortcutInfo
                            .Builder(PlayGameActivity.this, shortcutId)
                            .build();
                    Intent pinnedShortcutCallbackIntent =
                            mShortcutManager.createShortcutResultIntent(pinShortcutInfo);

                    //Get notified when a shortcut is pinned successfully//

                    PendingIntent successCallback = PendingIntent.getBroadcast(PlayGameActivity.this, 0,
                            pinnedShortcutCallbackIntent, 0);
                    mShortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
                }
            }
        } else {
            /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/

            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, arrItem.getName());
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

            //addIntent.putExtra("duplicate", true);
            getApplicationContext().sendBroadcast(addIntent);
        }
    }


}
