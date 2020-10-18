package com.rjn.thegamescompany.Global;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by PIYUSH NAROLA on 12/12/2018.
 */

public class Utility {

    public Utility() {

    }

    public boolean checkInternetConnection(Activity Activity) {
        ConnectivityManager cm = (ConnectivityManager) Activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = Activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //window.setStatusBarColor(Activity.getResources().getColor(R.color.Dark_Gray));
            }*/
            return true;
        } else {
            Toast.makeText(Activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            //new Alert_Ammaziya(Activity, "Please Check Your Internet Connection");
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = Activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.RED);
            }*/
            return false;
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String RemoveFirstChar(String s) {
        return s.trim().substring(1);
    }


    public int getActionBarHeight(Activity Activity) {
        TypedValue tv = new TypedValue();
        Activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = Activity.getResources().getDimensionPixelSize(tv.resourceId);
        return actionBarHeight;
    }

    public int getStatusBarHeight(Activity Activity) {
        int result = 0;
        int resourceId = Activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        Log.i("extension : ", "" + extension);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Log.i("TYPE : ", "" + type);
        }
        return type;
    }

    public static String getURLExtension(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return extension;
    }

    public Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public String getTomorrowDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return simpleDateFormat.format(System.currentTimeMillis() - 1001);
    }

    public String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        } catch (MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }

    /*public String getUriToBase64(Activity activity, Uri imgUri) {

        String strBase64 = "";

        try {
            final InputStream imageStream = activity.getContentResolver().openInputStream(imgUri);
            final Bitmap bm = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            Bitmap bitmap = PVImageCompressor.with(activity).getCompressBitmap(imgUri.toString());
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
            byte[] b = baos.toByteArray();
            strBase64 = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBase64;
    }*/

    public String getBitmapToBase64(Activity activity, Bitmap selectedBitmap) {
        String strBase64 = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
            byte[] b = baos.toByteArray();
            strBase64 = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBase64;
    }

    public String getBitmapToUri(Activity activity, Bitmap bitmap) {
        try {
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, null, null);
            return Uri.parse(path).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Bitmap getUriToBitmap(Activity activity, Uri imgUri) {

        Bitmap bitmap = null;

        try {
            //compressImage(activity, imgUri.toString());
            final InputStream imageStream = activity.getContentResolver().openInputStream(imgUri);
            bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap compressImage(Activity activity, String imageUri) {

        String filePath = getRealPathFromURI(activity, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            File fdelete = new File(filename);
            File filePathDelete = new File(filePath);

            fdelete.delete();
            if (fdelete.exists()) {
                try {
                    fdelete.getCanonicalFile().delete();
                    if (fdelete.exists()) {
                        activity.deleteFile(fdelete.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            filePathDelete.delete();
            if (filePathDelete.exists()) {
                try {
                    filePathDelete.getCanonicalFile().delete();
                    if (filePathDelete.exists()) {
                        activity.deleteFile(filePathDelete.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

           /* if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :" + filename);
                } else {
                    System.out.println("file not Deleted :" + filename);
                }
            }

            if (filePathDelete.exists()) {
                if (filePathDelete.delete()) {
                    System.out.println("file Deleted :" + filePath);
                } else {
                    System.out.println("file not Deleted :" + filePath);
                }
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return scaledBitmap;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "The Games Company/Image");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(Activity activity, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    public boolean isWriteStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public static boolean appInstalledOrNot(Activity activity, String uri) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static void rateUSApp(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void goToGmailFeedBack(Activity activity) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"manufacturingbiss@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The Games Company Feedback");
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            final PackageManager pm = activity.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            activity.startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moreApps(Activity activity) {
        Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=8412834193356581377");
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    uri));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAppVersion(Activity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
