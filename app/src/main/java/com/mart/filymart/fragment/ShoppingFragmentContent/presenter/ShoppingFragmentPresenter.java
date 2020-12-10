package com.mart.filymart.fragment.ShoppingFragmentContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.JSONParser;
import com.mart.filymart.ProductsAdapter;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.IProduct;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;
import com.mart.filymart.fragment.ShoppingFragmentContent.view.IShoppingFragmentView;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragmentPresenter implements IShoppingFragmentPresenter {

    IShoppingFragmentView iShoppingFragmentView;
    IProduct iProduct;
    Handler handler;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private ProductsAdapter adapter;

    List<ProductModel> productModel;
    public int a;

    public ShoppingFragmentPresenter (IShoppingFragmentView iShoppingFragmentView){
        this.iShoppingFragmentView = iShoppingFragmentView;
        productModel=new ArrayList<>();
        a = 2;
        // initUser();
        handler = new Handler(Looper.getMainLooper());

    }



    @Override
    public void clear() {
        new FruitsVergetables().execute();
    }

    @Override
    public void loadData() {
                new AllProduct().execute();
    }

    @Override
    public void loadAData() {
        new CleaningHygiene().execute();
    }

    @Override
    public void loadBData() {
        new SnacksBiskuitsCakes().execute();
    }

    @Override
    public void loadEData() {
        new EggsMeatFish().execute();
    }

    @Override
    public void loadBeveData() {
        new BeveragesDrinks().execute();
    }

    @Override
    public void loadFoodData() {
        new FoodGrainOil().execute();
    }

    @Override
    public void loadADataKids() {
        new KidsCollection().execute();
    }

    @Override
    public void loadBDataWomens() {
        new WomensCollection().execute();
    }

    @Override
    public void loadEDataMens() {
        new MensCollection().execute();
    }

    @Override
    public void loadBeveDataAllBrands() {
        new AllBrands().execute();
    }

    @Override
    public void loadFoodDataAccessoriesElectronics() {
        new AccessoriesElectronics().execute();
    }


    class AllProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
           productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/mobileAllProducts";
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
                            product.getString("price"),
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
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);

        }

        }

    class FruitsVergetables extends AsyncTask<String, String, String> {

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
            productModel.clear();
            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/fruitsvergetable";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/eggsmeatfish";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);

        }

    }

    class SnacksBiskuitsCakes extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/brandedsnacksfood";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }


    class CleaningHygiene extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/detegentshygiene";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/beveragesdrinks";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }

    class FoodGrainOil extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/foodgrainoil";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }


    class KidsCollection extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/kidscollections";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);

        }

    }

    class MensCollection extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/mencollections";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }


    class WomensCollection extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/womencollections";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }

    class AccessoriesElectronics extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/accessoriesandelectronics";
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
                            product.getString("price"),
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
            a = 1;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }

    class AllBrands extends AsyncTask<String, String, String> {

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
            productModel.clear();

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "https://filymart.com/allbrands";
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
                            product.getString("brandname"),
                            product.getString("brandlogo")
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
            a = 2;
            iShoppingFragmentView.loadDatas((ArrayList<ProductModel>) productModel, a);
        }

    }

    @Override
    public void setProgressBarVisiblity(int visiblity) {
        iShoppingFragmentView.onSetProgressBarVisibility(visiblity);
    }

    @Override
    public boolean setClickable(boolean visiblity) {
        iShoppingFragmentView.setClickable(visiblity);
        return true;
    }
}