package com.rjn.thegamescompany.Custom;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rjn.thegamescompany.R;

public class PVProgressDialog {

    private Activity activity;

    //private ProgressDialog progressDialog;
    private Dialog progressDialog;

    public PVProgressDialog(Activity activity) {
        this.activity = activity;

        View view = LayoutInflater.from(activity).inflate(R.layout.pvprogressdialog, null);

        progressDialog = new Dialog(activity);
        progressDialog.setCancelable(false);
        ImageView imgLogo = view.findViewById(R.id.imgLogo);
        imgLogo.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_anim));
        progressDialog.setContentView(view);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

      /*  progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);*/
    }

    public void show() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    public void dismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
