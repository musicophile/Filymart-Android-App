package com.mart.filymart.CheckAddressContent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mart.filymart.CheckAddressContent.model.AddressModel;
import com.mart.filymart.JSONParser;
import com.mart.filymart.DeliveryInformationContent.DeliveryInformationActivity;
import com.mart.filymart.R;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private CheckAddressActivity mContext;
    private List<AddressModel> addressList;
    private SessionManager session;
    private SQLiteHandler db;

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;
    String id;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView addressInfo;
        public ImageButton btnDeleteA;
        private Button btnSelectA;



        public MyViewHolder(View view) {
            super(view);
            addressInfo =  view.findViewById(R.id.InfoA);
            btnDeleteA =  view.findViewById(R.id.imDeleteAddress);
            btnSelectA =  view.findViewById(R.id.btnSelectAddress);
                    }
    }


    public AddressAdapter(CheckAddressActivity mContext, List<AddressModel> addressList) {
        this.mContext = mContext;
        this.addressList = addressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_card, parent, false);

        pDialog = new ProgressDialog(mContext);
        pDialog.setCancelable(false);



        db = new SQLiteHandler(mContext);
        session = new SessionManager(mContext);

             return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AddressModel address = addressList.get(position);
        holder.addressInfo.setText(String.format("The name of your address is %s and it's Location is %s", address.getName(), address.getLocation()));

        holder.btnDeleteA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = address.getId();
                new DeleteAddress().execute();
                }
        });

        holder.btnSelectA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(), DeliveryInformationActivity.class);
                intent.putExtra("addressId", address.getId());
                mContext.startActivity(intent);
                mContext.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }


    class DeleteAddress extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Address Deleting..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
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
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("address_id", id));

            final String CHECKADDRESS = "http://www.filymart.com/mobileDeleteAddress";
            JSONObject json = jsonParser.makeHttpRequest(CHECKADDRESS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                //uid = json.getString("ordersum");

                JSONObject o = json.getJSONObject("Addresss");


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
            Intent intent = new Intent(mContext, CheckAddressActivity.class);
            mContext.startActivity(intent);


        }

    }



}