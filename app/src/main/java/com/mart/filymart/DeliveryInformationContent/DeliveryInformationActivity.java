package com.mart.filymart.DeliveryInformationContent;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.mart.filymart.CheckAddressContent.CheckAddressActivity;
import com.mart.filymart.DeliveryInformationContent.model.OrderSummaryModel;
import com.mart.filymart.DeliveryInformationContent.presenter.DeliveryInformationPresenter;
import com.mart.filymart.DeliveryInformationContent.presenter.IDeliveryInformationPresenter;
import com.mart.filymart.DeliveryInformationContent.view.IDeliveryInformationView;
import com.mart.filymart.JSONParser;
import com.mart.filymart.OrderSummary;
import com.mart.filymart.R;
import com.mart.filymart.helper.SQLiteHandler;
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
import android.widget.ImageView;
import android.widget.ScrollView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class DeliveryInformationActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener,
        LocationListener,
        AdapterView.OnItemSelectedListener,
        IDeliveryInformationView {

    private GoogleMap mMap;
    String ordersum, deliveryC, Maintotal;

    private double tolongitude;
    private double tolatitude;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    private double olat, olng;


    private double fromLongitude;
    private double fromLatitude;

    //To -> the second coordinate to where we need to calculate the distance
    private double toLongitude;
    private double toLatitude, otherlat, otherlng;
    ScrollView scrollView;
    ImageView transparentImageView;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    //Our buttons
    private Button buttonCalcDistance, standardBtn, fastBtn, weekBtn;
    private Button proceedPayment, changeAddress;
    private TextView standardPrice, fastDeliveryTime, busketTotal, DeliveryTotal, Total;
    private TextView fastPrice, AddressIfo, DistanceInfo, weekPrice, weekDeliverytime;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String deliverytime, addressInfomation, distanceInformation;
    private ProgressDialog pDialog;
    String[] bankNames={"07:30am - 11:30am","02:00pm-07:00pm"};

    private RecyclerView recyclerView;
    private OrderSummaryAdapter adapter;
    private List<OrderSummary> orderSummaryList;
    private double distanceKm;
    private IDeliveryInformationPresenter iDeliveryInformationPresenter;
    private TransparentProgressDeliveryInfo pd;
    private Handler h;
    private Runnable r;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Spinner spin =  findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        weekBtn = findViewById(R.id.weekBtn);
        weekPrice = findViewById(R.id.tvWeekPrice);
        weekDeliverytime = findViewById(R.id.tvWeekDeliveryTime);


        buttonCalcDistance.setOnClickListener(this);
        proceedPayment.setOnClickListener(this);

        scrollView =  findViewById(R.id.scrollDeliveryView);
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


        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryInformationActivity.this, CheckAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        Intent intent = getIntent();
        String addressId = intent.getStringExtra("addressId");


        standardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    String delivery = "1";
                    String name = "Standard Delivery";
                    String cost = standardPrice.getText().toString();
                    pd.show();
                    h.postDelayed(r,5000);
                    iDeliveryInformationPresenter.loadDataStandard(user_id, delivery, name, cost, deliverytime );
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
                    String delivery = "2";
                    String name = "Fast Delivery";
                    String fastTime = fastDeliveryTime.getText().toString().trim();
                    String cost = fastPrice.getText().toString().trim();
                    pd.show();
                    h.postDelayed(r,5000);
                    iDeliveryInformationPresenter.loadDataFast(user_id, delivery, name, cost, fastTime );
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        weekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    String delivery = "3";
                    String name = "Weekly Delivery";
                    String weekTime = weekDeliverytime.getText().toString().trim();
                    String cost = weekPrice.getText().toString().trim();
                    pd.show();
                    h.postDelayed(r,5000);
                    iDeliveryInformationPresenter.loadDataWeek(user_id, delivery, name, cost, weekTime );
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        h = new Handler();
        pd = new TransparentProgressDeliveryInfo(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };


        iDeliveryInformationPresenter = new DeliveryInformationPresenter(this);
        if (isNetworkAvailable()){
            pd.show();
            h.postDelayed(r,5000);

            iDeliveryInformationPresenter.loadData(user_id, addressId);

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
                Uri.parse("android-app://com.mart.filymart/http/host/path")
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
                    Uri.parse("android-app://com.mart.filymart/http/host/path")
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
        urlString.append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyCgAwjdCJ_g5LKcDUPbw9lOKMqyRDYpvbY");
        return urlString.toString();
    }

    private void getLocation(String city, double tolat, double tolong){
        double too = tolatitude;
        double tot = tolongitude;
        toLatitude = too;
        toLongitude = tot;
        ProgressDialog loading = ProgressDialog.show(this,
                "Getting Route",
                "Please wait...",
                false,
                false);


        mMap.clear();
        LatLng loca = new LatLng(toLatitude,toLongitude);

        mMap.addMarker(new MarkerOptions()
                .position(loca)
                .draggable(true)
                .title("Current Location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loca));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        loading.dismiss();
    }

    private void getDirection(){

        double fro = olat;
        double frot = olng;


        fromLatitude = fro;
        fromLongitude = frot;

        double too = tolatitude;
        double tot = tolongitude;
        toLatitude = too;
        toLongitude = tot;
        //Getting the URL
        String url = makeURL(fromLatitude, fromLongitude, toLatitude, toLongitude);
        ProgressDialog loading = ProgressDialog.show(this,
                    "Getting Route",
                    "Please wait...",
                    false,
                    false);

            StringRequest stringRequest = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
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

    public void drawPath(String  result) {

        LatLng from = new LatLng(fromLatitude,fromLongitude);
        LatLng to = new LatLng(toLatitude,toLongitude);

        Double distance = SphericalUtil.computeDistanceBetween(from, to);

        mMap.clear();
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

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


    }

    @Override
    public void onClick(View v) {


        if(v == buttonCalcDistance){
            getDirection();
        }

        if(v == proceedPayment){
             HashMap<String, String> users = db.getUserDetails();
            String user_id = users.get("uid");
            String user_email = users.get("email");
            String user_name = users.get("name");
            String user_phone = users.get("phone");
            BigDecimal d = new BigDecimal(Total.getText().toString().trim());
            BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP);
            String dd = String.valueOf(d2);

                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.scheme("http").authority("www.filymart.com").path("/o-payment.php");
                    uriBuilder.appendQueryParameter("amount", dd);

                    uriBuilder.appendQueryParameter("description", "Order Payment");
                    uriBuilder.appendQueryParameter("type", "MERCHANT");
                    uriBuilder.appendQueryParameter("reference", "001");
                    uriBuilder.appendQueryParameter("first_name", user_name);
                    uriBuilder.appendQueryParameter("last_name", "");
                    uriBuilder.appendQueryParameter("email", user_email);
                    Uri payPalUri = uriBuilder.build();

                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
                    startActivity(viewIntent);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deliverytime = bankNames[position];
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

    @Override
    public void loadDatas(ArrayList<OrderSummaryModel> info, String ordersum, String deliveryC, String Maintotal,
                          String addressInfomation, String distanceInformation, Double tolat, Double tolong,
                          String city, Double olat, Double olng) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        this.olat = olat;
        this.olng = olng;
        this.tolatitude = tolat;
        this.tolongitude = tolong;
        this.otherlat = tolat;
        this.otherlng = tolong;
        this.city = city;
        adapter = new OrderSummaryAdapter(DeliveryInformationActivity.this, info);
        recyclerView.setAdapter(adapter);
        busketTotal.setText(ordersum);
        DeliveryTotal.setText(deliveryC);
        Total.setText(Maintotal);
        AddressIfo.setText("Your delivery address is " + addressInfomation +" and its location is at "+ distanceInformation+" as shown below on a map. If this is not your address you want your order to be delivered you may Change address by click the button below to Change Address");

        if (Maintotal.equals("Null")){
            proceedPayment.setVisibility(View.INVISIBLE);
        }else{
            proceedPayment.setVisibility(View.VISIBLE);
        }
       if(Objects.equals(city, "Other")){
           standardBtn.setVisibility(View.INVISIBLE);
           fastBtn.setVisibility(View.INVISIBLE);
           buttonCalcDistance.setVisibility(View.INVISIBLE);
           getLocation(city, tolat, tolong);
       }else{
           standardBtn.setVisibility(View.VISIBLE);
           fastBtn.setVisibility(View.VISIBLE);
           buttonCalcDistance.setVisibility(View.VISIBLE);
           getDirection();
       }

    }

    @Override
    public void StandardDelivery(String deliveryC, String Maintotal, String ordersum) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        DeliveryTotal.setText(deliveryC);
        Total.setText(Maintotal);
        if (ordersum.equals("0")){
        proceedPayment.setVisibility(View.INVISIBLE);
        }else{
            proceedPayment.setVisibility(View.VISIBLE);
        }
        if(Objects.equals(city, "Other")){
            standardBtn.setVisibility(View.INVISIBLE);
            fastBtn.setVisibility(View.INVISIBLE);
            buttonCalcDistance.setVisibility(View.INVISIBLE);
           // getLocation(city, tolatitude, tolongitude);
        }else{
            standardBtn.setVisibility(View.VISIBLE);
            fastBtn.setVisibility(View.VISIBLE);
            buttonCalcDistance.setVisibility(View.VISIBLE);
            getDirection();
        }
    }

    @Override
    public void FastDelivery(String deliveryC, String Maintotal, String ordersum) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        DeliveryTotal.setText(deliveryC);
        Total.setText(Maintotal);
        if (ordersum.equals("0")){
            proceedPayment.setVisibility(View.INVISIBLE);
        }else{
            proceedPayment.setVisibility(View.VISIBLE);
        }
        if(Objects.equals(city, "Other")){
            standardBtn.setVisibility(View.INVISIBLE);
            fastBtn.setVisibility(View.INVISIBLE);
            buttonCalcDistance.setVisibility(View.INVISIBLE);
            //getLocation(city, tolatitude, tolongitude);
        }else{
            standardBtn.setVisibility(View.VISIBLE);
            fastBtn.setVisibility(View.VISIBLE);
            buttonCalcDistance.setVisibility(View.VISIBLE);
            getDirection();
        }
    }

    @Override
    public void WeekDelivery(String deliveryC, String Maintotal, String ordersum) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        DeliveryTotal.setText(deliveryC);
        Total.setText(Maintotal);
        if (ordersum.equals("0")){
            proceedPayment.setVisibility(View.INVISIBLE);
        }else{
            proceedPayment.setVisibility(View.VISIBLE);
        }
        if(Objects.equals(city, "Other")){
            standardBtn.setVisibility(View.INVISIBLE);
            fastBtn.setVisibility(View.INVISIBLE);
            buttonCalcDistance.setVisibility(View.INVISIBLE);
           // getLocation(city, tolatitude, tolongitude);
        }else{
            standardBtn.setVisibility(View.VISIBLE);
            fastBtn.setVisibility(View.VISIBLE);
            buttonCalcDistance.setVisibility(View.VISIBLE);
            getDirection();
        }
    }

}
