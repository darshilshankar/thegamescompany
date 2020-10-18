package com.rjn.thegamescompany.Custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.rjn.thegamescompany.Global.Global_Class;
import com.rjn.thegamescompany.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by PIYUSH NAROLA on 08/05/2018.
 */

public class PVAsyncCall {

    // https://github.com/race604/WaveLoading
    private Activity activity;
    private String methodName = "";
    private Map<String, String> mapRequestTag;
    private boolean isProgressDialogVisible = true;
    private EnumAsyncCall enumAsyncCall;
    private OnPVPostExecuteInterface onPVPostExecuteInterface;

    private ProgressDialog progressDialog;
    private String strResponse = "";

    private Global_Class globalClass = new Global_Class();

    public PVAsyncCall(Activity activity, String methodName, Map<String, String> mapRequestTag, boolean isProgressDialogVisible, EnumAsyncCall enumAsyncCall, OnPVPostExecuteInterface onPVPostExecuteInterface) {
        this.activity = activity;
        this.methodName = methodName;
        this.mapRequestTag = mapRequestTag;
        this.isProgressDialogVisible = isProgressDialogVisible;
        this.enumAsyncCall = enumAsyncCall;
        this.onPVPostExecuteInterface = onPVPostExecuteInterface;

        globalClass.init();

        if (globalClass.utility.checkInternetConnection(activity))
            new callAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class callAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isProgressDialogVisible) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setCancelable(false);
                progressDialog.setTitle(activity.getResources().getString(R.string.app_name));
                progressDialog.setTitle("Please wait...");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpClient httpClient = new DefaultHttpClient();

            JSONObject object = new JSONObject();
            try {

                HttpParams httpParams = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParams, 10000);
                HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                HttpConnectionParams.setSoTimeout(httpParams, 10000);
                String Url = "";

                switch (enumAsyncCall) {
                    case ASYNC_GET:
                        StringBuilder stringBuilder = new StringBuilder();
                        if (mapRequestTag.size() != 0) {
                            for (Map.Entry<String, String> map : mapRequestTag.entrySet()) {
                                stringBuilder.append("&" + map.getKey() + "=" + map.getValue());
                            }
                        }
                        Url = (Global_Class.BASE_URL + methodName + (!globalClass.isEmpty(stringBuilder.toString()) ? "?" + stringBuilder.substring(1) : "")).replaceAll(" ", "%20");
                        break;
                    case ASYNC_POST:
                        if (mapRequestTag.size() != 0) {
                            for (Map.Entry<String, String> map : mapRequestTag.entrySet()) {
                                object.put(map.getKey(), map.getValue());
                            }
                        }
                        Url = object.toString();
                        break;
                }

                Log.i("Url_" + methodName, Url);

                HttpResponse response = null;
                switch (enumAsyncCall) {
                    case ASYNC_GET:
                        HttpGet httpget = new HttpGet(Url);
                        response = httpClient.execute(httpget);
                        break;
                    case ASYNC_POST:
                        HttpPost POST = new HttpPost(Global_Class.BASE_URL + methodName);
                        POST.setEntity(new StringEntity(Url, "UTF8"));
                        POST.setHeader("Content-type", "application/json");
                        response = httpClient.execute(POST);
                        break;
                }
                strResponse = EntityUtils.toString(response.getEntity());

                Log.i("Result_" + methodName, strResponse);

            } catch (Exception e) {
                Log.d("LOG", "" + e.toString());
                strResponse = "";
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (globalClass.isEmpty(strResponse)) {
                Toast.makeText(activity, "Server time out please try again.", Toast.LENGTH_SHORT).show();
                onPVPostExecuteInterface.onPVPostExecute(null, "", "", "", true);
            } else {
                try {
                    String ErrorCode = "";
                    String ErrorMessage = "";
                    String AlertTypeCode = "";
                    JSONObject jsonObject = new JSONObject(strResponse);
                    onPVPostExecuteInterface.onPVPostExecute(jsonObject, ErrorCode, ErrorMessage, AlertTypeCode, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    onPVPostExecuteInterface.onPVPostExecute(null, "", "", "", true);
                }
            }

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    public interface OnPVPostExecuteInterface {
        void onPVPostExecute(JSONObject responseJsonObject, String ErrorCode, String ErrorMessage, String AlertTypeCode, boolean isException);
    }

    public enum EnumAsyncCall {
        ASYNC_GET, ASYNC_POST
    }
}
