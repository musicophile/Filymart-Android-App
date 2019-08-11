package com.mart.filymart.ProductDetailsContent.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mart.filymart.JSONParser;
import com.mart.filymart.ProductDetailsContent.model.IProduct;
import com.mart.filymart.ProductDetailsContent.model.ProductModel;
import com.mart.filymart.ProductDetailsContent.view.IProductDetailsView;
import com.mart.filymart.helper.SQLiteHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsPresenterCompl implements IProductDetailsPresenter {

    IProductDetailsView iProductDetailsView;
    IProduct iProduct;
    Handler handler;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private String dd;
    String Id, pname, productPrice, productAbout, productBenefits, productImage, unit, single;


    public ProductDetailsPresenterCompl (IProductDetailsView iProductDetailsView){
        this.iProductDetailsView = iProductDetailsView;
    //    initUser();
        handler = new Handler(Looper.getMainLooper());

    }
    @Override
    public void clear() {


    }

    @Override
    public void doLogin(String dd) {
        this.dd = dd;
        new ProductDetai().execute();
    }

        class ProductDetai extends AsyncTask<String, String, String> {


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
            protected String doInBackground( String... strings) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", dd));

                // getting JSON Object
                // Note that create product url accepts POST method
                final String URL_PRDUCTS = "http://www.filymart.com/mobileProductDetails";
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

                        pname = product.getString("product_name");
                        productPrice = String.format(product.getString("price"));
                        productAbout = product.getString("about") ;
                        productBenefits = product.getString("benefits") ;
                        productImage = product.getString("image");
                        Id = product.getString("id");
                        unit = product.getString("unit");
                        single = product.getString("single");


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
                /// pDialog.dismiss();
                iProduct = new ProductModel(pname, productPrice, productAbout, productBenefits, productImage, unit, single);
                String me = iProduct.getProductName();
                String meme = iProduct.getPrice();
                String image = iProduct.getImage();
                String about = iProduct.getAbout();
                String benefits = iProduct.getBenefit();
                String unit = iProduct.getUnit();
                String single = iProduct.getSingle();
                iProductDetailsView.onClearText(me, meme, image, about, benefits, unit, single);
            }

        }




    @Override
    public void setProgressBarVisiblity(int visiblity) {
        iProductDetailsView.onSetProgressBarVisibility(visiblity);
    }


}
