package  net.akg.com.deviceunlockdetection;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MyReceiver extends DeviceAdminReceiver {
    SharedPreferences sharedPreferences;
    private String strEmail;
    private String strPassword;
    private String strFromEmail;
    private File pictureFileDir;
    private static String filename;
    private String photoFile = "Picture.jpg";
    public LocationManager locationManager;
    public MyLocationListener listener;
    public String latitude ;
    public String longitude;
    private String fromEmail;
    private String strAttempt;

    private  Camera cam = null;
    private byte[] photoData;
    public static int counter = 0;

    public MyReceiver() {
        Log.d("akg","MyReceiver");

    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {

        locationManager = (LocationManager) ctxt.getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (4000), 0, listener);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (4000), 0, listener);

        } catch (Exception e) {
            Log.d("akg", "permission not granted");
        }

        Log.d("akg","onPasswordFailed");
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctxt);
        Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG)
                .show();

        strEmail = sharedPreferences.getString("email", "");
        strPassword = sharedPreferences.getString("password","");
        strFromEmail = sharedPreferences.getString("fromemail","");
        strAttempt = sharedPreferences.getString("attempt","1");
        Log.d("akg","strAttempt"+strAttempt);

        int attempt = Integer.parseInt(strAttempt.toString());

        counter =  counter + 1;

        if(counter ==  attempt) {

            if(strFromEmail.isEmpty()) {
                fromEmail = "mobileappcommunication@gmail.com";
            }
            else
            {
                fromEmail = strFromEmail.toString();
            }
            if(strPassword.isEmpty()) {
                strPassword = "omsairam009";
            }
            else
            {
                strPassword = strPassword.toString();
            }



            Log.d("akg", "emailto" + strEmail);
            Log.d("akg", "emailfrom" + fromEmail);
            Log.d("akg", "emailpswd" + strPassword);
            captureImage(ctxt);
            counter = 0;
        }
        else
        {
            //do nothing;
        }



    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        counter = 0;
    }

    public void sendEmail(Context ctxt,String strToEmail,String strFromEmail,String strPassword)
    {

            EmailAsyncTask task = new EmailAsyncTask(ctxt, strToEmail, strFromEmail, strPassword);
            task.execute();

    }


    class EmailAsyncTask extends AsyncTask<String, Void, Boolean> {

        private Context ctxt = null;
        private String strToEmail;
        private String strFromEmail;
        private String strPassword;


        EmailAsyncTask(Context ctxt,String strToEmail,String strFromEmail,String strPassword)
        {

            this.ctxt = ctxt;
            this.strFromEmail = strFromEmail;
            this.strToEmail = strToEmail;
            this.strPassword = strPassword;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                Log.d("akg","attachment"+filename);
                Mail m = new Mail(strFromEmail, strPassword);
                String[] toArr = {strToEmail};
                m.setTo(toArr);
                m.setFrom(strFromEmail);
                m.setSubject("DUD:Someone has tried unlocking your phone.");
                m.setBody("Location link:\nhttps://maps.google.com/maps?q="+latitude+"%2C"+longitude);
                m.addAttachment(filename);

                if(m.send()) {

                    Log.d("akg", "Email was sent successfully");
                } else {

                    Log.d("akg", "Email was not sent");
                }
            } catch(Exception e) {
                Log.d("akg", "Could not send email"+e);
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            Log.d("akg","mail sent");
            counter = 0;
        }
    }

    private void releaseCameraAndPreview() {
        if (cam != null) {
            cam.release();
            cam = null;
        }
    }
    public void captureImage(final Context context){



        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camNo, camInfo);
            if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                releaseCameraAndPreview();
                cam = Camera.open(camNo);
                SurfaceTexture st = new SurfaceTexture(context.MODE_PRIVATE);
                try{
                    cam.setPreviewTexture(st);}
                catch(Exception e){}
                cam.startPreview();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cam.takePicture(null, null, new PhotoHandler(context));
                    }
                }, 1000);


                Log.d("akg","facing front");
            }
        }
        if (cam == null) {
            // no front-facing camera, use the first back-facing camera instead.
            // you may instead wish to inform the user of an error here...
            cam = Camera.open();
            Log.d("akg","camera open"+cam);
        }
    }


    public class PhotoHandler implements Camera.PictureCallback {

        private final Context context;

        public PhotoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            photoData = data;
            Log.d("akg","picture Taken"+data.length);
            Log.d("akg", "picture Taken" + photoData.length);


            pictureFileDir = getDir();

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                Log.d("akg", "Can't create directory to save image.");
                Toast.makeText(context, "Can't create directory to save image.",
                        Toast.LENGTH_LONG).show();
                return;

            }

            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            //String date = dateFormat.format(new Date());


            filename = pictureFileDir.getPath() + File.separator + photoFile;

            File pictureFile = new File(filename);

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Toast.makeText(context, "New Image saved:" + photoFile,
                        Toast.LENGTH_LONG).show();
                //SEND EMAIL
                sendEmail(context, strEmail, fromEmail,strPassword);
            } catch (Exception error) {
                Log.d("akg", "File" + filename + "not saved: "
                        + error.getMessage());
                Toast.makeText(context, "Image could not be saved.",
                        Toast.LENGTH_LONG).show();
            }
        }

        private File getDir() {
            File sdDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return new File(sdDir, "CameraAPIDemo");
        }
    }


    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.d("akg", "Location changed");
                loc.getLatitude();
                loc.getLongitude();
                latitude = ""+loc.getLatitude();
                longitude = ""+loc.getLongitude();
                Log.d("akg", "location"+latitude+"long"+longitude);
        }

        public void onProviderDisabled(String provider)
        {
            //Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            //Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }


}

