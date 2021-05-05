package com.mart.filymart.ProductDetailsContent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mart.filymart.JSONParser;
import com.mart.filymart.ProductDetailsContent.presenter.IProductDetailsPresenter;
import com.mart.filymart.ProductDetailsContent.presenter.ProductDetailsPresenterCompl;
import com.mart.filymart.ProductDetailsContent.view.IProductDetailsView;
import com.mart.filymart.R;
import com.mart.filymart.TransparentProgressDialogActivity;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsContents extends AppCompatActivity implements IProductDetailsView {

    private TextView prodN;
    private EditText amount;
    private TextView prodP, Count, prodAbout, prodBene;
    private Button btnSend;
    private ImageButton backPd;
    private ImageView imageView;
    private IProductDetailsPresenter loginPresenter;
    String dd;
    private TransparentProgressDialogActivity pd;
    private Handler h;
    private Runnable r;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_contents);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        prodN = findViewById(R.id.product_name);
        db = new SQLiteHandler(getApplicationContext());
        imageView = findViewById(R.id.image);
        prodP = findViewById(R.id.price);
        prodAbout = findViewById(R.id.about);
        prodBene = findViewById(R.id.benefits);
        amount = findViewById(R.id.amount);
        backPd = findViewById(R.id.backProductdetailsContent);
        Count = findViewById(R.id.count);
        btnSend = findViewById(R.id.addBtn);

        h = new Handler();
        pd = new TransparentProgressDialogActivity(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };


        backPd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
               intent1.putExtra("value",3);
                startActivity(intent1);
                finish();
            }
        });
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qnty = amount.getText().toString().trim();
                if (!qnty.isEmpty()){
                    if (user_id == null){
                        Toast.makeText(getApplicationContext(),
                            "Please Login First!", Toast.LENGTH_LONG)
                            .show();
                    }else{
                    new PostOrder().execute();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Enter Amount or Quantity!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        //init
        Intent in = getIntent();
         id = in.getStringExtra("id");
        loginPresenter = new ProductDetailsPresenterCompl(this);
        pd.show();
        h.postDelayed(r,5000);

        loginPresenter.doLogin(id);


    }


    class PostOrder extends AsyncTask<String, String, String> {

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

            String productName = prodN.getText().toString().trim();
            String priceP = prodP.getText().toString().trim();
            String idd = id;
            String Amount = amount.getText().toString().trim();
            String user_id = users.get("uid");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("product_name", productName));
            params.add(new BasicNameValuePair("price", priceP));
            params.add(new BasicNameValuePair("Id", idd));
            params.add(new BasicNameValuePair("Amount", Amount));
            params.add(new BasicNameValuePair("user_id", user_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_ORDER,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {
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
                    //   finish();
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
            amount.setText("");
        }

    }
public void load(){
    pd.show();
    h.postDelayed(r,5000);
    Intent in = getIntent();
    dd = in.getStringExtra("id");
    loginPresenter.doLogin(dd);
}
    @Override
    public void onClearText(String me, String meme, String image, String about, String benefit, String unit, String single) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
       // super.onDestroy();
        prodN.setText(me);
        prodP.setText(String.format("Tsh."+ meme +"/"+single + unit));
        Glide.with(getApplicationContext())
                .load(image)
                .into(imageView);
        prodAbout.setText(about);
        prodBene.setText(benefit);
    }


    @Override
    public void onLoginResult(Boolean result, int code) {

    }

    @Override
    public void onSetProgressBarVisibility(int visibility) {
    }
}
