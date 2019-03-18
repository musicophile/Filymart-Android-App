package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.filymart.app.AppConfig;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText femail;
    @BindView(R.id.password)
    EditText fpassword;
    @BindView(R.id.btnReset)
    Button btnReset;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());
    }

    @OnClick(R.id.btnReset)
    public void reset() {
        if (!femail.getText().toString().isEmpty() && !fpassword.getText().toString().isEmpty()) {
            // login user
            new CreateNewProduct().execute();
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {


            String email = femail.getText().toString().trim();
            String password = fpassword.getText().toString().trim();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_LOGIN,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {
                    // successfully created product
                    // user successfully logged in
                    // Create login session
                    //  session.setLogin(true);

                    // Now store the user in SQLite
                 /*   JSONObject user = json.getJSONObject("user");
                    String uid = user.getString("id");
                    String name = user.getString("name");
                    String emaill = user.getString("email");
                    String created_at = user
                            .getString("created_at");

                    // Inserting row in users table
                    db.addUser(name, emaill, uid, created_at);
                   */ //HashMap<String, String> user = db.getUserDetails();

                    //String name = user.get("name");
                    //String email = user.get("email");

                    // Displaying the user details on the screen
                    // txtName.setText(name);
                    // txtEmail.setText(email);

                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                }
                if (success == 2) {
                    final String errorMsg = json.getString("error_msg");
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });

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
            pDialog.dismiss();
        }

    }
}
