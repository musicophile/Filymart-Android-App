package com.mart.filymart.CheckAddressContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.CheckAddressContent.model.AddressModel;
import com.mart.filymart.CheckAddressContent.model.IAddress;
import com.mart.filymart.CheckAddressContent.view.ICheckAddressView;
import com.mart.filymart.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckAddressPresenter implements ICheckAddressPresenter {

    ICheckAddressView iCheckAddressView;
    IAddress iAddress;
    Handler handler;
    JSONParser jsonParser = new JSONParser();
    String uid;
    String user_id;

    List<AddressModel> addressModelList;


    public CheckAddressPresenter (ICheckAddressView iCheckAddressView){
        this.iCheckAddressView = iCheckAddressView;
        addressModelList=new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());



    }
    @Override
    public void loadData(String user_id) {
        this.user_id = user_id;
        new CheckAddress().execute();
    }

    class CheckAddress extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("user_id", user_id));

            final String CHECKADDRESS = "http://www.filymart.com/mobileCheckAddress";
            JSONObject json = jsonParser.makeHttpRequest(CHECKADDRESS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                //uid = json.getString("ordersum");

                JSONObject o = json.getJSONObject("Addresss");
                JSONArray array = o.getJSONArray("Addresss");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    addressModelList.add(new AddressModel(
                            product.getString("id"),
                            product.getString("address_name"),
                            product.getString("location")
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
        iCheckAddressView.loadDatas((ArrayList<AddressModel>) addressModelList);
        }

    }
}
