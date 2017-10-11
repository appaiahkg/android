package net.akg.com.tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import java.util.ArrayList;


public class DeleteActivity extends Activity {

    Spinner spinnerList;
    private String selectedIndex;
    Button btnDeleteUser;
    SpinnerAdapter adapter;
    private static DeleteActivity activity;

    public static ArrayList<User> userDeleteList = new ArrayList<User>();


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

        userDeleteList.clear();
        for(int i = 0 ; i < MapsActivity.userList.size();i++)
        {
            if(MapsActivity.userList.get(i).getUserName().contains("ALL USERS"))
            {
                //do nothing
            }
            else
            {
                userDeleteList.add(MapsActivity.userList.get(i));
            }
        }

        setContentView(R.layout.activity_delete);
        btnDeleteUser = (Button)findViewById(R.id.btnDeleteUser);
        spinnerList = (Spinner)findViewById(R.id.spinnerList);


        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (MapsActivity.conMgr.getActiveNetworkInfo() == null)
                {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), "Your Internet seems to be disabled,Please visit device settings to enable it!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {

                    JSONDeleteUserTask task = new JSONDeleteUserTask();
                    task.execute();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
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


    @Override
    protected void onResume() {
        super.onResume();

        adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, userDeleteList);
        spinnerList.setAdapter(adapter);
        spinnerList.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Log.d("akg", "spinner" + userList.get(position).getUserId());
                        selectedIndex = userDeleteList.get(position).getUserId();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // Another interface callback
                    }


                });
    }



    class JSONDeleteUserTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog pdia;

        JSONDeleteUserTask()
        {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(DeleteActivity.activity);
            pdia.setMessage("Data Processing...");
            pdia.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                Log.d("akg", "mapper deleted");
                UserData selectedUser = mapper.load(UserData.class,selectedIndex);
                mapper.delete(selectedUser);

                LocationData locDetails = mapper.load(LocationData.class,selectedIndex);
                mapper.delete(selectedUser);


                JSONFetchUserTask task = new JSONFetchUserTask();
                task.execute();

            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result)
        {
            Log.d("akg","user deleted");
            pdia.dismiss();
            Toast.makeText(getApplicationContext(), "User Deleted Successfully", Toast.LENGTH_SHORT).show();// display toast
        }
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
            final byte[] decodedBytes = Base64.decode(list.get(position).getPhotoData(), Base64.DEFAULT);
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

            userDeleteList.clear();
            for(int i = 0 ; i < MapsActivity.userList.size();i++)
            {
                if(MapsActivity.userList.get(i).getUserName().contains("ALL USERS"))
                {
                    //do nothing
                }
                else
                {
                    userDeleteList.add(MapsActivity.userList.get(i));
                }
            }


            adapter.notifyDataSetChanged();
            spinnerList.invalidate();



        }
    }
}
