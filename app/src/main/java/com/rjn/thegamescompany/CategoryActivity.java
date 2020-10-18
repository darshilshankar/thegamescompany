package com.rjn.thegamescompany;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rjn.thegamescompany.Custom.PVProgressDialog;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Enum_Games;
import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.Global.Model_Fav_Games;
import com.rjn.thegamescompany.Global.RealmController;
import com.rjn.thegamescompany.Global.WebService_Tag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class CategoryActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView txtCategory;
    private RelativeLayout loutSearch;
    private ImageView imgSearch;
    private EditText edtSearch;

    private SwipeRefreshLayout loutSwipeRefresh;
    private RecyclerView listCategory;
    private List<Element> arrDataList = new ArrayList<>();
    private List<Model_Fav_Games> arrFavoriteDataList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    private TextView txtDataNotAvailable;

    private FavGameAdapter favGameAdapter;

    private Global_Class globalClass = new Global_Class();

    private Enum_Games.NavigationType navigationType;
    private String strTITLE = "";
    private String strID = "";

    boolean isRoundedDataRaw = false;

    private Realm realm;
    private RealmController realmController;
    private FirebaseAnalytics firebaseAnalytics;

    private void ActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_category_activty);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        try {
            navigationType = (Enum_Games.NavigationType) getIntent().getSerializableExtra("NAVIGATIONTYPE");
            strTITLE = getIntent().getStringExtra("TITLE");
            strID = getIntent().getStringExtra("ID");
        } catch (Exception e) {
            e.printStackTrace();
        }

        realmController = RealmController.with(CategoryActivity.this);
        realm = realmController.getRealm();

        globalClass.init();

        //ActionBar();

        findViewById();

        txtCategory.setText(strTITLE);

        loutSearch.setVisibility(navigationType != null && navigationType == Enum_Games.NavigationType.SEARCHGAMEFILTER ? View.VISIBLE : View.GONE);
        imgSearch.setVisibility(navigationType != null && navigationType == Enum_Games.NavigationType.SEARCHGAMEFILTER ? View.GONE : View.VISIBLE);

        if (navigationType != null && navigationType != Enum_Games.NavigationType.FAVORITEGAMES) {
            resetData(true);
        } else {
            resetFavApplication();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NavigationActivity.counterDisplayAd == 3) {
                    NavigationActivity.counterDisplayAd = 1;
                    if (NavigationActivity.mInterstitialAd != null && NavigationActivity.mInterstitialAd.isLoaded()) {
                        NavigationActivity.mInterstitialAd.show();
                    } else {
                        NavigationActivity.mInterstitialAd = new InterstitialAd(CategoryActivity.this);
                        NavigationActivity.mInterstitialAd.setAdUnitId("ca-app-pub-7202906887840059/6064226269");
                        //NavigationActivity.mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                        NavigationActivity.mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }else{
                    NavigationActivity.counterDisplayAd++;
                }

                startActivity(new Intent(CategoryActivity.this, CategoryActivity.class)
                        .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.SEARCHGAMEFILTER)
                        .putExtra("TITLE", "Search")
                        .putExtra("ID", ""));
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (categoryAdapter != null)
                    categoryAdapter.onFilterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (navigationType != null && navigationType != Enum_Games.NavigationType.FAVORITEGAMES) {
                    resetData(false);
                } else {
                    resetFavApplication();
                }
            }
        });
    }

    private void findViewById() {

        imgBack = findViewById(R.id.imgBack);
        imgSearch = findViewById(R.id.imgSearch);
        edtSearch = findViewById(R.id.edtSearch);

        txtCategory = findViewById(R.id.txtCategory);

        loutSearch = findViewById(R.id.loutSearch);

        loutSwipeRefresh = findViewById(R.id.loutSwipeRefresh);
        txtDataNotAvailable = findViewById(R.id.txtDataNotAvailable);
        listCategory = findViewById(R.id.listCategory);
        listCategory.setLayoutManager(new GridLayoutManager(CategoryActivity.this, 3, LinearLayoutManager.VERTICAL, false));
    }

    private void resetData(boolean isProgressVisible) {
        arrDataList.clear();
        if (categoryAdapter != null)
            categoryAdapter.notifyDataSetChanged();

        callCategoryList();
    }

    private void callCategoryList() {

        if (globalClass.utility.checkInternetConnection(CategoryActivity.this)) {

            final PVProgressDialog pvProgressDialog = new PVProgressDialog(CategoryActivity.this);
            //pvProgressDialog.show();

            String strURL = "";

            switch (navigationType) {
                case CATEGORYFILTER:
                    strURL = WebService_Tag.M_API_all_app + "?" + "cate_id=" + strID;
                    break;
                case PUBLISHERFILTER:
                    strURL = WebService_Tag.M_API_all_app + "?" + "pub_id=" + strID;
                    break;
                case CATEGORYALL:
                    strURL = WebService_Tag.M_API_all_cate_pub + "?" + "tbl_name=" + "Category";
                    isRoundedDataRaw = true;
                    break;
                case PUBLISHERALL:
                    strURL = WebService_Tag.M_API_all_cate_pub + "?" + "tbl_name=" + "Publisher";
                    break;
                case CATEGORYGAMES:
                    strURL = WebService_Tag.M_API_fetch_app_by_cate_pub + "?" + "cate_id=" + strID;
                    break;
                case PUBLISHERGAMES:
                    strURL = WebService_Tag.M_API_fetch_app_by_cate_pub + "?" + "pub_id=" + strID;
                    break;
                case TRENDINGGAMES:
                    strURL = WebService_Tag.M_API_all_app + "?" + "trand=true";
                    break;
                case NEWADDEDGAMES:
                    strURL = WebService_Tag.M_API_all_app + "?" + "added=true";
                    break;
                case ALLGAMES:
                case SEARCHGAMEFILTER:
                    strURL = WebService_Tag.M_API_all_app + "?" + "all=true";
                    break;
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            Log.i("URL Category : ", "" + strURL);
            StringRequest request = new StringRequest(Request.Method.GET, strURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pvProgressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            Element element = new Element();
                            element.setId(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_id));
                            element.setName(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_name));
                            element.setCate_id(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_cate_id));
                            element.setIcon(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_icon));

                            if (navigationType == Enum_Games.NavigationType.CATEGORYALL || navigationType == Enum_Games.NavigationType.PUBLISHERALL) {
                                element.setImage(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_image));
                            } else {
                                element.setImage(element.getIcon());
                            }

                            element.setEmail(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_email));
                            element.setPub_id(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_pub_id));
                            element.setHtml_flash(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_html_flash));
                            element.setUrl(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_url));
                            element.setCounter(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_counter));
                            element.setPosition_order(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_position_order));
                            element.setPname(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_pname));
                            element.setCname(globalClass.getJsonObjectVal(jsonObject, WebService_Tag.R_cname));
                            arrDataList.add(element);
                        }

                        if (arrDataList.size() != 0) {
                            categoryAdapter = new CategoryAdapter(CategoryActivity.this, arrDataList, isRoundedDataRaw);
                            listCategory.setAdapter(categoryAdapter);
                            txtDataNotAvailable.setVisibility(View.GONE);
                            loutSwipeRefresh.setVisibility(View.VISIBLE);
                        } else {
                            //Toast.makeText(CategoryActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                            if (navigationType == Enum_Games.NavigationType.FAVORITEGAMES) {
                                txtDataNotAvailable.setVisibility(View.VISIBLE);
                                loutSwipeRefresh.setVisibility(View.GONE);
                            } else {
                                loutSwipeRefresh.setVisibility(View.VISIBLE);
                                Toast.makeText(CategoryActivity.this, "Data unavailable.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CategoryActivity.this, "Games not available. Please search other category.", Toast.LENGTH_SHORT).show();
                    }

                    loutSwipeRefresh.setRefreshing(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pvProgressDialog.dismiss();
                    Toast.makeText(CategoryActivity.this, getResources().getString(R.string.Msg_Error_Server), Toast.LENGTH_SHORT).show();
                    loutSwipeRefresh.setRefreshing(false);
                }
            });
            queue.add(request);
        } else {
            loutSwipeRefresh.setRefreshing(false);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

        private Activity activity;
        private List<Element> arrList;
        private List<Element> arrFilterList = new ArrayList<>();

        private boolean isRounded = false;

        public CategoryAdapter(Activity activity, List<Element> arrList, boolean isRounded) {
            this.activity = activity;
            this.arrList = arrList;
            this.isRounded = isRounded;

            arrFilterList.addAll(arrList);
        }

        @NonNull
        @Override
        public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_category, null);
            return new CategoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.CategoryHolder holder, int i) {

            final int pos = i;
            final Element arrItem = arrList.get(pos);

            holder.txtGameName.setText(arrItem.getName());

            holder.imgIcon.setBackgroundResource(isRounded ? R.drawable.bg_dedede_round : R.drawable.bg_category_ideal);

            if (!globalClass.isEmpty(arrItem.getImage())) {

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(false ? 100 : 15));
                Glide.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getImage().replaceAll(" ", "%20")).apply(requestOptions).into(holder.imgIcon);

               /* Picasso.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getIcon()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // new PVCornerView(activity, holder.imgIcon, 10);
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                        roundedBitmapDrawable.setCornerRadius(10);
                        roundedBitmapDrawable.setAntiAlias(true);
                        holder.imgIcon.setImageDrawable(roundedBitmapDrawable);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        holder.imgIcon.setImageResource(0);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });*/
            } else {
                holder.imgIcon.setImageResource(0);
            }

            holder.loutCategoryRaw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (arrItem.getName().toUpperCase().equals("ALL GAMES")) {
                        activity.startActivity(new Intent(activity, CategoryActivity.class)
                                .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.ALLGAMES)
                                .putExtra("TITLE", "All Games")
                                .putExtra("ID", ""));
                    } else {
                        Enum_Games.NavigationType navigationTypeTemp = null;
                        switch (navigationType) {
                            case CATEGORYFILTER:
                            case CATEGORYALL:
                                navigationTypeTemp = Enum_Games.NavigationType.CATEGORYGAMES;
                                break;
                            case PUBLISHERFILTER:
                            case PUBLISHERALL:
                                navigationTypeTemp = Enum_Games.NavigationType.PUBLISHERGAMES;
                                break;
                            default:
                                navigationTypeTemp = null;
                                activity.startActivity(new Intent(activity, PlayGameActivity.class).putExtra("ARRITEM", arrItem));
                        }

                        if (navigationTypeTemp != null) {
                            activity.startActivity(new Intent(activity, CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", navigationTypeTemp)
                                    .putExtra("TITLE", arrItem.getName())
                                    .putExtra("ID", arrItem.getId()));
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrList.size();
        }

        public class CategoryHolder extends RecyclerView.ViewHolder {

            public LinearLayout loutCategoryRaw;
            public ImageView imgIcon;
            public TextView txtGameName;

            public CategoryHolder(@NonNull View itemView) {
                super(itemView);
                this.loutCategoryRaw = itemView.findViewById(R.id.loutCategoryRaw);
                this.imgIcon = itemView.findViewById(R.id.imgIcon);
                this.txtGameName = itemView.findViewById(R.id.txtGameName);
            }
        }

        public void onFilterData(String strVal) {
            arrList.clear();
            if (strVal.isEmpty()) {
                arrList.addAll(arrFilterList);
                notifyDataSetChanged();
            } else {
                if (arrFilterList.size() != 0) {
                    for (Element arrTemp : arrFilterList) {
                        if (arrTemp.getName().toUpperCase().contains(strVal.toUpperCase())) {
                            arrList.add(arrTemp);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    private class FavGameAdapter extends RecyclerView.Adapter<FavGameAdapter.FavGameHolder> {

        private Activity activity;
        private List<Model_Fav_Games> arrList;

        public FavGameAdapter(Activity activity, List<Model_Fav_Games> arrList) {
            this.activity = activity;
            this.arrList = arrList;
        }

        @NonNull
        @Override
        public FavGameHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_category, null);
            return new FavGameHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FavGameHolder holder, int i) {

            final int pos = i;
            final Model_Fav_Games arrItem = arrList.get(pos);

            holder.txtGameName.setText(arrItem.getName());

            if (!globalClass.isEmpty(arrItem.getIcon())) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(false ? 100 : 15));
                Glide.with(activity).load(WebService_Tag.MAIN_URL + "assets/app_icon/" + arrItem.getIcon().replaceAll(" ", "%20")).apply(requestOptions).into(holder.imgIcon);
            } else {
                holder.imgIcon.setImageResource(0);
            }

            holder.loutCategoryRaw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Element element = new Element();
                    element.setId(arrItem.getId());
                    element.setName(arrItem.getName());
                    element.setCate_id(arrItem.getCate_id());
                    element.setImage(arrItem.getIcon());
                    element.setHtml_flash(arrItem.getHtml_flash());
                    element.setPname(arrItem.getPname());
                    element.setCname(arrItem.getCname());

                    activity.startActivityForResult(new Intent(activity, PlayGameActivity.class)
                            .putExtra("ARRITEM", element), 1001);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrList.size();
        }

        public class FavGameHolder extends RecyclerView.ViewHolder {

            public LinearLayout loutCategoryRaw;
            public ImageView imgIcon;
            public TextView txtGameName;

            public FavGameHolder(@NonNull View itemView) {
                super(itemView);
                this.loutCategoryRaw = itemView.findViewById(R.id.loutCategoryRaw);
                this.imgIcon = itemView.findViewById(R.id.imgIcon);
                this.txtGameName = itemView.findViewById(R.id.txtGameName);
            }
        }
    }

    private void resetFavApplication() {
        arrFavoriteDataList.clear();
        arrFavoriteDataList.addAll(realmController.getFavoriteGames());

        if (arrFavoriteDataList != null && arrFavoriteDataList.size() != 0) {
            Collections.reverse(arrFavoriteDataList);

            favGameAdapter = new FavGameAdapter(CategoryActivity.this, arrFavoriteDataList);
            listCategory.setAdapter(favGameAdapter);

            txtDataNotAvailable.setVisibility(View.GONE);
            loutSwipeRefresh.setVisibility(View.VISIBLE);
        } else {
            if (favGameAdapter != null) {
                favGameAdapter.notifyDataSetChanged();
            }
            loutSwipeRefresh.setVisibility(View.GONE);
            txtDataNotAvailable.setVisibility(View.VISIBLE);

            //Toast.makeText(CategoryActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (navigationType == Enum_Games.NavigationType.FAVORITEGAMES)
                resetFavApplication();
        }
    }
}
