package net.akg.com.tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class EditActivity extends Activity {
    Button btnEditSelect;
    private String imagepath=null;
    ImageView imgEdit;
    Spinner spinnerList;
    EditText editName;
    EditText editContact;
    private String imageData =  null;
    private String photoData = null;
    Button btnUpdateUser;
    SpinnerAdapter adapter;
    private static EditActivity activity;

    public static ArrayList<User> userEditList = new ArrayList<User>();



    private String selectedIndex;
    String updatedName;
    String updatedContact;
    String updatedImage;
    String updatedPhoto;

    private DynamoDBMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJ7GYY5B4IRFU3IFQ","SHuzs1TAESNowYJUHn2vi7efo/pjEUR4UJfXBe5S");
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
        ddbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        mapper = new DynamoDBMapper(ddbClient);

        userEditList.clear();
        for(int i = 0 ; i < MapsActivity.userList.size();i++)
        {
            if(MapsActivity.userList.get(i).getUserName().contains("ALL USERS"))
            {
                //do nothing
            }
            else
            {
                userEditList.add(MapsActivity.userList.get(i));
            }
        }



        setContentView(R.layout.activity_edit);

        btnEditSelect = (Button)findViewById(R.id.btnEditSelect);
        btnUpdateUser = (Button)findViewById(R.id.btnUpdateUser);
        imgEdit = (ImageView)findViewById(R.id.imgEdit);
        spinnerList = (Spinner)findViewById(R.id.spinnerList);
        editName = (EditText)findViewById(R.id.editName);
        editContact = (EditText)findViewById(R.id.editContact);




        btnEditSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);

            }
        });

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                 updatedName = editName.getText().toString();
                 updatedContact = editContact.getText().toString();
                if(imagepath != null )
                {
                    Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
                    Bitmap photoResized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();

                    updatedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);


                    ByteArrayOutputStream bytePhotoArrayOutputStream = new ByteArrayOutputStream();
                    photoResized.compress(Bitmap.CompressFormat.PNG, 100, bytePhotoArrayOutputStream);
                    byte[] bytePhotoArray = bytePhotoArrayOutputStream .toByteArray();
                    updatedPhoto = Base64.encodeToString(bytePhotoArray, Base64.DEFAULT);
                }
                else
                {
                    updatedImage = imageData;
                    updatedPhoto = photoData;
                }
                if (MapsActivity.conMgr.getActiveNetworkInfo() == null)
                {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), "Your Internet seems to be disabled,Please visit device settings to enable it!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    JSONUpdateUserTask task = new JSONUpdateUserTask();
                    task.execute();
                }


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(imagepath == null) {
            adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, userEditList);
            spinnerList.setAdapter(adapter);
            spinnerList.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Log.d("akg", "spinner" + userList.get(position).getUserId());
                            selectedIndex = userEditList.get(position).getUserId();
                            editName.setText(userEditList.get(position).getUserName().toUpperCase());
                            editContact.setText(userEditList.get(position).getTelePhone());
                            photoData = userEditList.get(position).getPhotoData();
                            final byte[] decodedBytes = Base64.decode(photoData, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            imgEdit.setImageBitmap(bitmap);
                            imagepath = null;

                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            // Another interface callback
                        }


                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath();
            //Uri imagename=data.getData();
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            imgEdit.setImageBitmap(bitmap);
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
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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

    public class SpinnerAdapter extends ArrayAdapter<User> {
        int groupid;
        Activity context;
        ArrayList<User> list;
        LayoutInflater inflater;
        public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<User>
                list){
            super(context, id, list);
            this.list=list;
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groupid=groupid;
        }

        public View getView(int position, View convertView, ViewGroup parent ){
            View itemView=inflater.inflate(groupid,parent,false);
            ImageView imageView=(ImageView)itemView.findViewById(R.id.img);
            imageData = list.get(position).getImageData();
            photoData = list.get(position).getPhotoData();
            final byte[] decodedBytes = Base64.decode(photoData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(MapsActivity.getCircleBitmap(bitmap));

            TextView textView=(TextView)itemView.findViewById(R.id.txt);
            textView.setText(list.get(position).getUserName().toUpperCase());
            TextView textCode=(TextView)itemView.findViewById(R.id.pcode);
            TextView textContact=(TextView)itemView.findViewById(R.id.txtContact);
            if(list.get(position).getUserId() != "") {
                textCode.setText("PCODE: " + list.get(position).getUserId());
                textContact.setText("CONTACT: " + list.get(position).getTelePhone());
            }
            else
            {
                textCode.setText("");
                textContact.setText("");
            }
            return itemView;
        }

        public View getDropDownView(int position, View convertView, ViewGroup
                parent){
            return getView(position,convertView,parent);

        }
    }


    class JSONUpdateUserTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog pdia;

        JSONUpdateUserTask()
        {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(EditActivity.activity);
            pdia.setMessage("Data Processing...");
            pdia.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                UserData selectedUser = mapper.load(UserData.class,selectedIndex);
                selectedUser.setContact(updatedContact);
                selectedUser.setName(updatedName);
                selectedUser.setImage(updatedImage);
                selectedUser.setPhoto(updatedPhoto);
                mapper.save(selectedUser);
                imagepath = null;
                Log.d("akg", "mapper saved");
            }  catch (Exception e) {

                e.printStackTrace();
            }

            JSONFetchUserTask task = new JSONFetchUserTask();
            task.execute();
            return false;
        }

        protected void onPostExecute(Boolean result)
        {
            Log.d("akg","user edited");
            pdia.dismiss();
            Toast.makeText(getApplicationContext(), "User Data Updated Successfully", Toast.LENGTH_SHORT).show();// display toast
        }
    }


    class JSONFetchUserTask extends AsyncTask<String, Void, Boolean> {

        private DynamoDBMapper mapper;

        JSONFetchUserTask()
        {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {


                BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJ7GYY5B4IRFU3IFQ","SHuzs1TAESNowYJUHn2vi7efo/pjEUR4UJfXBe5S");
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
                ddbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
                mapper = new DynamoDBMapper(ddbClient);

                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                MapsActivity.users = mapper.scan(UserData.class, scanExpression);

            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean users)
        {
            Log.d("akg", "user fetched");
            MapsActivity.userList.clear();
            User user0 = new User();
            user0.setUserId("");
            user0.setUserName("ALL USERS");
            user0.setTelePhone(" ");
            user0.setImageData(" ");
            MapsActivity.userList.add(user0);


            for (int i = 0; i <  MapsActivity.users.size(); i++) {
                User user = new User();
                user.setUserId( MapsActivity.users.get(i).getId());
                user.setUserName( MapsActivity.users.get(i).getName());
                user.setTelePhone( MapsActivity.users.get(i).getContact());
                user.setImageData( MapsActivity.users.get(i).getImage());
                user.setPhotoData( MapsActivity.users.get(i).getPhoto());
                MapsActivity.userList.add(user);
            }

            userEditList.clear();
            for(int i = 0 ; i < MapsActivity.userList.size();i++)
            {
                if(MapsActivity.userList.get(i).getUserName().contains("ALL USERS"))
                {
                    //do nothing
                }
                else
                {
                    userEditList.add(MapsActivity.userList.get(i));
                }
            }


            adapter.notifyDataSetChanged();
            spinnerList.invalidate();


        }
    }
}
