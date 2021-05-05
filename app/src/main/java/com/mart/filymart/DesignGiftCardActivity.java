package com.mart.filymart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.ProductDetailsContent.presenter.IProductDetailsPresenter;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DesignGiftCardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView short_message,long_message;
    private EditText shortMessage,longMessage,inputRecipientEmail,inputPhone,inputReceiverName;
    private TextView prodP, Count, date, shop_name,mainprice,cardPrice;
    private Button btnSend,substact,addition,firstprice,secondprice,thirdprice,fouthprice,fifthprice,sixthprice;
    private ImageButton backPd;
    private ImageView image;
    private IProductDetailsPresenter loginPresenter;
    String dd;
    private TransparentProgressDialogActivity pd;
    private Handler h;
    private Runnable r;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String id;
    DatePickerDialog.OnDateSetListener SelectDate;
    String[] bankNames={"-- Select Event --","Happy Birthday","Merry Christmas", "Happy New Year", "Graduation", "Wedding"};
    String shopname,occasion,shortmessage,recipientemail,Price,recipientphone,recipientname,enter_message,eventdate,imageUrl;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_gift_card);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        backPd = findViewById(R.id.backProductdetailsContent);
        shop_name = findViewById(R.id.shopName);
        shortMessage = findViewById(R.id.short_message_input);
        longMessage = findViewById(R.id.long_message_);
        short_message = findViewById(R.id.short_message);
        long_message = findViewById(R.id.long_message);
        image = findViewById(R.id.image);
        mainprice = findViewById(R.id.mainprice);
        substact = findViewById(R.id.substract);
        addition = findViewById(R.id.addBtn);
        firstprice = findViewById(R.id.price1);
        secondprice = findViewById(R.id.price2);
        thirdprice = findViewById(R.id.price3);
        fouthprice = findViewById(R.id.price4);
        fifthprice = findViewById(R.id.price5);
        sixthprice = findViewById(R.id.price6);
        cardPrice = findViewById(R.id.price);
        inputPhone = findViewById(R.id.recepient_phone);
        inputRecipientEmail = findViewById(R.id.recepient_email);
        inputReceiverName = findViewById(R.id.receiver_name);
        date = findViewById(R.id.date);
        Spinner spin =  findViewById(R.id.listEvents);
        btnSend = findViewById(R.id.submit);
        DatePickerDialog.OnDateSetListener selecteddate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DesignGiftCardActivity.this, selecteddate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        spin.setPrompt("Title");
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Testing", Toast.LENGTH_LONG).show();
                    image.setImageResource(R.drawable.birthday);
                    image.setTag(R.drawable.birthday);
                }
            });
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.birthday));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
        }

        Log.v("testId",getIntent().getStringExtra("id"));
        id = getIntent().getStringExtra("id");
        new getDataInfos().execute();

        backPd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                intent1.putExtra("value",4);
                startActivity(intent1);
                finish();
            }
        });
        shortMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0)
                    short_message.setText(s);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        longMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0)
                    long_message.setText(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mainprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0)
                    cardPrice.setText("TSH "+s);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        firstprice.setText("5000");
        firstprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("5000");
            }
        });
        secondprice.setText("15000");
        secondprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("15000");
            }
        });
        thirdprice.setText("35000");
        thirdprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("35000");
            }
        });
        fouthprice.setText("50000");
        fouthprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("50000");
            }
        });
        fifthprice.setText("75000");
        fifthprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("75000");
            }
        });
        sixthprice.setText("100000");
        sixthprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainprice.setText("100000");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shortmessage = short_message.getText().toString();
                recipientemail = inputRecipientEmail.getText().toString();
                Price = mainprice.getText().toString();
                recipientphone = inputPhone.getText().toString();
                recipientname = inputReceiverName.getText().toString();
                enter_message = long_message.getText().toString();
                eventdate = date.getText().toString();
                imageUrl = image.getTag().toString();
                new sendCardInfos().execute();
                Log.v("occasion", imageUrl);
            }
        });


    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        occasion = adapterView.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    class getDataInfos extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("shop_id", id));
//            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/getGiftCardShopInfos";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                JSONObject o = json.getJSONObject("info");
                Log.v("infos", o+"");
                shopname = o.getString("name");

//                JSONArray array = o.getJSONArray("order");
//
//                for (int i = 0; i < array.length(); i++) {
//
//                    //getting product object from json array
//                    JSONObject product = array.getJSONObject(i);
//
////                    productModel.add(new ProductModel(
////                            product.getString("id"),
////                            product.getString("product_name"),
////                            product.getString("category"),
////                            product.getString("price"),
////                            product.getString("image")
////                    ));
//
//
//
//                }
                // int success = json.getInt("success");
                //  JSONObject user = json.getJSONObject("user");



            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            shop_name.setText(shopname);
            // dismiss the dialog once done
//            a = 1;
//            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);

        }

    }

    class sendCardInfos extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("occasion", occasion));
            params.add(new BasicNameValuePair("shortmessage", shortmessage));
            params.add(new BasicNameValuePair("recipientemail", recipientemail));
            params.add(new BasicNameValuePair("recipientphone", recipientphone));
            params.add(new BasicNameValuePair("recipientname", recipientname));
            params.add(new BasicNameValuePair("enter_message", enter_message));
            params.add(new BasicNameValuePair("eventdate", eventdate));
            params.add(new BasicNameValuePair("cardPrice", Price));
            params.add(new BasicNameValuePair("shopId", id));
            params.add(new BasicNameValuePair("user_id", id));
            params.add(new BasicNameValuePair("imageUrl", id));
//            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/mobileorderedgiftcard";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                int success = json.getInt("success");
                Log.v("saved", success+"");
//                shopname = o.getString("name");
//                JSONArray array = o.getJSONArray("order");
//
//                for (int i = 0; i < array.length(); i++) {
//
//                    //getting product object from json array
//                    JSONObject product = array.getJSONObject(i);
//
////                    productModel.add(new ProductModel(
////                            product.getString("id"),
////                            product.getString("product_name"),
////                            product.getString("category"),
////                            product.getString("price"),
////                            product.getString("image")
////                    ));
//
//
//
//                }
                // int success = json.getInt("success");
                //  JSONObject user = json.getJSONObject("user");



            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("fragment",3);
            startActivity(intent);
            finish();
            // dismiss the dialog once done
//            a = 1;
//            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);

        }

    }

}
