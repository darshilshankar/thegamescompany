package com.rjn.thegamescompany.Global;

import android.app.Activity;
import android.app.Fragment;

import com.rjn.thegamescompany.HomeFragment;
import com.rjn.thegamescompany.R;

public class GoToFragment {

    private Activity activity;
    private Enum_Games.NavigationType navigationType;

    public GoToFragment() {

    }

    public void gotoFragment(Activity activity, Enum_Games.NavigationType navigationType) {

        Fragment fragment = null;

        switch (navigationType) {
            case DASHBOARD:
                fragment = new HomeFragment();
                break;
        }

        if (fragment != null) {
            activity.getFragmentManager().beginTransaction().replace(R.id.loutFrame, fragment).commit();
        }
    }

}
