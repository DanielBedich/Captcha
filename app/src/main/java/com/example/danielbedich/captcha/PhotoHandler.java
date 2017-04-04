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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.danielbedich.captcha.AdminReceiver;

public class PhotoHandler implements PictureCallback  {

    private final Context context;
    GMailSender sender;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    String mCurrentPhotoPath;
    private String ImagePath;
    private Uri URI;
    public File pictureFile;

    private ImageView mImageView;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

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

        addImageToGallery(pictureFile.getAbsolutePath());
        sender = new GMailSender("captchaosu@gmail.com", "captcha4471");

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
                sender.sendMailAttachment("Captcha Test", "Yay it works!", "captchaosu@gmail.com", "danielbedich@gmail.com", pictureFile);
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
}
