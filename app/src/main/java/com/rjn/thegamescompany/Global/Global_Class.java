package com.rjn.thegamescompany.Global;

import android.app.Activity;
import android.graphics.Bitmap;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by PIYUSH NAROLA on 08/12/2018.
 */

public class Global_Class {

    // LIVE URL
    public static String BASE_URL = "http://192.168.0.101/AdminLTE";

    // LOCAL LATEST URL
    //public static String BASE_URL = "http://demo.finestardiamonds.com/finerestservice.svc/";

    // LOCAL URL
    // public static String BASE_URL = "http://fine.futu
    // retechsoft.in/finerestservice.svc/";

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);

    public static Map<String, String> mapPassingSearchParam = new HashMap<>();
    public static List<Element> arrWatchSelectedList = new ArrayList<>();
    public static Map<String, String> mapResultSelectedData = new HashMap<>();
    public static Element arrDetailItem = new Element();

    public static List<Element> arrMyBidList = new ArrayList<>();

    /* ..... Notification Set ..... */
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "ah_firebase";
    public static String Customer_ID = "";
    public static String Notification_ID = "";
    public static boolean Notification_Flag = false;
    public static final String TOPIC_GLOBAL = "global";
    public static String RegDeviceId = "";

    public static String getStatus = "";

    public Utility utility;

    public static boolean Flag_CommaAppend;

    public static JSONObject jsonHomeObject = null;

    public Global_Class() {

    }

    public void init() {
        utility = new Utility();
    }

    public String getJsonObjectVal(JSONObject jsonObject, String key) {
        try {
            if (jsonObject != null)
                return jsonObject.has(key) ? jsonObject.getString(key) : "";
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isEmptyEdt(EditText editText) {
        boolean isEmpty = isEmpty(editText.getText().toString());
        if (isEmpty) {
            editText.requestFocus();
        }
        return isEmpty;
    }

    public boolean isEmpty(String s) {
        return s == null || s.equals("null") || s.trim().equals("") || s.isEmpty() ? true : false;
    }

    public String getEdtVal(EditText editText) {
        return editText.getText().toString().trim();
    }

    public void toastView(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public String setTwoPointDecimalFormatter(String val) {
        return !isEmpty(val) ? String.format("%.2f", Double.parseDouble(val)) : "0.00";
    }

    public String setDecimalFormatter(String val) {
        return !isEmpty(val) ? new DecimalFormat("#,##,###,####.00").format(Double.parseDouble(val)) : "0.00";
        // return val;
    }

    public boolean isValueNegativeOrNot(String val) {
        return !isEmpty(val) ? Double.compare(Double.parseDouble(val), 0.0) < 0 : false;
        // return val;
    }

    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern) ? true : false;
    }

    public String isSelectedDataKeyExists(String key, Map<String, String> mapSelectedData) {
        return mapSelectedData.containsKey(key) ? mapSelectedData.get(key) : "";
    }

    public String getSelectedItemsToQuery(String strType, String strSelectedItems) {
        return " AND " + strType + " IN (" + strSelectedItems + ")";
    }

    public interface OnLoginInterface {
        void OnLogin(String username, String password);
    }
    //endregion

    public void setRoundedBitmap(Activity activity, ImageView imageView, float radius, Bitmap bitmap) {
        RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(activity.getResources(), bitmap);
        RBD.setCornerRadius(radius);
        RBD.setAntiAlias(true);
        imageView.setImageDrawable(RBD);
    }
}
