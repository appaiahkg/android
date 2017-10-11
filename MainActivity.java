package net.akg.com.instabroadcast;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView txtSms;
    private TextView txtEmail;
    private TextView txtOnlineSms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSms = (TextView)findViewById(R.id.txtSms);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtOnlineSms = (TextView)findViewById(R.id.txtOnlineSms);



        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS
            }, 12345);
        }

        txtEmail.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

            }
        });

        txtSms.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {

                Intent intent = new Intent(MainActivity.this, SmsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });

        txtOnlineSms.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {

                Intent intent = new Intent(MainActivity.this, OnlineSmsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });
    }

    /*@Override
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
    }*/
}
