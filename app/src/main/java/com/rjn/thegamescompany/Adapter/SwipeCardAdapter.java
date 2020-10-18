package com.rjn.thegamescompany.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.WebService_Tag;
import com.rjn.thegamescompany.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class SwipeCardAdapter extends ArrayAdapter<Element> {

    private Activity activity;
    private List<Element> arrList;

    public SwipeCardAdapter(@NonNull Activity activity, List<Element> arrList) {
        super(activity, -1, arrList);
        this.activity = activity;
        this.arrList = arrList;
    }

    public class Holder {

        RelativeLayout loutRawSwipeCard;

        ImageView imgSwipeCard;

        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        final Holder holder;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_swipecard, null);
            holder = new Holder();
            holder.loutRawSwipeCard = view.findViewById(R.id.loutRawSwipeCard);
            holder.imgSwipeCard = view.findViewById(R.id.imgSwipeCard);
            holder.progressBar = view.findViewById(R.id.progressBar);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        final int pos = position;
        final Element arrItem = arrList.get(position);

        holder.progressBar.setVisibility(View.VISIBLE);
        if (arrItem.getUrl() != null && !arrItem.getUrl().isEmpty()) {
            Picasso.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getImage()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // new PVCornerView(activity, holder.imgIcon, 10);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(10);
                    roundedBitmapDrawable.setAntiAlias(true);
                    holder.imgSwipeCard.setImageDrawable(roundedBitmapDrawable);
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    holder.imgSwipeCard.setImageResource(0);
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            holder.imgSwipeCard.setImageResource(0);
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.loutRawSwipeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override public Element getItem(int position) {
        return arrList.get(position);
    }

    @Override public int getCount() {
        return arrList.size();
    }
}
