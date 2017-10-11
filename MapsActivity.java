package net.akg.com.tracker;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private PaginatedScanList<LocationData> result;
    public static PaginatedScanList<UserData> users;
    private SupportMapFragment mapFragment;
    public static ArrayList<User> userList = new ArrayList<User>();
    LatLngBounds bounds;
    int padding = 0;
    Button btnRefresh;
    FloatingActionButton btnUser;
    Spinner spinnerList;
    private String array_spinner[];
    private String selectedIndex = "ALL USERS";
    private static MapsActivity activity;
    public static ConnectivityManager conMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("akg", "create");
        activity = this;

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }


            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }


        }

        //JSONFetchUserTask task = new JSONFetchUserTask();
        //task.execute();




        setContentView(R.layout.activity_maps);
        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        btnUser = (FloatingActionButton)findViewById(R.id.btnUser);
        spinnerList = (Spinner) findViewById(R.id.spinnerList);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JSONFetchLocAsyncTask task = new JSONFetchLocAsyncTask();
                task.execute();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this,
                        UserActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });
        //setUpMapIfNeeded();

    }

    void getUserList()
    {
        userList.clear();
        User user0 = new User();
        user0.setUserId("");
        user0.setUserName("ALL USERS");
        user0.setTelePhone(" ");
        user0.setImageData(" ");
        userList.add(user0);


            for (int i = 0; i < users.size(); i++) {
                User user = new User();
                user.setUserId(users.get(i).getId());
                user.setUserName(users.get(i).getName());
                user.setTelePhone(users.get(i).getContact());
                user.setImageData(users.get(i).getImage());
                user.setPhotoData(users.get(i).getPhoto());
                userList.add(user);
            }


        /*User user1 = new User();
        user1.setUserId("102");
        user1.setUserName("SANTHOSH ALVA");
        user1.setTelePhone("8722279155");
        user1.setImageId(R.drawable.santoshalva);

        User user2 = new User();
        user2.setUserId("103");
        user2.setUserName("RAVI PRASAD");
        user2.setTelePhone("9448779070");
        user2.setImageId(R.drawable.raviprasad);


        User user3 = new User();
        user3.setUserId("104");
        user3.setUserName("SANTOSH RAO");
        user3.setTelePhone("9980465681");
        user3.setImageId(R.drawable.santoshrao);

        User user4 = new User();
        user4.setUserId("105");
        user4.setUserName("MURALI");
        user4.setTelePhone("9008518418");
        user4.setImageId(R.drawable.murali);

        User user5 = new User();
        user5.setUserId("106");
        user5.setUserName("PRAVEEN");
        user5.setTelePhone("9663966719");
        user5.setImageId(R.drawable.praveen);

        User user6 = new User();
        user6.setUserId("108");
        user6.setUserName("SUKESH");
        user6.setTelePhone("7204772893");
        user6.setImageId(R.drawable.sukesh);

        User user7 = new User();
        user7.setUserId("109");
        user7.setUserName("VASANTH");
        user7.setTelePhone("9886112870");
        user7.setImageId(R.drawable.vasanth);

        User user8 = new User();
        user8.setUserId("110");
        user8.setUserName("NISHWITH");
        user8.setTelePhone("7353276293");
        user8.setImageId(R.drawable.nishwith);

        User user9 = new User();
        user9.setUserId("111");
        user9.setUserName("DEEPAK");
        user9.setTelePhone("8147991024");
        user9.setImageId(R.drawable.deepak);

        User user10 = new User();
        user10.setUserId("112");
        user10.setUserName("ANISH");
        user10.setTelePhone("8147686378");
        user10.setImageId(R.drawable.anish);

        User user11 = new User();
        user11.setUserId("113");
        user11.setUserName("RONALD");
        user11.setTelePhone("9986403878");
        user11.setImageId(R.drawable.ronald);

        User user12 = new User();
        user12.setUserId("114");
        user12.setUserName("JAYANAND");
        user12.setTelePhone("9035534068");
        user12.setImageId(R.drawable.jayanandh);

        User user13 = new User();
        user13.setUserId("115");
        user13.setUserName("ASHOKA DEVADIGA");
        user13.setTelePhone("9844885640");
        user13.setImageId(R.drawable.ashokdevadiga);



        User user14 = new User();
        user14.setUserId("116");
        user14.setUserName("AJAY");
        user14.setTelePhone("9632940685");
        user14.setImageId(R.drawable.user);


        User user15 = new User();
        user15.setUserId("117");
        user15.setUserName("RAJSHEKAR");
        user15.setTelePhone("7411735202");
        user15.setImageId(R.drawable.rajshekar);


        User user16 = new User();
        user16.setUserId("118");
        user16.setUserName("CHARAN");
        user16.setTelePhone("8867276112");
        user16.setImageId(R.drawable.user);

        User user17 = new User();
        user17.setUserId("119");
        user17.setUserName("NAVANEETH");
        user17.setTelePhone("9738909320");
        user17.setImageId(R.drawable.navaneeth);


        User user18 = new User();
        user18.setUserId("120");
        user18.setUserName("SANTHOSH HIREMATA");
        user18.setTelePhone("7760064138");
        user18.setImageId(R.drawable.santoshhiremat);

        User user19 = new User();
        user19.setUserId("121");
        user19.setUserName("JAYAKUMAR");
        user19.setTelePhone("8105978962");
        user19.setImageId(R.drawable.jayakumar);

        User user20 = new User();
        user20.setUserId("122");
        user20.setUserName("JAYARAM");
        user20.setTelePhone("9342173611");
        user20.setImageId(R.drawable.jayaram);


        User user21 = new User();
        user21.setUserId("123");
        user21.setUserName("ANIL");
        user21.setTelePhone("9743129935");
        user21.setImageId(R.drawable.user);

        User user22 = new User();
        user22.setUserId("124");
        user22.setUserName("IMRAN");
        user22.setTelePhone("9611683113");
        user22.setImageId(R.drawable.imran);

        User user23 = new User();
        user23.setUserId("125");
        user23.setUserName("SURAJ");
        user23.setTelePhone("8123766739");
        user23.setImageId(R.drawable.suraj);

        User user24 = new User();
        user24.setUserId("127");
        user24.setUserName("IVAN");
        user24.setTelePhone("9611411671");
        user24.setImageId(R.drawable.ivan);

        User user25 = new User();
        user25.setUserId("130");
        user25.setUserName("ADARSH");
        user25.setTelePhone("8123039156");
        user25.setImageId(R.drawable.user);

        User user26 = new User();
        user26.setUserId("131");
        user26.setUserName("DEEPAK 2");
        user26.setTelePhone("7760652797");
        user26.setImageId(R.drawable.user);


        User user27 = new User();
        user27.setUserId("132");
        user27.setUserName("GANGADHAR");
        user27.setTelePhone("9964552822");
        user27.setImageId(R.drawable.gangadhar);

        User user28 = new User();
        user28.setUserId("133");
        user28.setUserName("ROHITHAKSHA");
        user28.setTelePhone("8722871081");
        user28.setImageId(R.drawable.rohithaksha);


        User user29 = new User();
        user29.setUserId("134");
        user29.setUserName("RAJESHWARI");
        user29.setTelePhone("8296844509");
        user29.setImageId(R.drawable.user);


        User user30 = new User();
        user30.setUserId("135");
        user30.setUserName("VASANTH SALIAN");
        user30.setTelePhone("8105302458");
        user30.setImageId(R.drawable.vasanthsalian);

        User user31 = new User();
        user31.setUserId("136");
        user31.setUserName("VINOD");
        user31.setTelePhone("8746939595");
        user31.setImageId(R.drawable.vinodh);

        User user32 = new User();
        user32.setUserId("138");
        user32.setUserName("YAKSHITH");
        user32.setTelePhone("8904826347");
        user32.setImageId(R.drawable.yakshith);

        User user33 = new User();
        user33.setUserId("139");
        user33.setUserName("DARMAPAL");
        user33.setTelePhone("9449332061");
        user33.setImageId(R.drawable.dharampal);

        User user34 = new User();
        user34.setUserId("140");
        user34.setUserName("KAVITHA");
        user34.setTelePhone("8050120197");
        user34.setImageId(R.drawable.kavitha);

        userList.add(user0);
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);

        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userList.add(user9);
        userList.add(user10);

        userList.add(user11);
        userList.add(user12);
        userList.add(user13);
        userList.add(user14);
        userList.add(user15);

        userList.add(user16);
        userList.add(user17);
        userList.add(user18);
        userList.add(user19);
        userList.add(user20);

        userList.add(user21);
        userList.add(user22);
        userList.add(user23);
        userList.add(user24);
        userList.add(user25);

        userList.add(user26);
        userList.add(user27);
        userList.add(user28);
        userList.add(user29);
        userList.add(user30);

        userList.add(user31);
        userList.add(user32);
        userList.add(user33);
        userList.add(user34);*/
        Log.d("akg", "userlist" + userList.size());

        JSONFetchLocAsyncTask task = new JSONFetchLocAsyncTask();
        task.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getUserList();
        Log.d("akg", "resume");

        if (conMgr.getActiveNetworkInfo() == null)
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), "Your Internet seems to be disabled,Please visit device settings to enable it!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            JSONFetchUserTask task = new JSONFetchUserTask();
            task.execute();
        }
        /*int counter = 0;
        array_spinner = null;
        array_spinner = new String[userList.size()+1];
        array_spinner[0] = "        ALL USERS";
        for(int i = 0 ; i < userList.size();i++)
        {
            counter = counter +1;
            array_spinner[counter] = userList.get(i).getUserId()+"      "+userList.get(i).getUserName();
        }
*/
    /*    ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, array_spinner);*/
        /*SpinnerAdapter adapter = new SpinnerAdapter(this,
                R.layout.spinner_layout, R.id.txt, userList);
        spinnerList.setAdapter(adapter);
        spinnerList.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       // Log.d("akg", "spinner" + userList.get(position).getUserId());
                        selectedIndex = userList.get(position).getUserId();
                        setUpMapIfNeeded();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // Another interface callback
                    }


                });

        setUpMapIfNeeded();*/


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            JSONFetchLocAsyncTask task = new JSONFetchLocAsyncTask();
                            task.execute();
                            Log.d("akg","function call");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        //timer.schedule(doAsynchronousTask, 0, 300000); //execute in every 5 minutes
        timer.schedule(doAsynchronousTask, 0, 600000);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        Log.d("akg", "inside setUpMapIfNeeded");
        Log.d("akg", "mMap" + mMap);
        Log.d("akg", "mapFragment" + mapFragment);
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
           // mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            if(mapFragment == null) {
                Log.d("akg", "mMap1"+mMap);
                Log.d("akg", "mapFragment1"+mapFragment);
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

            }
            else
            {
                mapFragment.getMapAsync(this);
            }
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }




    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap map  = googleMap;
        googleMap.clear();
        List<Marker> markers = new ArrayList<Marker>();
        markers.clear();
        BitmapDescriptor icon;
        if(result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
            /*Log.d("akg","id:"+result.get(i).getId().toUpperCase() );
            if(result.get(i).getId().toUpperCase() == "PRAGATHI")
            {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pragathi);
            }
            else if(result.get(i).getId().toUpperCase() == "DIGANTH")
           {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.user);
           } else*/
                //{

                icon = BitmapDescriptorFactory.fromResource(R.drawable.user);
                //}

                String name = "";
                String number = "";
                String imageData;
                Log.d("akg", "id" + result.get(i).getId().toUpperCase());
                for (int j = 1; j < userList.size(); j++) {
                   // Log.d("akg","result id"+result.get(i).getId().toUpperCase());
                    //Log.d("akg","userlist id"+userList.get(j).getUserId().toUpperCase());

                    if (userList.get(j).getUserId().toString().toUpperCase().equals(result.get(i).getId().toString().toUpperCase())) {
                        Log.d("akg","EQUALS");
                        name = userList.get(j).getUserName();
                        number = userList.get(j).getTelePhone();
                        imageData = userList.get(j).getImageData();

                        final byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                        icon = BitmapDescriptorFactory.fromBitmap(getCircleBitmap(bitmap));

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(result.get(i).getLatitude()), Double.parseDouble(result.get(i).getLongitude())))
                                .title("P Code:" + result.get(i).getId())
                                .snippet("Last seen:" + result.get(i).getDate())
                                .icon(icon);

                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                for (int k = 1; k < userList.size(); k++) {
                                    if (marker.getTitle().contains(userList.get(k).getUserId().toString().toUpperCase())) {
                                        Toast.makeText(getApplicationContext(), "Name:" + userList.get(k).getUserName() + "\nNumber:" + userList.get(k).getTelePhone(), Toast.LENGTH_SHORT).show();// display toast
                                    }
                                }


                            }
                        });

                        Marker marker = map.addMarker(markerOptions);
                        Log.d("akg","index"+selectedIndex);
                      /*  String temp[];
                        String index = "";
                        if(!selectedIndex.contains("ALL USERS"))
                        {
                            temp = selectedIndex.split(" ");
                            index = temp[0];
                        }*/

                        Log.d("akg", "marker" + marker.getTitle());
                        if(selectedIndex.isEmpty()) {
                            markers.add(marker);
                        }
                        else if(marker.getTitle().contains(selectedIndex))
                        {
                            markers.add(marker);
                        }

                    }
                }


            }
        }

        Log.d("akg","markers"+markers.size());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(markers.size() > 0) {
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            bounds = builder.build();
            padding = 0;

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                }
            });
        }

        // offset from edges of the map in pixels





    }

    public int getDrawableId(String name){
        try {
            Field fld = R.drawable.class.getField(name);
            return fld.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }




    class JSONFetchLocAsyncTask extends AsyncTask<String, Void, Boolean> {

        private DynamoDBMapper mapper;

        JSONFetchLocAsyncTask()
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
                result = mapper.scan(LocationData.class, scanExpression);

            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result)
        {
            Log.d("akg", "loc fetched");
            setUpMapIfNeeded();
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
                users = mapper.scan(UserData.class, scanExpression);

            }  catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean users)
        {
            Log.d("akg", "user fetched");


                getUserList();

                SpinnerAdapter adapter = new SpinnerAdapter(MapsActivity.activity, R.layout.spinner_layout, R.id.txt, userList);
                spinnerList.setAdapter(adapter);
                spinnerList.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // Log.d("akg", "spinner" + userList.get(position).getUserId());
                                selectedIndex = userList.get(position).getUserId();
                                setUpMapIfNeeded();
                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                // Another interface callback
                            }


                        });

                setUpMapIfNeeded();


        }
    }

    public  static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.WHITE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
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

            String imageData = list.get(position).getPhotoData();
            if(imageData!=null) {
            final byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(MapsActivity.getCircleBitmap(bitmap));
            }


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
}
