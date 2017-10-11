package net.akg.com.tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddActivity extends Activity {

    Button btnSelect;
    private String imagepath=null;
    ImageView imgAdd;
    private DynamoDBMapper mapper;
    Button btnAddUser;
    EditText addName;
    EditText addPcode;
    EditText addContact;

    private String userName;
    private String userContact;
    private String pcode;
    private String image;
    private String photo;
    private static AddActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_add);
        btnSelect = (Button)findViewById(R.id.btnSelect);
        imgAdd = (ImageView)findViewById(R.id.imgAdd);
        btnAddUser = (Button)findViewById(R.id.btnAddUser);

        addName = (EditText)findViewById(R.id.addName);
        addPcode = (EditText)findViewById(R.id.addPcode);
        addContact = (EditText)findViewById(R.id.addContact);


        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJ7GYY5B4IRFU3IFQ","SHuzs1TAESNowYJUHn2vi7efo/pjEUR4UJfXBe5S");
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
        ddbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        mapper = new DynamoDBMapper(ddbClient);

        btnSelect.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);

            }
        });


        btnAddUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                userName = addName.getText().toString();
                userContact = addContact.getText().toString();
                pcode = addPcode.getText().toString();
                image = imagepath;

                if(userName.isEmpty() || userContact.isEmpty() || pcode.isEmpty() || imagepath == null)
                {
                    Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 60, 60, true);

                    Bitmap photoResized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    ByteArrayOutputStream byteArrayPhotoOutputStream = new ByteArrayOutputStream();
                    photoResized.compress(Bitmap.CompressFormat.PNG, 100, byteArrayPhotoOutputStream);
                    byte[] bytePhotoArray = byteArrayPhotoOutputStream .toByteArray();
                    photo = Base64.encodeToString(bytePhotoArray, Base64.DEFAULT);

                    if (MapsActivity.conMgr.getActiveNetworkInfo() == null)
                    {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "Your Internet seems to be disabled,Please visit device settings to enable it!",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {

                        JSONAddUserTask task = new JSONAddUserTask();
                        task.execute();
                    }
                }

            }
        });

    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath();
            //Uri imagename=data.getData();
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            imgAdd.setImageBitmap(bitmap);
            Log.d("akg", "path" + imagepath);



        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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


    class JSONAddUserTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog pdia;

        JSONAddUserTask()
        {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(AddActivity.activity);
            pdia.setMessage("Data Processing...");
            pdia.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {



                UserData user = new UserData();
                user.setId(pcode);
                user.setName(userName);
                user.setContact(userContact);
                user.setImage(image);
                user.setPhoto(photo);
                mapper.save(user);

                Log.d("akg", "mapper saved");





            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }



        protected void onPostExecute(Boolean result)
        {
            Log.d("akg","user added");
            pdia.dismiss();
            Toast.makeText(getApplicationContext(), "User Added Successfully", Toast.LENGTH_SHORT).show();// display toast
        }
    }


}
