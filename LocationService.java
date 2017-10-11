package net.akg.com.trackme;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import net.akg.com.trackme.LocationData;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;



public class LocationService extends Service
{
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private DynamoDBMapper mapper;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJ7GYY5B4IRFU3IFQ","SHuzs1TAESNowYJUHn2vi7efo/pjEUR4UJfXBe5S");
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
        ddbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        mapper = new DynamoDBMapper(ddbClient);

    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
       try {
           locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (4000), 0, listener);
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (4000), 0, listener);
       }
       catch(Exception e)
       {
           Log.d("akg","permission not granted");
       }


    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.d("akg", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                //intent.putExtra("Latitude", loc.getLatitude());
                //intent.putExtra("Longitude", loc.getLongitude());
                //intent.putExtra("Provider", loc.getProvider());
                //sendBroadcast(intent);
                String latitude = ""+loc.getLatitude();
                String longitude = ""+loc.getLongitude();
                sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                //String username = sharedPreferences.getString("USERNAME","");
                String username = "140";

                if(username != "") {
                    JSONUpdateLocAsyncTask task = new JSONUpdateLocAsyncTask(latitude, longitude, username);
                    task.execute();
                }
            }
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

    class JSONUpdateLocAsyncTask extends AsyncTask<String, Void, Boolean> {

        private String latitude = null;
        private String longitude = null;
        private String username = null;

        JSONUpdateLocAsyncTask(String latitude,String longitude,String username)
        {
            this.latitude = latitude;
            this.longitude = longitude;
            this.username = username;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                Log.d("akg", "" + this.username + ";lat:" + this.latitude + ";long:" + this.longitude + ":" + this.username);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm aaa");
                String strDate = sdf.format(c.getTime());


                Log.d("akg","date"+strDate);

                LocationData loc = new LocationData();
                loc.setId(this.username);
                loc.setDate(strDate);
                loc.setLatitude(this.latitude.toString());
                loc.setLongitude(this.longitude.toString());
                mapper.save(loc);

                Log.d("akg", "mapper saved");





            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            Log.d("akg","loc updated");
        }
    }
}
