package net.akg.com.instabroadcast;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;




public class SettingsActivity extends Activity {
    private Button btnSettingsSave;
    private static SettingsActivity container;

    private Button btnVideo;
    private RadioGroup radioGroup;
    SharedPreferences sharedPreferences;
    private String strAttempt;
    private String strFromEmail;
    private String strPassword;
    private EditText txtSettingsEmail;
    private EditText txtPassword;

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = this;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_settings);

        btnVideo = (Button)findViewById(R.id.btnVideo);
        btnSettingsSave = (Button)findViewById(R.id.btnSettingsSave);
        txtSettingsEmail = (EditText)findViewById(R.id.txtSettingsEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);


        strFromEmail = sharedPreferences.getString("fromemail", "");
        strPassword = sharedPreferences.getString("password","");


        if(!strFromEmail.isEmpty())
        {
            txtSettingsEmail.setText(strFromEmail);
        }
        if (strPassword !="")
        {
            txtPassword.setText(strPassword);
        }


        btnSettingsSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                boolean isValid = false;
                strFromEmail = txtSettingsEmail.getText().toString();
                strPassword =  txtPassword.getText().toString();
                //Log.d("akg", "pswd" + strPassword);

                if (strFromEmail.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Enter the Email",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }else if (strPassword.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please Enter the Password",
                        Toast.LENGTH_LONG);
                toast.show();
                return;
            }


                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (strFromEmail.matches(emailPattern)) {
                    isValid = true;
                } else {
                    isValid = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid Email",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (isValid) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, 12345);
                    }

                    editor = sharedPreferences.edit();
                    editor.putString("fromemail", strFromEmail);
                    editor.putString("password", strPassword);
                    editor.commit();

                    Toast toast = Toast.makeText(getApplicationContext(), "Credentials have been saved Successfully",
                            Toast.LENGTH_LONG);
                    toast.show();


                }


            }


        });



        btnVideo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,VideoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

            }



        });


    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        strFromEmail = sharedPreferences.getString("fromemail","");
        strPassword = sharedPreferences.getString("password","");



    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
