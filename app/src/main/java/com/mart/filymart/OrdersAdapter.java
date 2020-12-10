package com.mart.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.filymart.PriviewUnpaidCardActivity;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.fragment.BusketFragmentContent.BusketsFragment;
import com.mart.filymart.fragment.BusketFragmentContent.model.OrderModel;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private BusketsFragment mContext;
    private List<OrderModel> albumList;
    JSONParser jsonParser = new JSONParser();
    private SessionManager session;
    private SQLiteHandler db;
    String id;
    String Qntyty;
    String BusketType;

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;
    private ProgressDialog pDialog;
    private SharedPreferences pref;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;
        private Button updateBtn, preview;
        private EditText Qntyy;
        private ImageButton deleteBtn;



        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            count =  view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
            deleteBtn = view.findViewById(R.id.delete);
            updateBtn = view.findViewById(R.id.update);
            preview = view.findViewById(R.id.preview);
            Qntyy = view.findViewById(R.id.qnty);
            if(BusketType.equals("2")){
                Qntyy.setVisibility(View.VISIBLE);
                preview.setVisibility(View.INVISIBLE);
                updateBtn.setVisibility(View.VISIBLE);
            }else{
                Qntyy.setVisibility(View.INVISIBLE);
                preview.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.INVISIBLE);
            }

        }
    }


    public OrdersAdapter(BusketsFragment mContext, List<OrderModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        h = new Handler();

    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent, false);


        pDialog = new ProgressDialog(parent.getContext());
        pref = parent.getContext().getSharedPreferences("filymart", 0);
        BusketType = pref.getString("BusketType", null);
        // SQLite database handler
        db = new SQLiteHandler(parent.getContext());
        pDialog.setCancelable(false);

        pd = new TransparentProgressDialog(parent.getContext(), R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };


        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderModel orders = albumList.get(position);
        holder.title.setText(orders.getName());
        holder.count.setText("Tsh." + orders.getNumOfSongs() );
        holder.Qntyy.setText(orders.getQuantity());


        // loading album cover using Glide library
        Glide.with(mContext).load(orders.getThumbnail()).into(holder.thumbnail);

        holder.Qntyy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Qntyty = holder.Qntyy.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



       holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isNetworkAvailable()){
                    id = orders.getId();
                    new DeleteOrder().execute();
                }else{
                    Toast.makeText(mContext.getContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){


                    id = orders.getId();
                    if (Qntyty == null){
                        Toast.makeText(mContext.getContext(), "Enter Quantity to update", Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(mContext.getContext(),  Qntyty, Toast.LENGTH_LONG).show();
                        new UpdateOrder().execute();
                    }

                }else{
                    Toast.makeText(mContext.getContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getContext(), PriviewUnpaidCardActivity.class);
                mContext.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        mContext.onDestroy();
    }

    class UpdateOrder extends AsyncTask<String, String, String> {

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
            String user_id = users.get("uid");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("product_id", id));
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("quantity", Qntyty));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_UPDATE,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {
                    // successfully created product
                    // successfully created product
                    Intent intent1 = new Intent(mContext.getContext(), HomeActivity.class);
                    intent1.putExtra("fragment",2);
                    mContext.startActivity(intent1);
                    finalize();

                }
                if (success == 2) {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
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

    public class DeleteOrder extends AsyncTask<String, String, String> {

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
        public String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();
            String user_id = users.get("uid");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("product_id", id));
            params.add(new BasicNameValuePair("user_id", user_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_DELETE,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {
                    // successfully created product
                    Intent intent1 = new Intent(mContext.getContext(), HomeActivity.class);
                    intent1.putExtra("fragment",2);
                    mContext.startActivity(intent1);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}