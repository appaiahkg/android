package net.akg.com.instabroadcast;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;



public class SmsActivity extends Activity {

    private Button smsUpload;
    List<String> list = new ArrayList<String>();
    private Button sendSmsButton;
    private EditText smsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        smsUpload = (Button)findViewById(R.id.smsUpload);
        sendSmsButton = (Button)findViewById(R.id.sendSmsButton);
        smsContent = (EditText)findViewById(R.id.smsContent);

        smsUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);

            }
        });

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SmsManager sms = SmsManager.getDefault();
                String message = smsContent.getText().toString();

                for(String number : list) {
                    sms.sendTextMessage(number, null, message, null, null);
                }

                Toast toast = Toast.makeText(getApplicationContext(), "SMS broadcasted action initiated",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();

            StringBuilder text = new StringBuilder();

            list.clear();
            int counter = 0;
            File file = new File(selectedFileUri.getPath());
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }

                Toast toast = Toast.makeText(getApplicationContext(), ".txt file successfully uploaded",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            catch (Exception e)
            {

            }

            // fetch the Sms Manager





        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sms, menu);
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
