
package com.mart.filymart;

        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.mart.filymart.helper.SQLiteHandler;
        import com.mart.filymart.EggsMeatFish.model.ProductModel;

        import org.apache.http.NameValuePair;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

        import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DesignCardFragment extends Fragment {
    private List<String> events;
    private List<String> images;
    String[] bankNames={"-- Select Event --","Happy Birthday","Merry Christmas", "Happy New Year", "Graduation", "Wedding"};
    Spinner spin;
    private ImageView image;
    JSONParser jsonParser = new JSONParser();
    LinearLayout layout;
    SQLiteHandler db;
    ImageView imageView;
    int finalI = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.design_card_fragment, container, false);

         spin =  v.findViewById(R.id.listEvents);
        image = v.findViewById(R.id.image);
        events = new ArrayList<>();
        images = new ArrayList<>();
        spin.setPrompt("Title");
        db = new SQLiteHandler(getActivity());

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

         layout = (LinearLayout) v.findViewById(R.id.linear);



        new CardsInformation().execute();
        return v;
    }

    public static DesignCardFragment newInstance(String text, int image) {

        DesignCardFragment f = new DesignCardFragment();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("img", image);

        f.setArguments(b);

        return f;
    }

    class CardsInformation extends AsyncTask<String, String, String> {

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
            final String URL_PRDUCTS = "https://filymart.com/eventsResults";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


//            Log.d("Create Response", json.toString());

            try {

                JSONObject o = json.getJSONObject("info");
                JSONArray array = o.getJSONArray("title");
                db.deleteEvents();
                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    Log.v("CardsInfo", product.getString("title")+product.getString("image")+product.getString("created_at")+"");
                        events.add(product.getString("event"));
                        images.add(product.getString("image"));

                    db.addEvent(product.getString("event_id"), product.getInt("id"),
                            product.getString("event"), product.getString("title"),
                            product.getString("image"), product.getString("created_at"),
                            product.getString("updated_at"));
//                    productModel.add(new ProductModel(
//                            product.getString("id"),
//                            product.getString("product_name"),
//                            product.getString("category"),
//                            product.getInt("price"),
//                            product.getString("image")
//                    ));


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
            events.clear();
            images.clear();
            events = db.getEvents();
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,events);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(aa);
            Log.v("images2",db.getImages()+"");
            images = db.getImages();
            for (int i = 0; i < images.size(); i++) {
                imageView = new ImageView(getContext());
                imageView.setId(i);

//                if (i > 1){
//                    finalI = 0;
//                }else {
//                    finalI = i;
//                }
                int finalI1 = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Glide.with(getActivity()).load(images.get(finalI1))
                                .transition(withCrossFade())
                                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(image);
                        Toast.makeText(getContext(), "Testing", Toast.LENGTH_LONG).show();
//                    image.setImageResource(R.drawable.birthday);
//                    image.setTag(R.drawable.birthday);
                    }
                });
                imageView.setPadding(2, 2, 2, 2);
//            imageView.setImageBitmap(BitmapFactory.decodeResource(
//                    getResources(), R.drawable.birthday));
                Log.v("loopimages",images.get(i)+"");
                Glide.with(getActivity()).load(images.get(i))
                        .transition(withCrossFade())
                        //.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                layout.addView(imageView);
            }

            // dismiss the dialog once done
//            iEggsMeatFishView.loadDatas((ArrayList<ProductModel>) productModel);
        }

    }

}
