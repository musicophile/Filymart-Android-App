package com.mart.filymart.fragment.SettingsFragmentContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.CheckAddressContent.model.AddressModel;
import com.mart.filymart.CheckAddressContent.model.IAddress;
import com.mart.filymart.JSONParser;
import com.mart.filymart.fragment.SettingsFragmentContent.view.ISettingsFragmentView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragmentPresenter implements ISettingsFragmentPresenter {

    ISettingsFragmentView iSettingsFragmentView;
    IAddress iAddress;
    Handler handler;
    JSONParser jsonParser = new JSONParser();
    String uid;
    String user_id;
    String name, email, phone;
    List<AddressModel> addressModelList;


    public SettingsFragmentPresenter (ISettingsFragmentView iSettingsFragmentView){
        this.iSettingsFragmentView = iSettingsFragmentView;
        addressModelList=new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());



    }
    @Override
    public void loadData(String user_id) {
        this.user_id = user_id;
        new SettingsData().execute();
    }

    @Override
    public void loadDataDelete(String user_id) {
        this.user_id = user_id;
        new DeleteData().execute();
    }

    class SettingsData extends AsyncTask<String, String, String> {

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

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/mobileUserInformation";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                JSONObject oo = json.getJSONObject("users");
                JSONArray Array = oo.getJSONArray("users");

                for (int i = 0; i < Array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = Array.getJSONObject(i);

                    name = product.getString("name");
                    email = product.getString("email");
                    phone = product.getString("phone");
                }


                JSONObject o = json.getJSONObject("address");
                JSONArray array = o.getJSONArray("address");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    addressModelList.add(new AddressModel(
                            product.getString("id"),
                            product.getString("address_name"),
                            product.getString("location")
                    ));


                }
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
            // dismiss the dialog once done
        iSettingsFragmentView.loadDatas((ArrayList<AddressModel>) addressModelList, name, email, phone);

        }

    }

    class DeleteData extends AsyncTask<String, String, String> {

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

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_DELETE = "http://www.filymart.com/mobileDeleteUserInformation";
            JSONObject json = jsonParser.makeHttpRequest(URL_DELETE,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                JSONObject o = json.getJSONObject("msg");

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
            iSettingsFragmentView.loadData();

        }

    }

}
