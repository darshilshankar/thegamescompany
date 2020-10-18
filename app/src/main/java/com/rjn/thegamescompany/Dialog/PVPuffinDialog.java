package com.rjn.thegamescompany.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.rjn.thegamescompany.PlayGameActivity;
import com.rjn.thegamescompany.R;

public class PVPuffinDialog {

    private Activity activity;
    private String strURL = "";

    public PVPuffinDialog(final Activity activity, String strURL) {
        this.activity = activity;
        this.strURL = strURL;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_puffin);
        dialog.setCanceledOnTouchOutside(true);

        Button btnBack = dialog.findViewById(R.id.btnBack);
        Button btnPlay = dialog.findViewById(R.id.btnPlay);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.cloudmosa.puffinFree");
                    //String url = "https://games.inbox.lv/mini/game/strip-or-die/?language=en";
                    intent.setData(Uri.parse(PlayGameActivity.strGameURL));
                    activity.startActivity(intent);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cloudmosa.puffinFree"));
                    activity.startActivity(browserIntent);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

}
