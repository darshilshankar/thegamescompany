package com.rjn.thegamescompany.Adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.rjn.thegamescompany.CategoryActivity;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Enum_Games;
import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.Global.WebService_Tag;
import com.rjn.thegamescompany.PlayGameActivity;
import com.rjn.thegamescompany.R;

import java.util.List;

public class InfinitePagerAdapter extends PagerAdapter {

    private Activity activity;
    private List<Element> arrList;
    private boolean mIsTwoWay = false;

    Global_Class globalClass = new Global_Class();

    public InfinitePagerAdapter(Activity activity, List<Element> arrList, boolean mIsTwoWay) {
        this.activity = activity;
        this.arrList = arrList;
        this.mIsTwoWay = mIsTwoWay;
    }

    @Override
    public int getCount() {
        return mIsTwoWay ? 6 : arrList.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Element arrItem = arrList.get(position);
        View view = null;
        if (mIsTwoWay) {
            /*view = LayoutInflater.from(activity).inflate(R.layout.two_way_item, container, false);

            final VerticalInfiniteCycleViewPager verticalInfiniteCycleViewPager = view.findViewById(R.id.vicvp);
            verticalInfiniteCycleViewPager.setAdapter(new VerticalPagerAdapter(activity));
            verticalInfiniteCycleViewPager.setCurrentItem(position);*/
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.raw_infinitepager, container, false);
        }

        ImageView imgRaw = view.findViewById(R.id.imgRaw);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        if (arrItem.getImage() != null && !arrItem.getImage().isEmpty()) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(14));
            Glide.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getImage().replaceAll(" ", "%20")).apply(requestOptions).into(imgRaw);
            progressBar.setVisibility(View.GONE);
        } else {
            imgRaw.setImageResource(0);
            progressBar.setVisibility(View.GONE);
        }

        imgRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Enum_Games.NavigationType navigationTypeTemp = null;
                if (!globalClass.isEmpty(arrItem.getApp_id())) {
                    activity.startActivity(new Intent(activity, PlayGameActivity.class).putExtra("ARRITEM", arrItem));
                } else if (!globalClass.isEmpty(arrItem.getCate_id())) {
                    navigationTypeTemp = Enum_Games.NavigationType.CATEGORYGAMES;
                } else if (!globalClass.isEmpty(arrItem.getPub_id())) {
                    navigationTypeTemp = Enum_Games.NavigationType.PUBLISHERGAMES;
                } else {
                    activity.startActivity(new Intent(activity, PlayGameActivity.class).putExtra("ARRITEM", arrItem));
                }

                if (navigationTypeTemp != null) {
                    activity.startActivity(new Intent(activity, CategoryActivity.class)
                            .putExtra("NAVIGATIONTYPE", navigationTypeTemp)
                            .putExtra("TITLE", arrItem.getName())
                            .putExtra("ID", navigationTypeTemp == Enum_Games.NavigationType.PUBLISHERGAMES ? arrItem.getPub_id() : arrItem.getCate_id()));
                }
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
