package com.rjn.thegamescompany.Adapter;

import android.app.Activity;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rjn.thegamescompany.Global.Element;
import com.rjn.thegamescompany.R;

import java.util.List;

public class HelpPagerAdapter extends PagerAdapter {

    private Activity activity;
    private List<Element> arrList;
    private OnHelpImageClickInterface onHelpImageClickInterface;

    public HelpPagerAdapter(Activity activity, List<Element> arrList, OnHelpImageClickInterface onHelpImageClickInterface) {
        this.activity = activity;
        this.arrList = arrList;
        this.onHelpImageClickInterface = onHelpImageClickInterface;
    }

    @Override
    public int getCount() {
        return arrList.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Element arrItem = arrList.get(position);
        View view = null;

        view = LayoutInflater.from(activity).inflate(R.layout.raw_helppager, container, false);

        RelativeLayout loutHelpPager = view.findViewById(R.id.loutHelpPager);
        ImageView imgRaw = view.findViewById(R.id.imgRaw);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        Button btnContinue = view.findViewById(R.id.btnContinue);

        txtTitle.setText(arrItem.getTitle());
        txtDescription.setText(arrItem.getDescription());

        if (arrItem.getBGColor() != null && !arrItem.getBGColor().isEmpty()) {
            loutHelpPager.setBackgroundColor(Integer.parseInt(arrItem.getBGColor()));
        } else {
            loutHelpPager.setBackgroundColor(activity.getResources().getColor(R.color.C_WHITE));
        }

        btnContinue.setText(position == arrList.size() - 1 ? activity.getResources().getString(R.string.Explore) : activity.getResources().getString(R.string.Continue));

       /* if (arrItem.getImage() != null && !arrItem.getImage().isEmpty()) {
            imgRaw.setImageResource(Integer.parseInt(arrItem.getImage()));
        } else {
            imgRaw.setImageResource(0);
        }*/

        imgRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpImageClickInterface.onHelpImageClick(position);
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

    public interface OnHelpImageClickInterface {
        void onHelpImageClick(int pos);
    }
}
