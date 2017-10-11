package net.akg.com.instabroadcast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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


public class OnlineSmsActivity extends Activity {

    private Button onlinesmsUpload;
    List<String> list = new ArrayList<String>();
    private Button sendOnlineSmsButton;
    private EditText onlinesmsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_sms);
        onlinesmsUpload = (Button)findViewById(R.id.onlinesmsUpload);
        sendOnlineSmsButton = (Button)findViewById(R.id.sendOnlineSmsButton);
        onlinesmsContent = (EditText)findViewById(R.id.onlinesmsContent);

        onlinesmsUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);

            }
        });

        sendOnlineSmsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                String message = onlinesmsContent.getText().toString();

                for(String number : list) {

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


}
