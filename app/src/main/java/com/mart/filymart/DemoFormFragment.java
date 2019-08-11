package com.mart.filymart;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin Omyonga on 8/11/2016.
 */
public class DemoFormFragment extends Fragment {

    EditText etxtFName, etxtLName, etxtEmail, etxtPhone, etxtDesc, etxtAmount;
    Button btnProceed;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String name, email, phone, user, total;

    public DemoFormFragment() {}

    public static DemoFormFragment newInstance() {
        return new DemoFormFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getContext());

        etxtFName = (EditText) view.findViewById(R.id.etxtFName);
        etxtLName = (EditText) view.findViewById(R.id.etxtLName);
        etxtEmail = (EditText) view.findViewById(R.id.etxtEmail);
        etxtPhone = (EditText) view.findViewById(R.id.etxtPhone);
        etxtDesc = (EditText) view.findViewById(R.id.etxtDesc);
        etxtAmount = (EditText) view.findViewById(R.id.etxtAmount);
        new GetUserInfo().execute();
        btnProceed = (Button) view.findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty(etxtFName)) {
                    etxtFName.setError(getString(R.string.fname_required));
                } else {
                    etxtFName.setError(null);
                }
                if (isEmpty(etxtLName)) {
                    etxtLName.setError(getString(R.string.lname_required));
                } else {
                    etxtLName.setError(null);
                }
                if (isEmpty(etxtEmail)) {
                    etxtEmail.setError(getString(R.string.email_required));
                } else {
                    etxtEmail.setError(null);
                }
                if (isEmpty(etxtPhone)) {
                    etxtPhone.setError(getString(R.string.phone_required));
                } else {
                    etxtPhone.setError(null);
                }
                if (isEmpty(etxtDesc)) {
                    etxtDesc.setError(getString(R.string.desc_required));
                } else {
                    etxtDesc.setError(null);
                }
                if (isEmpty(etxtAmount)) {
                    etxtAmount.setError(getString(R.string.amount_required));
                } else {
                    etxtAmount.setError(null);
                }
                if (isEmpty(etxtFName) || isEmpty(etxtLName) || isEmpty(etxtEmail) ||
                        isEmpty(etxtPhone) || isEmpty(etxtDesc) || isEmpty(etxtAmount)) {
                    return;
                }

                Intent i = new Intent(getActivity(), PortalActivity.class);
                i.putExtra("fname", etxtFName.getText().toString());
                i.putExtra("lname", etxtLName.getText().toString());
                i.putExtra("email", etxtEmail.getText().toString());
                i.putExtra("phone", etxtPhone.getText().toString());
                i.putExtra("desc", etxtDesc.getText().toString());
                i.putExtra("amount", etxtAmount.getText().toString());
                startActivity(i);
            }
        });

        return view;
    }

    public boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }


    class GetUserInfo extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Creating Product..");
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

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_DATAFORPAYMENT,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    name = product.getString("name");
                    email = product.getString("email");
                    phone = product.getString("phone");

                }

                JSONObject t = json.getJSONObject("ordersum");
                JSONArray array_ = t.getJSONArray("ordersum");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array_.getJSONObject(i);

                     total = product.getString("total");
                     user = product.getString("user_id");

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
            etxtFName.setText(name);
            etxtEmail.setText(email);
            etxtPhone.setText(phone);
            etxtAmount.setText(total);
            etxtDesc.setText(user);
        }

    }
}

