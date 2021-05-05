package com.mart.filymart.DeliveryInformationContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.DeliveryInformationContent.model.IOrderSummary;
import com.mart.filymart.DeliveryInformationContent.model.OrderSummaryModel;
import com.mart.filymart.DeliveryInformationContent.view.IDeliveryInformationView;
import com.mart.filymart.JSONParser;
import com.mart.filymart.app.AppConfig;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryInformationPresenter implements IDeliveryInformationPresenter {

    IDeliveryInformationView iDeliveryInformationView;
    IOrderSummary iOrderSummary;
    Handler handler;
    JSONParser jsonParser = new JSONParser();
    String uid, orders;
    String user_id, addressId;
    String ordersum, deliveryC, Maintotal, city;
    List<OrderSummaryModel> orderSummaryModelList;
    private double tolongitude;
    private double tolatitude, olat, olng;
    String deliverytime, addressInfomation, distanceInformation;
    String delivery, name, cost;



    public DeliveryInformationPresenter (IDeliveryInformationView iDeliveryInformationView){
        this.iDeliveryInformationView = iDeliveryInformationView;
        orderSummaryModelList = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());



    }
    @Override
    public void loadData(String user_id, String addressId) {
        this.user_id = user_id;
        this.addressId = addressId;
        new Ordersum().execute();
    }

    @Override
    public void loadDataStandard(String user_id, String delivery, String name, String cost, String deliverytime) {
        this.user_id = user_id;
        this.delivery = delivery;
        this.name = name;
        this.cost = cost;
        this.deliverytime = deliverytime;
        new StandardDelivery().execute();

    }

    @Override
    public void loadDataFast(String user_id, String delivery, String name, String cost, String fastTime) {
        this.user_id = user_id;
        this.delivery = delivery;
        this.name = name;
        this.cost = cost;
        this.deliverytime = fastTime;
        new FastDelivery().execute();
    }

    @Override
    public void loadDataWeek(String user_id, String delivery, String name, String cost, String deliverytime) {
        this.user_id = user_id;
        this.delivery = delivery;
        this.name = name;
        this.cost = cost;
        this.deliverytime = deliverytime;
        new WeekDelivery().execute();
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
            params.add(new BasicNameValuePair("address_id", addressId));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_SAVEDADDRESS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                ordersum = json.getString("ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                tolatitude = json.getDouble("AddressLat");
                city = json.getString("cityname");
                olat = json.getDouble("olat");
                olng = json.getDouble("olng");
                tolongitude = json.getDouble("AddressLng");
                addressInfomation = json.getString("AddressName");
                distanceInformation = json.getString("AddressForDelivery");
                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    orderSummaryModelList.add(new OrderSummaryModel(
                            product.getString("id"),
                            product.getString("product_name"),
                            product.getString("total")
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
            iDeliveryInformationView.loadDatas((ArrayList<OrderSummaryModel>) orderSummaryModelList, ordersum, deliveryC, Maintotal, addressInfomation, city, olat, olng, distanceInformation, tolatitude, tolongitude);

        }

    }

    class StandardDelivery extends AsyncTask<String, String, String> {

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

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("delivery", delivery));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("deliverytime", deliverytime));
            params.add(new BasicNameValuePair("cost", cost));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_STANDARD = "http://www.filymart.com/deliverymobilestandard";
            JSONObject json = jsonParser.makeHttpRequest(URL_STANDARD,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                ordersum = json.getString("Ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                JSONObject o = json.getJSONObject("order");




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
            iDeliveryInformationView.StandardDelivery(deliveryC, Maintotal, ordersum);

        }

    }


    class FastDelivery extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("delivery", delivery));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("deliverytime", deliverytime));
            params.add(new BasicNameValuePair("cost", cost));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_FAST = "http://www.filymart.com/deliverymobilestandard";
            JSONObject json = jsonParser.makeHttpRequest(URL_FAST,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                ordersum = json.getString("Ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                JSONObject o = json.getJSONObject("order");


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
            iDeliveryInformationView.FastDelivery(deliveryC, Maintotal, ordersum);

        }

    }

    class WeekDelivery extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("delivery", delivery));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("deliverytime", deliverytime));
            params.add(new BasicNameValuePair("cost", cost));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_FAST = "http://www.filymart.com/deliverymobilestandard";
            JSONObject json = jsonParser.makeHttpRequest(URL_FAST,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                ordersum = json.getString("Ordersum");
                deliveryC = json.getString("delivery");
                Maintotal = json.getString("MainTotal");
                JSONObject o = json.getJSONObject("order");


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
            iDeliveryInformationView.WeekDelivery(deliveryC, Maintotal, ordersum);

        }

    }
}
