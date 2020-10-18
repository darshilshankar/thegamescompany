package com.rjn.thegamescompany;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rjn.thegamescompany.Adapter.DashboardDataAdapter;
import com.rjn.thegamescompany.Adapter.InfinitePagerAdapter;
import com.rjn.thegamescompany.Adapter.SwipeCardAdapter;
import com.rjn.thegamescompany.Custom.PVProgressDialog;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Enum_Games.NavigationType;
import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.Global.Model_RecentPlay_Games;
import com.rjn.thegamescompany.Global.RealmController;
import com.rjn.thegamescompany.Global.WebService_Tag;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import in.arjsna.swipecardlib.SwipeCardView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.rjn.thegamescompany.Global.Enum_Games.getStringToNavigationType;

public class HomeFragment extends Fragment {

    private View rootView;
    private ScrollView scrollMain;
    private SwipeRefreshLayout loutSwipeRefresh;

    private SwipeCardView swipeCard;
    private LinearLayout loutDashboard;

    //private KKViewPager viewPager;
    private UltraViewPager viewPager;
    InfinitePagerAdapter infinitePagerAdapter;

    private List<Element> arrSwipeCardList = new ArrayList<>();
    private SwipeCardAdapter swipeCardAdapter;

    Map<String, List<Element>> mapDashboardData = new LinkedHashMap<>();
    private List<Element> arrCategoryList = new ArrayList<>();
    private List<Element> arrPublisherList = new ArrayList<>();
    private List<Element> arrNewAddedList = new ArrayList<>();
    private List<Element> arrTrendingList = new ArrayList<>();

    Global_Class globalClass = new Global_Class();

    NavigationType dashboardNavigationType = null;

    private Realm realm;
    RealmController realmController;

    boolean isRoundedData = false;

    PVProgressDialog pvProgressDialog;
    private FirebaseAnalytics firebaseAnalytics;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragmenthome, container, false);
        realmController = RealmController.with(getActivity());
        realm = realmController.getRealm();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        pvProgressDialog = new PVProgressDialog(getActivity());

        findViewById();

        globalClass.init();

        resetData(true);

        loutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData(false);
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loutSwipeRefresh.setEnabled(false);
                return false;
            }
        });

        scrollMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loutSwipeRefresh.setEnabled(true);
                scrollMain.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        return rootView;
    }

    private void findViewById() {
        scrollMain = rootView.findViewById(R.id.scrollMain);
        loutSwipeRefresh = rootView.findViewById(R.id.loutSwipeRefresh);

        viewPager = rootView.findViewById(R.id.viewPager);

        swipeCard = rootView.findViewById(R.id.swipeCard);
        loutDashboard = rootView.findViewById(R.id.loutDashboard);

    }

    @Override
    public void onResume() {
        super.onResume();

        scrollMain.smoothScrollTo(0, 2);

        if (Global_Class.jsonHomeObject != null) {
            pvProgressDialog.show();
            clearData();
            setHomeData(Global_Class.jsonHomeObject);
        }
    }


    private void clearData() {
        mapDashboardData.clear();
        arrSwipeCardList.clear();
        arrCategoryList.clear();
        arrPublisherList.clear();
        arrNewAddedList.clear();
        arrTrendingList.clear();

        viewPager.refresh();

        if (infinitePagerAdapter != null)
            infinitePagerAdapter.notifyDataSetChanged();

        if (loutDashboard != null)
            loutDashboard.removeAllViews();
    }

    private void resetData(boolean isProgressVisible) {
        clearData();
        callDashboardList(isProgressVisible);
    }

    private void callDashboardList(boolean isProgressVisible) {

        if (globalClass.utility.checkInternetConnection(getActivity())) {

            if (isProgressVisible)
                pvProgressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.GET, WebService_Tag.M_API_dashboard_list, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pvProgressDialog.dismiss();
                    try {
                        Global_Class.jsonHomeObject = new JSONObject(response);
                        if (Global_Class.jsonHomeObject != null)
                            setHomeData(Global_Class.jsonHomeObject);
                        else
                            Toast.makeText(getActivity(), "Games are unavailable", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        pvProgressDialog.dismiss();
                        Toast.makeText(getActivity(), getActivity().getString(R.string.Msg_Error_Server), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i("HomeFragment 210",e.getMessage());
                        Toast.makeText(getActivity(),"Swipe down to refresh.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            queue.add(request);
        }

        loutSwipeRefresh.setRefreshing(false);
    }

    private void setHomeData(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(WebService_Tag.R_category);
            JSONArray jsonArrayPublisher = jsonObject.getJSONArray(WebService_Tag.R_publisher);
            JSONArray jsonArrayNewAdded = jsonObject.getJSONArray(WebService_Tag.R_app_last_ten);
            JSONArray jsonArrayTrending = jsonObject.getJSONArray(WebService_Tag.R_app_trad_ten);
            JSONArray jsonArrayAppCount = jsonObject.getJSONArray(WebService_Tag.R_app_count);
            JSONArray jsonArrayBanner = jsonObject.getJSONArray(WebService_Tag.R_banner);

            JSONObject jsonObjCate = jsonObject.getJSONObject(WebService_Tag.R_app_cate);

            if (jsonArrayBanner != null && jsonArrayBanner.length() != 0) {
                for (int i = 0; i < jsonArrayBanner.length(); i++) {
                    JSONObject objBanner = (JSONObject) jsonArrayBanner.get(i);
                    Element element = new Element();
                    element.setId(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_id));
                    element.setName(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_name));
                    element.setImage(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_image));
                    element.setCate_id(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_cate_id));
                    element.setPub_id(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_pub_id));
                    element.setApp_id(globalClass.getJsonObjectVal(objBanner, WebService_Tag.R_app_id));
                    arrSwipeCardList.add(element);
                }
            }

            if (jsonArray != null && jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objCategory = (JSONObject) jsonArray.get(i);
                    Element element = new Element();
                    element.setId(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_id));
                    element.setName(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_name));
                    element.setImage(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_image));
                    element.setPosition_order(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_position_order));
                    arrCategoryList.add(element);
                }
            }

            if (jsonArrayPublisher != null && jsonArrayPublisher.length() != 0) {
                for (int i = 0; i < jsonArrayPublisher.length(); i++) {
                    JSONObject objPublisher = (JSONObject) jsonArrayPublisher.get(i);
                    Element element = new Element();
                    element.setId(globalClass.getJsonObjectVal(objPublisher, WebService_Tag.R_id));
                    element.setName(globalClass.getJsonObjectVal(objPublisher, WebService_Tag.R_name));
                    element.setImage(globalClass.getJsonObjectVal(objPublisher, WebService_Tag.R_image));
                    element.setPosition_order(globalClass.getJsonObjectVal(objPublisher, WebService_Tag.R_position_order));
                    arrPublisherList.add(element);
                }
            }

            if (jsonArrayNewAdded != null && jsonArrayNewAdded.length() != 0) {
                for (int i = 0; i < jsonArrayNewAdded.length(); i++) {
                    JSONObject objNewAdded = (JSONObject) jsonArrayNewAdded.get(i);
                    Element element = new Element();
                    setNewAddedAndTrendingList(element, objNewAdded);
                    arrNewAddedList.add(element);
                }
            }

            if (jsonArrayTrending != null && jsonArrayTrending.length() != 0) {
                for (int i = 0; i < jsonArrayTrending.length(); i++) {
                    JSONObject objTrending = (JSONObject) jsonArrayTrending.get(i);
                    Element element = new Element();
                    setNewAddedAndTrendingList(element, objTrending);
                    arrTrendingList.add(element);
                }
            }

            if (jsonArrayAppCount != null && jsonArrayAppCount.length() != 0) {
                for (int i = 0; i < jsonArrayAppCount.length(); i++) {
                    JSONObject object = (JSONObject) jsonArrayAppCount.get(i);
                    NavigationActivity.txtTotalGames.setText(globalClass.getJsonObjectVal(object, WebService_Tag.R_total_app));
                }
            }

            setSwipeCardData();

            try {
                if(arrCategoryList==null){
                    Toast.makeText(getActivity(),"Swipe down to refresh.",Toast.LENGTH_SHORT).show();
                }else if (arrCategoryList != null && arrCategoryList.size() != 0) {
                    mapDashboardData.put(getActivity().getString(R.string.dashboard_Categories), arrCategoryList);
                }else {
                    Toast.makeText(getActivity(),"Swipe down to refresh.",Toast.LENGTH_SHORT).show();
                }
            }catch(NullPointerException e){
                Log.i("HomeFragment 297",e.getMessage());
                Toast.makeText(getActivity(),"Swipe down to refresh.",Toast.LENGTH_SHORT).show();
            }

            RealmResults<Model_RecentPlay_Games> arrRecentPlayedList = realmController.getecentPlayGames();

            if (arrRecentPlayedList != null && arrRecentPlayedList.size() > 0) {
                ArrayList<Element> temp = new ArrayList<>();
                int arrSize = arrRecentPlayedList.size();
                int startInt = arrSize > 10 ? (arrSize - 10) : 0;
                for (int i = arrSize - 1; i >= startInt; i--) {
                    Element element = new Element();
                    element.setId(arrRecentPlayedList.get(i).getId());
                    element.setName(arrRecentPlayedList.get(i).getName());
                    element.setIcon(arrRecentPlayedList.get(i).getIcon());
                    element.setImage("assets/app_icon/" + arrRecentPlayedList.get(i).getIcon());
                    element.setPosition_order("");
                    element.setCate_id(arrRecentPlayedList.get(i).getCate_id());
                    element.setPub_id("");
                    temp.add(element);
                }
                mapDashboardData.put(getActivity().getString(R.string.dashboard_Recent), temp);
            }

            if (arrTrendingList != null && arrTrendingList.size() != 0) {
                mapDashboardData.put(getActivity().getString(R.string.dashboard_Trending), arrTrendingList);
            }

            if (arrNewAddedList != null && arrNewAddedList.size() != 0) {
                mapDashboardData.put(getActivity().getString(R.string.dashboard_New_Added), arrNewAddedList);
            }

            if (arrPublisherList != null && arrPublisherList.size() != 0) {
                mapDashboardData.put(getActivity().getString(R.string.dashboard_Publisher), arrPublisherList);
            }

            if (jsonObjCate != null) {
                Iterator iterator = jsonObjCate.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    JSONArray jsonArrayCate = jsonObjCate.getJSONArray(key);

                    List<Element> arrTempCateList = new ArrayList<>();

                    if (jsonArrayCate != null) {
                        for (int i = 0; i < jsonArrayCate.length(); i++) {
                            JSONObject objCategory = (JSONObject) jsonArrayCate.get(i);
                            Element element = new Element();
                            element.setId(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_id));
                            element.setName(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_name));
                            element.setIcon(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_icon));
                            element.setImage("assets/app_icon/" + element.getIcon());
                            element.setPosition_order(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_position_order));
                            element.setCate_id(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_cate_id));
                            element.setPub_id(globalClass.getJsonObjectVal(objCategory, WebService_Tag.R_pub_id));
                            arrTempCateList.add(element);
                        }
                        mapDashboardData.put(key, arrTempCateList);
                    }
                }
            }

            if (mapDashboardData.size() != 0) {
                for (Map.Entry<String, List<Element>> map : mapDashboardData.entrySet()) {
                    setDashboardData(map.getKey(), map.getValue());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getActivity().getString(R.string.Msg_Error_Server), Toast.LENGTH_SHORT).show();
        }

        pvProgressDialog.dismiss();
    }

    private void setNewAddedAndTrendingList(Element element, JSONObject object) {
        element.setId(globalClass.getJsonObjectVal(object, WebService_Tag.R_id));
        element.setName(globalClass.getJsonObjectVal(object, WebService_Tag.R_name));
        element.setCate_id(globalClass.getJsonObjectVal(object, WebService_Tag.R_cate_id));

        element.setIcon(globalClass.getJsonObjectVal(object, WebService_Tag.R_icon));
        element.setImage(element.getIcon());

        element.setPub_id(globalClass.getJsonObjectVal(object, WebService_Tag.R_pub_id));
        element.setHtml_flash(globalClass.getJsonObjectVal(object, WebService_Tag.R_html_flash));
        element.setUrl(globalClass.getJsonObjectVal(object, WebService_Tag.R_url));
        element.setCounter(globalClass.getJsonObjectVal(object, WebService_Tag.R_counter));
        element.setPosition_order(globalClass.getJsonObjectVal(object, WebService_Tag.R_position_order));

    }

    private void setSwipeCardData() {

        Element element;

        /*element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/32/business-1012449_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/43/music-1012475_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/42/dance-1012474_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://png.pngtree.com/thumb_back/fw800/back_pic/00/04/30/4756233650d835b.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/32/business-1012449_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/43/music-1012475_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://cdn.pixabay.com/photo/2015/10/29/14/42/dance-1012474_960_720.jpg");
        arrSwipeCardList.add(element);

        element = new Element();
        element.setImage("https://png.pngtree.com/thumb_back/fw800/back_pic/00/04/30/4756233650d835b.jpg");
        arrSwipeCardList.add(element);*/

        if (arrSwipeCardList != null && arrSwipeCardList.size() != 0) {
            viewPager.setVisibility(View.VISIBLE);

            infinitePagerAdapter = new InfinitePagerAdapter(getActivity(), arrSwipeCardList, false);
            //infinitePager.setAdapter(infinitePagerAdapter);
            //  infinitePager.startAutoScroll(true);
            viewPager.setAdapter(infinitePagerAdapter);

           /* viewPager.setFadeEnabled(true);
            viewPager.setFadeFactor(0.6f);
            viewPager.setAnimationEnabled(true);
            viewPager.setCycle(false);*/

            // ======= UltraViewPager =======

            viewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);

         /*   viewPager.initIndicator();
            viewPager.getIndicator()
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setFocusColor(Color.GREEN)
                    .setNormalColor(Color.GRAY)
                    .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
            viewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            viewPager.getIndicator().build();*/

            viewPager.setMultiScreen(0.75f);
            viewPager.setItemRatio(1.0f);
            viewPager.setRatio(2.3f);
            // viewPager.setMaxHeight(800);

            viewPager.setPageTransformer(false, new UltraScaleTransformer());

            viewPager.setInfiniteLoop(true);
            viewPager.setAutoScroll(2500);

           /* swipeCardAdapter = new SwipeCardAdapter(getActivity(), arrSwipeCardList);
            swipeCard.setAdapter(swipeCardAdapter);
            swipeCard.setFlingListener(new SwipeCardView.OnCardFlingListener() {
                @Override
                public void onCardExitLeft(Object dataObject) {
                }

                @Override
                public void onCardExitRight(Object dataObject) {
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                }

                @Override
                public void onCardExitTop(Object dataObject) {
                }

                @Override
                public void onCardExitBottom(Object dataObject) {
                }
            });*/
        } else {
            viewPager.setVisibility(View.GONE);
        }
    }

    private void setDashboardData(String strTitle, List<Element> arrList) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.raw_dashboardmaindata, null);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(strTitle);
        final TextView btnSeeAll = view.findViewById(R.id.btnSeeAll);

        btnSeeAll.setVisibility(strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Recent)) ?
                View.GONE : View.VISIBLE);

        //btnSeeAll.setPaintFlags(btnSeeAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Categories))) {
            dashboardNavigationType = NavigationType.CATEGORYFILTER;
        } else if (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Publisher))) {
            dashboardNavigationType = NavigationType.PUBLISHERFILTER;
        } else if (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Trending))) {
            dashboardNavigationType = NavigationType.TRENDINGGAMES;
        } else if (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_New_Added))) {
            dashboardNavigationType = NavigationType.NEWADDEDGAMES;
        } else {
            dashboardNavigationType = NavigationType.CATEGORYGAMES;
        }

        try {
            btnSeeAll.setTag(strTitle + "&" + arrList.get(0).getCate_id() + "&" + String.valueOf(dashboardNavigationType));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Categories))) {
            isRoundedData = true;
        } else {
            isRoundedData = false;
        }

        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRoundedData = false;

                try {
                    String[] arrTagList = btnSeeAll.getTag().toString().split("&");

                    NavigationType navigationType = (NavigationType) getStringToNavigationType(arrTagList[2]);

                    switch (navigationType) {
                        case CATEGORYFILTER:
                            isRoundedData = true;
                            startActivity(new Intent(getActivity(), CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", NavigationType.CATEGORYALL)
                                    .putExtra("TITLE", getActivity().getString(R.string.dashboard_Categories))
                                    .putExtra("ID", ""));
                            break;
                        case CATEGORYGAMES:
                            isRoundedData = false;
                            startActivity(new Intent(getActivity(), CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", NavigationType.CATEGORYGAMES)
                                    .putExtra("TITLE", arrTagList[0])
                                    .putExtra("ID", arrTagList[1]));
                            break;
                        case PUBLISHERFILTER:
                            startActivity(new Intent(getActivity(), CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", NavigationType.PUBLISHERALL)
                                    .putExtra("TITLE", getActivity().getString(R.string.dashboard_Publisher))
                                    .putExtra("ID", ""));
                            break;
                        case TRENDINGGAMES:
                            startActivity(new Intent(getActivity(), CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", NavigationType.TRENDINGGAMES)
                                    .putExtra("TITLE", getActivity().getString(R.string.dashboard_Trending))
                                    .putExtra("ID", ""));
                            break;
                        case NEWADDEDGAMES:
                            startActivity(new Intent(getActivity(), CategoryActivity.class)
                                    .putExtra("NAVIGATIONTYPE", NavigationType.NEWADDEDGAMES)
                                    .putExtra("TITLE", getActivity().getString(R.string.dashboard_New_Added))
                                    .putExtra("ID", ""));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView listDashboardSubData = view.findViewById(R.id.listDashboardSubData);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, (strTitle.equalsIgnoreCase(getActivity().getString(R.string.dashboard_Recent)) ? true : false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, (false));
        listDashboardSubData.setLayoutManager(linearLayoutManager);

        listDashboardSubData.setAdapter(new DashboardDataAdapter(getActivity(), arrList, isRoundedData, dashboardNavigationType));
        listDashboardSubData.smoothScrollToPosition(0);
        loutDashboard.addView(view);
    }
}
