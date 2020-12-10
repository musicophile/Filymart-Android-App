package com.mart.filymart.EggsMeatFish.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.EggsMeatFish.ProductsAdapterEmf;
import com.mart.filymart.EggsMeatFish.model.IProduct;
import com.mart.filymart.EggsMeatFish.model.ProductModel;
import com.mart.filymart.EggsMeatFish.view.IEggsMeatFishView;
import com.mart.filymart.JSONParser;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EggsMeatFishPresenter implements IEggsMeatFishPresenter {

    IEggsMeatFishView iEggsMeatFishView;
    IProduct iProduct;
    Handler handler;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private ProductsAdapterEmf adapter;

    List<ProductModel> productModel;
    public int a;

    public EggsMeatFishPresenter (IEggsMeatFishView iEggsMeatFishView){
        this.iEggsMeatFishView = iEggsMeatFishView;
        productModel=new ArrayList<>();
        a = 2;
        // initUser();
        handler = new Handler(Looper.getMainLooper());

    }
    @Override
    public void clear() {
        new EggsMeatFish().execute();
    }

    class EggsMeatFish extends AsyncTask<String, String, String> {

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

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.000webhostapp.com/eggsmeatfish";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    productModel.add(new ProductModel(
                            product.getString("id"),
                            product.getString("product_name"),
                            product.getString("category"),
                            product.getInt("price"),
                            product.getString("image")
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
            iEggsMeatFishView.loadDatas((ArrayList<ProductModel>) productModel);
        }

    }
}
