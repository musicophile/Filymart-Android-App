package com.mart.filymart.fragment.BusketFragmentContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.JSONParser;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.fragment.BusketFragmentContent.model.IOrder;
import com.mart.filymart.fragment.BusketFragmentContent.model.OrderModel;
import com.mart.filymart.fragment.BusketFragmentContent.view.IBusketFragmentView;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusketFragmentInterpreter implements IBusketFragmentPresenter {

    IBusketFragmentView iBusketFragmentView;
    IOrder iOrder;
    Handler handler;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String uid, orders;
    String user_id;


    List<OrderModel> orderModelList;

    public BusketFragmentInterpreter (IBusketFragmentView iBusketFragmentView){
        this.iBusketFragmentView = iBusketFragmentView;
        orderModelList=new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());



    }


    @Override
    public void loadData(String user_id) {
        this.user_id = user_id;
        new  Ordersum().execute();
    }



    class Ordersum extends AsyncTask<String, String, String> {

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
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_SHOWORDER,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                uid = json.getString("orders");

                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    orderModelList.add(new OrderModel(
                            product.getString("id"),
                            product.getString("product_name"),
                            product.getInt("total"),
                            product.getString("quantity"),
                            product.getString("image")
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
           
            iBusketFragmentView.loadDatas((ArrayList<OrderModel>) orderModelList, uid);
        }

    }
}
