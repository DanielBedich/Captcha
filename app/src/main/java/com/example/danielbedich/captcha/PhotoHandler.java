package com.example.danielbedich.captcha;

/**
 * Created by DanielBedich on 4/2/17.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.danielbedich.captcha.AdminReceiver;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PhotoHandler implements PictureCallback  {

    private final Context context;
    public PhotoHandler(Context context) {
        this.context = context;
    }

    GMailSender sender;
    private String senderEmail = "captchaosu@gmail.com";
    private String senderPassword = "captcha4471";
    private String userEmail;

    SimpleDateFormat simpleDateFormat;
    String format;

    private String mLat;
    private String mLong;
    private final String emailSubject = "CAPTCHA! ALERT: POTENTIAL INTRUDER DETECTED!";

    String mCurrentPhotoPath;
    private String ImagePath;
    private Uri URI;
    public File pictureFile;

    private ImageView mImageView;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        userEmail = getDefaultSharedPreferences(context).getString("EMAIL", "Error: no email");

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(AdminReceiver.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;
        System.out.println("Filename" + filename);

        pictureFile = new File(filename);

        System.out.println(pictureFile.getAbsolutePath());


        //URI = Uri.parse(pictureFile.getAbsolutePath());

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d(AdminReceiver.DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        format = simpleDateFormat.format(new Date());
        addImageToGallery(pictureFile.getAbsolutePath());
        getGPSCoordinates();
        sender = new GMailSender(senderEmail, senderPassword);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            new MyAsyncClass().execute();
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private File getDir() {
        String storageDir = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
        return new File(storageDir, "Captcha!");
    }

    public void addImageToGallery(final String filePath) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            try {
                System.out.println("HEY Do try");
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMailAttachment(emailSubject, getEmailBody(), senderEmail, userEmail, pictureFile);
            }
            catch (Exception ex) {
                System.out.println("HEY Do catch");
            }
            System.out.println("HEY Pre");
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                System.out.println("HEY Do try");
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Captcha Test", "Yay it works!", "captchaosu@gmail.com", "dbedich@yahoo.com");
            }
            catch (Exception ex) {
                System.out.println("HEY Do catch");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("HEY Post");
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(context, "Email send", Toast.LENGTH_SHORT).show();
        }
    }

    public void getGPSCoordinates(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLat = location.getLatitude() + "";
        mLong = location.getLongitude() + "";
    }

    public String getEmailBody(){
        return "CAPTCHA! has detected a potential intruder!\n" +
                "The time of intrusion was " + format + "\n" +
                "View their location at the following link:\n" +
                "https://www.google.com/maps/@"+mLat+","+mLong+",13z\n" +
                "View the attached image for who CAPTCHA! caught!";
    }
}
