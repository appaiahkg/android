package  net.akg.com.deviceunlockdetection;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends ActionBarActivity {

    private EditText txtEmail;
    //private EditText txtPassword;
    private Button btnSave;
    private Button btnCam;
    private Button btnEnable;

    private String strEmail;
    //private String strPassword;
    public static MainActivity activity;
    private AdView mAdView;




    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        //txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnSave = (Button)findViewById(R.id.btnSave);
  /*      btnCam = (Button)findViewById(R.id.btnCam);
        btnEnable = (Button)findViewById(R.id.btnEnable);*/


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();



        strEmail = sharedPreferences.getString("email", "");
        //strPassword = sharedPreferences.getString("password","");


        if(!strEmail.isEmpty())
        {
            txtEmail.setText(strEmail);
        }
        /*if (strPassword !="")
        {
            txtPassword.setText(strPassword);
        }*/


        /*if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }*/


       /* btnCam.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
        boolean hasStoragePermission = (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasStoragePermission) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                123);
                        return;

                    }


                }
                Toast toast = Toast.makeText(getApplicationContext(), "Auto Email Feature activated",
                        Toast.LENGTH_LONG);
                toast.show();
            }





        });*/


    /*    btnEnable.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                boolean hasCamPermission = (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasCamPermission) {

                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                11);
                        return;

                    }
                }

                Toast toast = Toast.makeText(getApplicationContext(), "Photo Capture Enabled",
                        Toast.LENGTH_LONG);
                toast.show();

            }





        });*/


        btnSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {





                boolean isValid = false;
              strEmail = txtEmail.getText().toString();
               // strPassword = txtPassword.getText().toString();
                //Log.d("akg", "pswd" + strPassword);

                    if (strEmail.isEmpty() ) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please Enter the Email",
                                Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }/*else if (strPassword.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please Enter the Password",
                        Toast.LENGTH_LONG);
                toast.show();
                return;
            }*/


                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (strEmail.matches(emailPattern))
                {
                    isValid = true;
                }
                else
                {
                    isValid = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid Email",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if( isValid )
                {
                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, 12345);
                    }

                    editor = sharedPreferences.edit();
                    editor.putString("email", strEmail);
                    //editor.putString("password", strPassword);
                    editor.commit();


                }


            }





        });



        ComponentName cn=new ComponentName(this, MyReceiver.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        if (mgr.isAdminActive(cn)) {
            int msgId;

            if (mgr.isActivePasswordSufficient()) {
                msgId=R.string.compliant;
            }
            else {
                msgId=R.string.not_compliant;
            }

            //Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
      /*      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));*/
            startActivity(intent);


        }

        //finish();
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
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);

        }
        else if(id == R.id.action_privacy) {
            Intent intent = new Intent(this, PrivacyActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);

        }
        else if(id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        strEmail = sharedPreferences.getString("email","");
        //strPassword = sharedPreferences.getString("password","");
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }



    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
