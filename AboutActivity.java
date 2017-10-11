package net.akg.com.deviceunlockdetection;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.VideoView;
import android.content.Intent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AboutActivity extends Activity {
    private AdView mAdAboutView;
    //private VideoView videoview;
    //private Button btnVideo;
    private static AboutActivity container;
/*    private RadioButton radioButton;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioGroup radioGroup;*/
    SharedPreferences sharedPreferences;
    private String strAttempt;

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = this;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_about);
        mAdAboutView = (AdView) findViewById(R.id.adAboutView);
/*        radioButton = (RadioButton)findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);*/
        //btnVideo = (Button) findViewById(R.id.btnVideo);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdAboutView.loadAd(adRequest);



/*        radioButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Log.d("akg", "1");
                if(radioButton.isChecked())
                {
                    editor = sharedPreferences.edit();
                    editor.putString("attempt", "1");
                    editor.commit();
                }

            }


        });
        radioButton2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(radioButton2.isChecked())
                {
                    editor = sharedPreferences.edit();
                    editor.putString("attempt","2");
                    editor.commit();
                }

            }


        });
        radioButton3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(radioButton3.isChecked())
                {
                    editor = sharedPreferences.edit();
                    editor.putString("attempt","3");
                    editor.commit();
                }

            }


        });*/

        /*btnVideo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this,VideoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

            }



        });*/

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }*/

/*    @Override
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
    }*/
    }

    @Override
    public void onPause() {
        if (mAdAboutView != null) {
            mAdAboutView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdAboutView != null) {
            mAdAboutView.resume();
        }

        /*strAttempt = sharedPreferences.getString("attempt","1");
        Log.d("akg","attempt*"+strAttempt);
        if(!strAttempt.isEmpty())
        {
            if(strAttempt.equalsIgnoreCase("1"))
            {
                Log.d("akg","radioButton"+strAttempt);
                radioButton.setChecked(true);
                MyReceiver.counter = 0;
            }
            else if(strAttempt.equalsIgnoreCase("2"))
            {
                Log.d("akg","radioButton2"+strAttempt);
                radioButton2.setChecked(true);
                MyReceiver.counter = 0;
            }
            else if(strAttempt.equalsIgnoreCase("3"))
            {
                Log.d("akg","radioButton3"+strAttempt);

                radioButton3.setChecked(true);
                MyReceiver.counter = 0;
            }

        }*/


    }

    @Override
    public void onDestroy() {
        if (mAdAboutView != null) {
            mAdAboutView.destroy();
        }
        super.onDestroy();
    }
}