package com.mart.filymart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private Button getSignature;
    ImageView signImage;
    private Bitmap bitmap;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String image_path, base64, encodedImage;
    private ProgressDialog pDialog;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());

        getSignature = findViewById(R.id.getSignature);
        signImage = (ImageView) findViewById(R.id.imageView1);
      //  getSignature.setOnClickListener(new View.OnClickListener() {
          //  @Override
        //    public void onClick(View v) {


             /*   if (isNetworkAvailable()){
                    new Ordersum().execute();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }*/
            // Intent intent = new Intent(getApplicationContext(), PesapalActivity.class);
            // startActivity(intent);
      //      }
    //    });


        getSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http").authority("www.filymart.com").path("/pesapal-iframe.php");
                uriBuilder.appendQueryParameter("amount", "500");

                uriBuilder.appendQueryParameter("description", "First Order");
                uriBuilder.appendQueryParameter("type", "MERCHANT");
                uriBuilder.appendQueryParameter("reference", "001");
                uriBuilder.appendQueryParameter("first_name", "Bert");
                uriBuilder.appendQueryParameter("last_name", "Fily");
                // uriBuilder.appendQueryParameter("cn", "Note to the developer");
                uriBuilder.appendQueryParameter("email", "1");
               // uriBuilder.appendQueryParameter("currency_code", "USD");
                Uri payPalUri = uriBuilder.build();

                // Start your favorite browser
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
                startActivity(viewIntent);
            }
        });

         image_path = getIntent().getStringExtra("imagePath");
         bitmap = BitmapFactory.decodeFile(image_path);
        signImage.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        //byte[] benefits = baos.toByteArray();
        //encodedImage = Base64.encodeToString(benefits, Base64.NO_WRAP | Base64.URL_SAFE |Base64.DEFAULT| Base64.NO_PADDING );
        byte[] data = baos.toByteArray();
        encodedImage = Base64.encodeToString(data, Base64.DEFAULT);
        byte[] dataa = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            dataa = encodedImage.getBytes(StandardCharsets.UTF_8);
        }
        base64 = Base64.encodeToString(dataa, Base64.NO_WRAP | Base64.URL_SAFE |Base64.DEFAULT| Base64.NO_PADDING);



    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void paymentBack(View view) {
        Intent intent = new Intent(getApplicationContext(), SignatureActivity.class);
        startActivity(intent);
    }



    class Ordersum extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PaymentActivity.this);
            pDialog.setMessage("Delivery Methods..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            String stringValueOf = String.valueOf(base64);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("signature", stringValueOf));

            // getting JSON Object
            final String URL_PAYMENT = "http://www.filymart.com/onDeliveryMobilePayment";
            JSONObject json = jsonParser.makeHttpRequest(URL_PAYMENT,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                // successfully created product
                final String errorMsg = json.getString("msg");
                // successfully created product
                runOnUiThread(new Runnable() {
                    public void run() {
                        // String errorMsg = json.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG)
                                .show();
                    }

                });


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }
}
