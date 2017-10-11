package net.akg.com.trackme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity  {
    //private Button btnTrackMe;
    private static MainActivity activity;

  /*  private EditText userName;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //setContentView(R.layout.activity_main);
        //btnTrackMe = (Button)findViewById(R.id.btnTrackMe);
        //userName = (EditText)findViewById(R.id.userName);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            }


            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }


        }

        startTracking();
        finish();




        /*sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String prevusername = sharedPreferences.getString("USERNAME", "");
        if(!prevusername.isEmpty())
        {
            userName.setText(prevusername);
        }

        btnTrackMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if(btnTrackMe.getText().toString().toUpperCase() == "STOP SURVEY")
                {
                    btnTrackMe.setText("START SURVEY");
                    userName.setEnabled(true);
                    stopTracking();
                }
                else {
                    String strUserName = userName.getText().toString();
                    Log.d("akg", "btnclick" + strUserName);
                    if (strUserName.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "PLEASE ENTER THE NAME", Toast.LENGTH_SHORT).show();
                    } else {
                        userName.setEnabled(false);
                        btnTrackMe.setText("STOP SURVEY");
                        editor.putString("USERNAME", strUserName);
                        editor.commit();
                        startTracking();
                    }
                }
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static void startTracking()
    {
      Log.d("akg", "start service from mainactivity");
        MainActivity.activity.startService(new Intent(MainActivity.activity, LocationService.class));
    }

    public static void stopTracking()
    {
        Log.d("akg", "stop service from mainactivity");
        MainActivity.activity.stopService(new Intent(MainActivity.activity, LocationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* btnTrackMe.setText("START SURVEY");
        userName.setEnabled(true);*/
    }
}
