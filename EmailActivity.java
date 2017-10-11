package net.akg.com.instabroadcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class EmailActivity extends Activity {

    ImageView emailSettings;
    Button sendEmail;
    Button attachment;
    List<String> list = new ArrayList<String>();
    Button broadcastEmail;
    SharedPreferences sharedPreferences;
    private String strPassword;
    private String strFromEmail;
    private String subject;
    private EditText txtSubject;
    private EditText bodyEmail;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        emailSettings = (ImageView)findViewById(R.id.emailSettings);
        sendEmail = (Button)findViewById(R.id.sendEmail);
        attachment = (Button)findViewById(R.id.attachment);
        broadcastEmail = (Button)findViewById(R.id.broadcastEmail);
        txtSubject = (EditText)findViewById(R.id.txtSubject);
        bodyEmail = (EditText)findViewById(R.id.bodyEmail);


        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(EmailActivity.this);


        emailSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(EmailActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

            }
        });

        broadcastEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                strPassword = sharedPreferences.getString("password","");
                strFromEmail = sharedPreferences.getString("fromemail","");
                sendEmail(EmailActivity.this, strFromEmail, strPassword);

                Toast toast = Toast.makeText(getApplicationContext(), "Email broadcasted action initiated",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);

            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);

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
        else if(requestCode == 2 && resultCode == RESULT_OK)
        {
            Uri selectedFileUri = data.getData();

            filename = selectedFileUri.getPath();
            Log.d("akg","filename"+filename);
            Toast toast = Toast.makeText(getApplicationContext(), "attachment successfully uploaded",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }





    public void sendEmail(Context ctxt,String strFromEmail,String strPassword)
    {

        EmailAsyncTask task = new EmailAsyncTask(ctxt, strFromEmail, strPassword);
        task.execute();

    }


    class EmailAsyncTask extends AsyncTask<String, Void, Boolean> {

        private Context ctxt = null;
        private String strFromEmail;
        private String strPassword;


        EmailAsyncTask(Context ctxt,String strFromEmail,String strPassword)
        {

            this.ctxt = ctxt;
            this.strFromEmail = strFromEmail;
            this.strPassword = strPassword;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                subject = txtSubject.getText().toString();

                Mail m = new Mail(strFromEmail, strPassword);
                String[] toArr = list.toArray(new String[list.size()]);
                m.setTo(toArr);
                m.setFrom(strFromEmail);
                m.setSubject(subject);
                m.setBody(bodyEmail.getText().toString());
                if(filename != null) {
                    m.addAttachment(filename);
                }

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
        }
    }

}
