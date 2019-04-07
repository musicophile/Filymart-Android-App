package com.example.filymart;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.filymart.helper.SQLiteHandler;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.SphericalUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener,
        LocationListener,
        AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    String ordersum, deliveryC, Maintotal;

    private double tolongitude;
    private double tolatitude;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;


    private double fromLongitude;
    private double fromLatitude;

    //To -> the second coordinate to where we need to calculate the distance
    private double toLongitude;
    private double toLatitude;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    //Our buttons
    private Button buttonCalcDistance, standardBtn, fastBtn;
    private Button proceedPayment, changeAddress;
    private TextView standardPrice, fastDeliveryTime, busketTotal, DeliveryTotal, Total;
    private TextView fastPrice, AddressIfo, DistanceInfo;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String deliverytime, addressInfomation, distanceInformation;
    private ProgressDialog pDialog;
    String[] bankNames={"07:30am - 11:30am","02:00pm-07:00pm"};

    private RecyclerView recyclerView;
    private OrderSummaryAdapter adapter;
    private List<OrderSummary> orderSummaryList;
    private double distanceKm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Spinner spin =  findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        recyclerView = findViewById(R.id.recyclerViewOrderSummary);
        LinearLayoutManager linearVertical = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearVertical);

        orderSummaryList = new ArrayList<>();
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
                // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();


        buttonCalcDistance =  findViewById(R.id.buttonCalcDistance);
        proceedPayment = findViewById(R.id.proceed_payment);
        standardPrice = findViewById(R.id.tvSPrice);
        fastPrice = findViewById(R.id.tvFastPrice);
        standardBtn = findViewById(R.id.standardBtn);
        fastBtn = findViewById(R.id.fastBtn);
        fastDeliveryTime = findViewById(R.id.tvFastDeliveryTime);
        busketTotal = findViewById(R.id.totalBusket);
        DeliveryTotal = findViewById(R.id.tvDeliveryCost);
        Total = findViewById(R.id.tvTotal);
        changeAddress = findViewById(R.id.btnChangeAddress);
        AddressIfo = findViewById(R.id.addressInfo);
        DistanceInfo = findViewById(R.id.mapInfo);


        buttonCalcDistance.setOnClickListener(this);
        proceedPayment.setOnClickListener(this);

        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, CheckAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });

        standardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    new StandardDelivery().execute();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        fastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    new FastDelivery().execute();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });


        if (isNetworkAvailable()){
            new Ordersum().execute();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }

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
    protected void onResume() {
        googleApiClient.connect();
        super.onResume();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.filymart/http/host/path")
        );
        AppIndex.AppIndexApi.start(googleApiClient, viewAction);
    }



    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
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

    @Override
    protected void onPause() {
        googleApiClient.disconnect();
        super.onPause();


            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "Maps Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://com.example.filymart/http/host/path")
            );
            AppIndex.AppIndexApi.end(googleApiClient, viewAction);
    }

    //Function to move the map
    private void moveMap() {
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
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyCgAwjdCJ_g5LKcDUPbw9lOKMqyRDYpvbY");
        return urlString.toString();
    }

    private void getDirection(){

        double fro = -3.3727086922955514;
        double frot = 36.69473072275389;


        fromLatitude = fro;
        fromLongitude = frot;

        double too = tolatitude;
        double tot = tolongitude;
        toLatitude = too;
        toLongitude = tot;
        //Getting the URL
        String url = makeURL(fromLatitude, fromLongitude, toLatitude, toLongitude);

        //Showing a dialog till we get the route
        ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deliveryBack(View view) {
        Intent intent = new Intent(getApplicationContext(), CheckAddressActivity.class);
        startActivity(intent);


    }

    //The parameter is the server response
    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from = new LatLng(fromLatitude,fromLongitude);
        LatLng to = new LatLng(toLatitude,toLongitude);

        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(from, to);

        mMap.clear();
        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(from)
                .draggable(true));

        mMap.addMarker(new MarkerOptions()
                .position(to)
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);

            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject realdistance = steps.getJSONObject("distance");
             distanceKm = Double.parseDouble(realdistance.getString("value"));
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(8)
                    .color(Color.BLUE)
                    .geodesic(true)
            );


        }
        catch (JSONException e) {

        }


        Double distanceKmm = distanceKm / 1000;
        Double StandadP = distanceKmm * 600;
        Double fastP = distanceKmm * 600 * 2;
        standardPrice.setText(new DecimalFormat("##.##").format(StandadP));
        fastPrice.setText(new DecimalFormat("##.##").format(fastP));
        DistanceInfo.setText("Delivery cost is charged per 1Km is Tsh.600 and the distance from your address location to markert is "+distanceKmm+"Km");

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();
        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));

        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {


        if(v == buttonCalcDistance){
            getDirection();
        }

        if(v == proceedPayment){

            Intent intent = new Intent(getApplicationContext(), SignatureActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deliverytime = bankNames[position];
        Toast.makeText(getApplicationContext(), deliverytime, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class Ordersum extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Delivery Methods..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            Intent intent = getIntent();
            String addressId = intent.getStringExtra("addressId");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("address_id", addressId));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/showMobileorderSummary";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                ordersum = json.getString("ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                tolatitude = json.getDouble("AddressLat");
                tolongitude = json.getDouble("AddressLng");
                addressInfomation = json.getString("AddressName");
                distanceInformation = json.getString("AddressForDelivery");
                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    orderSummaryList.add(new OrderSummary(
                            product.getString("id"),
                            product.getString("product_name"),
                            product.getString("total")
                    ));


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
            adapter = new OrderSummaryAdapter(MapsActivity.this, orderSummaryList);
            recyclerView.setAdapter(adapter);
            busketTotal.setText(ordersum);
            DeliveryTotal.setText(deliveryC);
            Total.setText(Maintotal);
            AddressIfo.setText("Your delivery address is " +addressInfomation+" and its location is at "+distanceInformation+" as shown below on a map. If this is not your address you want your order to be delivered you may Change address by click the button below to Change Address");

            if (Maintotal.equals("Null")){
            proceedPayment.setVisibility(View.INVISIBLE);
            }else{
                proceedPayment.setVisibility(View.VISIBLE);
            }
            getDirection();
        }

    }

    class StandardDelivery extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Standard Delivery Method..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");


            // Building Parameters
            String delivery = "1";
            String name = "Standard Delivery";
            String cost = standardPrice.getText().toString();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("delivery", delivery));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("deliverytime", deliverytime));
            params.add(new BasicNameValuePair("cost", cost));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_STANDARD = "http://www.filymart.com/deliverymobilestandard";
            JSONObject json = jsonParser.makeHttpRequest(URL_STANDARD,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                ordersum = json.getString("Ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                JSONObject o = json.getJSONObject("order");




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
            DeliveryTotal.setText(deliveryC);
            Total.setText(Maintotal);
            proceedPayment.setVisibility(View.VISIBLE);
            getDirection();

        }

    }

    class FastDelivery extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Fast Delivery Method..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            String delivery = "2";
            String name = "Fast Delivery";
            String fastTime = fastDeliveryTime.getText().toString().trim();
            String cost = fastPrice.getText().toString().trim();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("delivery", delivery));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("deliverytime", fastTime));
            params.add(new BasicNameValuePair("cost", cost));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_FAST = "http://www.filymart.com/deliverymobilestandard";
            JSONObject json = jsonParser.makeHttpRequest(URL_FAST,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                ordersum = json.getString("Ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                JSONObject o = json.getJSONObject("order");


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
            DeliveryTotal.setText(deliveryC);
            Total.setText(Maintotal);
            proceedPayment.setVisibility(View.VISIBLE);
            getDirection();

        }

    }
}
