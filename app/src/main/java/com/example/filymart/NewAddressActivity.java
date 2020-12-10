package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filymart.activity.HomeActivity;
import com.example.filymart.app.AppConfig;
import com.example.filymart.fragment.BusketsFragment;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;
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
import com.mart.filymart.R;

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
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {


    private GoogleMap mMap;
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    private Marker m;

    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;
    public static final String TAG = NewAddressActivity.class.getSimpleName();
    private TextView firstName, secondName, phoneNumber,
            appartmentName, cityName, addressName, streetDetails, location, mlatitude, mlongitude;
    private Button createBtn;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String Id;


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



        mlatitude = findViewById(R.id.lat);
        mlongitude = findViewById(R.id.lng);
        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        phoneNumber = findViewById(R.id.phonenumber);
        appartmentName = findViewById(R.id.appartment_name);
        addressName = findViewById(R.id.address_name);
        streetDetails = findViewById(R.id.street);
        location = findViewById(R.id.location);
        cityName = findViewById(R.id.city_name);
        createBtn = findViewById(R.id.create);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostAddressInfo().execute();
            }
        });
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

    //Getting current location
    private void getCurrentLocation() {
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();


            //moving the map to location
            moveMap();
        }
    }

    //Function to move the map
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

        //Displaying current coordinates in toast


        String lat= Double.toString(latitude);
        String lng= Double.toString(longitude);
        mlatitude.setText (lat);
        mlongitude.setText(lng);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void map(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
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
        Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
        intent1.putExtra("fragment",2);
        startActivity(intent1);
    }

    class PostAddressInfo extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewAddressActivity.this);
            pDialog.setMessage("Creating Address..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();


            //String email = user.get("email");

            // Displaying the user details on the screen
            // txtName.setText(name);
            // txtEmail.setText(email);

            String adfirstname = firstName.getText().toString().trim();
            String adsecondname = secondName.getText().toString().trim();
            String adphone = phoneNumber.getText().toString().trim();
            String adadrress = addressName.getText().toString().trim();
            String adstreet = streetDetails.getText().toString().trim();
            String adlocation = location.getText().toString().trim();
            String adlatitude = mlatitude.getText().toString().trim();
            String adlongitude = mlongitude.getText().toString().trim();
            String adappname = appartmentName.getText().toString().trim();
            String addcityname = cityName.getText().toString().trim();
            String user_id = users.get("uid");

           // $table->string('country_code', 4)->nullable();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first_name", adfirstname));
            params.add(new BasicNameValuePair("last_name", adsecondname));
            params.add(new BasicNameValuePair("phone", adphone));
            params.add(new BasicNameValuePair("address_name", adadrress));
            params.add(new BasicNameValuePair("street_details", adstreet));
            params.add(new BasicNameValuePair("location", adlocation));
            params.add(new BasicNameValuePair("city_name", addcityname));
            params.add(new BasicNameValuePair("lat", adlatitude));
            params.add(new BasicNameValuePair("lng", adlongitude));
            params.add(new BasicNameValuePair("apartment_name", adappname));
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
                    // successfully created product


                    // Now store the user in SQLite
                   /* JSONObject user = json.getJSONObject("user");
                    String uid = user.getString("id");
                    String name = user.getString("name");
                    String emaill = user.getString("email");
                    String created_at = user
                            .getString("created_at");

                    // Inserting row in users table
                    db.addUser(name, emaill, uid, created_at);

/*/
                    final String errorMsg = json.getString("msg");
                    // successfully created product
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    // closing this screen
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
            pDialog.dismiss();
        }

    }

}
