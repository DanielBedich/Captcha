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
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PhotoAndEmailHandler implements PictureCallback  {

    //context of the app, very important!
    private final Context context;
    public PhotoAndEmailHandler(Context context) {
        this.context = context;
    }

    //Email variables
    GMailSender sender;
    private String senderEmail = "captchaosu@gmail.com";
    private String senderPassword = "captcha4471";
    private String userEmail;

    //Time variables
    private SimpleDateFormat simpleDateFormat;
    private String format;

    //GPS variables
    private String mLat;
    private String mLong;
    private final String emailSubject = "CAPTCHA! ALERT: POTENTIAL INTRUDER DETECTED!";

    //Picture variables
    public File pictureFile;


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //set user email
        userEmail = getDefaultSharedPreferences(context).getString("EMAIL", "Error: no email");

        //set directory of picture taken
        File pictureFileDir = getDir();

        //Make sure directory was made
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(AdminReceiver.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        //set time of intrusion for image name format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());

        //name image file
        String photoFile = "Picture_" + date + ".jpg";

        //Name image file with path of directory
        String filename = pictureFileDir.getPath() + File.separator + photoFile;
        //System.out.println("Filename" + filename);

        //Create the image file
        pictureFile = new File(filename);

        //System.out.println(pictureFile.getAbsolutePath());

        //Save image file...somewhere?
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

        //save image file to device's gallery app
        addImageToGallery(pictureFile.getAbsolutePath());

        //set time of intrusion for email
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        format = simpleDateFormat.format(new Date());

        //get gps locations for email
        getGPSCoordinates();

        //set instance of GMailSender to Captcha!'s email
        sender = new GMailSender(senderEmail, senderPassword);

        //New thread?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();

        //Send email in background through AsyncClass
        StrictMode.setThreadPolicy(policy);
        try {
            new MyAsyncClass().execute();
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    //Get directory where image will be saved
    private File getDir() {
        String storageDir = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
        return new File(storageDir, "Captcha!");
    }

    //Saves image to device's gallery
    public void addImageToGallery(final String filePath) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    //Class sends email in another thread
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            try {
                // Add subject, body, Captcha! mail Id, user mail Id, and attachment.
                sender.sendMailAttachment(emailSubject, getEmailBody(), senderEmail, userEmail, pictureFile);
            }
            catch (Exception ex) {
            }
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                //sender.sendMail("Captcha Test", "Yay it works!", "captchaosu@gmail.com", "dbedich@yahoo.com");
            }
            catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            //Toast.makeText(context, "Email send", Toast.LENGTH_SHORT).show();
        }
    }

    //Sets gps variables
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

    //Creates body of email
    public String getEmailBody(){
        return "CAPTCHA! has detected a potential intruder!\n" +
                "The time of intrusion was " + format + "\n" +
                "View their location at the following link:\n" +
                "https://www.google.com/maps/@"+mLat+","+mLong+",13z\n" +
                "View the attached image for who CAPTCHA! caught!";
    }
}
