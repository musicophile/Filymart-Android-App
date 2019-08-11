package com.mart.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.CheckAddressContent.CheckAddressActivity;
import com.mart.filymart.DeliveryInformationContent.DeliveryInformationActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewAddressActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener{


    private GoogleMap mMap;
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    private String name;
    private Marker m;

    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;
    public static final String TAG = NewAddressActivity.class.getSimpleName();
    private TextView firstName, secondName, phoneNumber,
            appartmentName, addressName, streetDetails, location, mlatitude, mlongitude;
    private Button createBtn;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String Id;
    private NewAddressProgressDialog pd;
    private Handler h;
    private Runnable r;
    ScrollView scrollView;
    ImageView transparentImageView;
    String[] cityNames={"Select your City", "Arusha","Moshi", "Daressalaam", "Other"};
    String cityName;
    String warn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);


        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        Spinner spin =  findViewById(R.id.city_name);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,cityNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        mlatitude = findViewById(R.id.lat);
        mlongitude = findViewById(R.id.lng);
        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        phoneNumber = findViewById(R.id.phonenumber);
        addressName = findViewById(R.id.address_name);
        location = findViewById(R.id.location);
        createBtn = findViewById(R.id.create);
        scrollView =  findViewById(R.id.scrollView);
         transparentImageView = findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());

        h = new Handler();
        pd = new NewAddressProgressDialog(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    String adfirstname = firstName.getText().toString().trim();
                    String adsecondname = secondName.getText().toString().trim();
                    String adphone = phoneNumber.getText().toString().trim();
                    String adadrress = addressName.getText().toString().trim();
                    String adlocation = location.getText().toString().trim();
                    if(adfirstname.isEmpty() || adsecondname.isEmpty() || adphone.isEmpty() || adadrress.isEmpty() || adlocation.isEmpty() || cityName.isEmpty()){
                        Toast.makeText(NewAddressActivity.this, "Please fill all the required credentials", Toast.LENGTH_LONG).show();
                    }else{
                        new PostAddressInfo().execute();
                    }


                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
              super.onStop();
    }

    private void getCurrentLocation() {

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            moveMap();
        }
    }

    private void moveMap() {

        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;


        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Displaying current coordinates in toast


        String lat= Double.toString(latitude);
        String lng= Double.toString(longitude);
        mlatitude.setText (lat);
        mlongitude.setText(lng);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void map(View view) {
        Intent intent = new Intent(this, DeliveryInformationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
      //  if(v == buttonCurrent){
        //    getCurrentLocation();
         //   moveMap();
       // }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        //Adding marker to that coordinate
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //Setting onMarkerDragListener to track the marker drag
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerDragListener(this);
        //Adding a long click listener to the map
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        latitude = latLng.latitude;
        longitude = latLng.longitude;
        String msg = latitude + ", "+longitude;
        String lat= Double.toString(latitude);
        String lng= Double.toString(longitude);
        mlatitude.setText (lat);
        mlongitude.setText(lng);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }



    @Override
    public void onMarkerDragStart(Marker marker) {

        mMap.clear();
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        mMap.clear();
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        mMap.clear();
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();

    }

    public void addressBack(View view) {
        Intent intent1 = new Intent(getApplicationContext(), CheckAddressActivity.class);
      //  intent1.putExtra("fragment",2);
        startActivity(intent1);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        latitude = latLng.latitude;
        longitude = latLng.longitude;

        String msg = latitude + ", "+longitude;
        String lat= Double.toString(latitude);
        String lng= Double.toString(longitude);
        mlatitude.setText (lat);
        mlongitude.setText(lng);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        warn = cityNames[position];
        if(warn.equals("Select your City")){
            Toast.makeText(this, "Please select Your City", Toast.LENGTH_LONG).show();
        }else{
            cityName = cityNames[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class PostAddressInfo extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
            h.postDelayed(r,5000);
        }


        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();



            String adfirstname = firstName.getText().toString().trim();
            String adsecondname = secondName.getText().toString().trim();
            String adphone = phoneNumber.getText().toString().trim();
            String adadrress = addressName.getText().toString().trim();
            String adlocation = location.getText().toString().trim();
            String adlatitude = mlatitude.getText().toString().trim();
            String adlongitude = mlongitude.getText().toString().trim();
            String user_id = users.get("uid");

           // $table->string('country_code', 4)->nullable();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first_name", adfirstname));
            params.add(new BasicNameValuePair("last_name", adsecondname));
            params.add(new BasicNameValuePair("phone", adphone));
            params.add(new BasicNameValuePair("address_name", adadrress));
            params.add(new BasicNameValuePair("location", adlocation));
            params.add(new BasicNameValuePair("city_name", cityName));
            params.add(new BasicNameValuePair("lat", adlatitude));
            params.add(new BasicNameValuePair("lng", adlongitude));
            params.add(new BasicNameValuePair("user_id", user_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_ADDRESS,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {


                    Intent intent = new Intent(getApplicationContext(), DeliveryInformationActivity.class);
                    intent.putExtra("addressId", json.getString("addressid"));
                    startActivity(intent);
                       finish();
                }
                if (success == 2) {
                    final String errorMsg = json.getString("error_msg");
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            h.removeCallbacks(r);
            if (pd.isShowing() ) {
                pd.dismiss();
            }
        }

    }

}
