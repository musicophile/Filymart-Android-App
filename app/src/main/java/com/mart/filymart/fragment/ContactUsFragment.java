package com.mart.filymart.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.JSONParser;
import com.mart.filymart.R;
import com.mart.filymart.TransparentProgressDialog;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ContactUsFragment extends Fragment {

    private CardView googleCard, facebookCard, instagramCard, twitterCard;
    private ImageButton google, facebook, instagram, twitter;
    private TextView name, email, message;
    private Button sendtext;
    private SessionManager session;
    private SQLiteHandler db;
    String uid;
    JSONParser jsonParser = new JSONParser();

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    private ProgressDialog pDialog;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        googleCard = view.findViewById(R.id.cardGoogle);
        facebookCard = view.findViewById(R.id.cardFacebook);
        instagramCard = view.findViewById(R.id.cardInsta);
        twitterCard = view.findViewById(R.id.cardTwitter);
        google = view.findViewById(R.id.ImGoogle);
        facebook = view.findViewById(R.id.ImFacebook);
        instagram = view.findViewById(R.id.ImInsta);
        twitter = view.findViewById(R.id.Imtwitter);
        name = view.findViewById(R.id.edName);
        email = view.findViewById(R.id.edEmail);
        message = view.findViewById(R.id.editText);
        sendtext = view.findViewById(R.id.contactUsMessage);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(getContext());

        h = new Handler();
        pd = new TransparentProgressDialog(getContext(), R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        sendtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Messages = message.getText().toString().trim();
                if (!Name.isEmpty() & !Email.isEmpty() & !Messages.isEmpty()){
                    pd.show();
                    h.postDelayed(r,5000);
                new SendingMessage().execute();
                }else{
                    Toast.makeText(getContext(), "Please fill all field's.!", Toast.LENGTH_LONG).show();
                }
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/musicophile__");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/musicophile__")));
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("feusabius1710@gmail.com"));
                    sendIntent.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "feusabius1710@gmail.com" });
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Test");
                    startActivity(sendIntent);
                }catch (Exception e){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "feusabius1710@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/filbert.eusebius.7"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/filbert.eusebius.7"));
                }
                getContext().startActivity(intent);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=@FEusabius"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/@FEusabius"));
                }
                getContext().startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }

    class SendingMessage extends AsyncTask<String, String, String> {

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

            String Name = name.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String Messages = message.getText().toString().trim();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", Name));
            params.add(new BasicNameValuePair("email", Email));
            params.add(new BasicNameValuePair("message", Messages));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/messageMobile";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                uid = json.getString("ordersum");

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
            h.removeCallbacks(r);
            if (pd.isShowing() ) {
                pd.dismiss();
            }
            name.setText("");
            email.setText("");
            message.setText("");
            Toast.makeText(getContext(),
                    "You have Successfully Sent", Toast.LENGTH_LONG)
                    .show();
        }

    }


}
