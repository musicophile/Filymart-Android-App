package com.mart.filymart.BrandActivity.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.BeveragesDrinksContent.model.ProductModel;
import com.mart.filymart.BrandActivity.ProductsAdapterBrand;
import com.mart.filymart.BrandActivity.model.BrandsModel;
import com.mart.filymart.BrandActivity.model.IBrand;
import com.mart.filymart.BrandActivity.view.IBrandsActivityView;
import com.mart.filymart.JSONParser;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrandsAcivityPresenter implements IBrandsActivityPresenter {

    IBrandsActivityView iBrandsActivityView;
    IBrand iBrand;
    Handler handler;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private ProductsAdapterBrand adapter;

    List<BrandsModel> brandsModels;
    public int a;
    private String id;

    public BrandsAcivityPresenter (IBrandsActivityView iBrandsActivityView){
        this.iBrandsActivityView = iBrandsActivityView;
        brandsModels=new ArrayList<>();
        a = 2;
        // initUser();
        handler = new Handler(Looper.getMainLooper());

    }
    @Override
    public void clear(String id) {
        this.id = id;
        new BeveragesDrinks().execute();
    }

    class BeveragesDrinks extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("id", id));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/allbrandproducts";
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

                    brandsModels.add(new BrandsModel(
                            product.getString("id"),
                            product.getString("product_name"),
                            product.getString("category"),
                            product.getInt("price"),
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
           iBrandsActivityView.loadDatas((ArrayList<BrandsModel>) brandsModels);
        }

    }


}
