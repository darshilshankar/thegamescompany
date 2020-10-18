package com.rjn.thegamescompany.Adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.rjn.thegamescompany.CategoryActivity;
import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.Global.Enum_Games;
import com.rjn.thegamescompany.Global.WebService_Tag;
import com.rjn.thegamescompany.PlayGameActivity;
import com.rjn.thegamescompany.R;

import java.util.List;

public class DashboardDataAdapter extends RecyclerView.Adapter<DashboardDataAdapter.DashboardDataHolder> {

    private Activity activity;
    private List<Element> arrList;
    private boolean isRounded = false;
    private Enum_Games.NavigationType navigationType;

    public DashboardDataAdapter(Activity activity, List<Element> arrList, boolean isRounded, Enum_Games.NavigationType navigationType) {
        this.activity = activity;
        this.arrList = arrList;
        this.isRounded = isRounded;
        this.navigationType = navigationType;
    }

    @NonNull
    @Override
    public DashboardDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_dashboarddata, null);
        return new DashboardDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DashboardDataHolder holder, int i) {

        final int pos = i;
        final Element arrItem = arrList.get(i);

        holder.txtName.setText(arrItem.getName());

        holder.imgIcon.setBackgroundResource(isRounded ? R.drawable.bg_dedede_round : R.drawable.bg_category_ideal);

        if (arrItem.getImage() != null && !arrItem.getImage().isEmpty()) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(isRounded ? 100 : 15));
            Glide.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getImage().replaceAll(" ", "%20")).apply(requestOptions).into(holder.imgIcon);

            /*Picasso.with(activity).load(WebService_Tag.MAIN_URL + arrItem.getImage()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // new PVCornerView(activity, holder.imgIcon, 10);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(isRounded ? 100 : 10);
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

        holder.loutDashboardDataRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrItem.getName().toUpperCase().equals("ALL GAMES")) {
                    activity.startActivity(new Intent(activity, CategoryActivity.class)
                            .putExtra("NAVIGATIONTYPE", Enum_Games.NavigationType.ALLGAMES)
                            .putExtra("TITLE", "All Games")
                            .putExtra("ID", ""));
                } else {
                    Enum_Games.NavigationType navigationTypeTemp = null;
                    if (navigationType != null) {
                        switch (navigationType) {
                            case CATEGORYFILTER:
                                navigationTypeTemp = Enum_Games.NavigationType.CATEGORYGAMES;
                                break;
                            case PUBLISHERFILTER:
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class DashboardDataHolder extends RecyclerView.ViewHolder {

        public LinearLayout loutDashboardDataRaw;
        public ImageView imgIcon;
        public TextView txtName;

        public DashboardDataHolder(@NonNull View itemView) {
            super(itemView);
            this.loutDashboardDataRaw = itemView.findViewById(R.id.loutDashboardDataRaw);
            this.imgIcon = itemView.findViewById(R.id.imgIcon);
            this.txtName = itemView.findViewById(R.id.txtName);
        }
    }
}
