package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.example.filymart.activity.HomeActivity;
import com.example.filymart.app.AppConfig;
import com.example.filymart.fragment.BusketsFragment;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private BusketsFragment mContext;
    private List<Orders> albumList;
    JSONParser jsonParser = new JSONParser();
    private SessionManager session;
    private SQLiteHandler db;
    String id;
    String Qntyty;


    private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;
        private Button updateBtn;
        private EditText Qntyy;
        private ImageButton deleteBtn;


        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            count =  view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
            deleteBtn = view.findViewById(R.id.delete);
            updateBtn = view.findViewById(R.id.update);
            Qntyy = view.findViewById(R.id.qnty);






        }
    }


    public OrdersAdapter(BusketsFragment mContext, List<Orders> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent, false);


        pDialog = new ProgressDialog(parent.getContext());
        // SQLite database handler
        db = new SQLiteHandler(parent.getContext());
        pDialog.setCancelable(false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Orders orders = albumList.get(position);
        holder.title.setText(orders.getName());
        holder.count.setText("Tsh." + orders.getNumOfSongs() );
        holder.Qntyy.setText(orders.getQuantity());


        // loading album cover using Glide library
        Glide.with(mContext).load(orders.getThumbnail()).into(holder.thumbnail);

        id = albumList.get(position).getId();

        holder.Qntyy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Qntyty =
                        holder.Qntyy.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



       holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isNetworkAvailable()){
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
                    new UpdateOrder().execute();
                }else{
                    Toast.makeText(mContext.getContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    class UpdateOrder extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(mContext.getContext());
            pDialog.setMessage("Updating Order..");
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
            pDialog.dismiss();

        }

    }

    public class DeleteOrder extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext.getContext());
            pDialog.setMessage("Deleting Order..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
            pDialog.dismiss();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}