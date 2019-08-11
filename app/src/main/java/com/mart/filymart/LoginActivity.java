package com.mart.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private TextView skipBtn;
    private Button btnLinkToRegister;
    private TextView btnForgotPassword;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private LoginProgressDialog pd;
    private Handler h;
    private Runnable r;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        inputEmail =  findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin =  findViewById(R.id.btnLogin);
        btnLinkToRegister =  findViewById(R.id.btnLinkToRegisterScreen);
        skipBtn =  findViewById(R.id.btnSkip);
        btnForgotPassword = findViewById(R.id.btnLinkToForgotPassword);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());
        h = new Handler();
        pd = new LoginProgressDialog(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    ;

                    if (isNetworkAvailable()){
                        pd.show();
                        h.postDelayed(r,5000);
                        new CreateNewProduct().execute();
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Check Your Network Connection", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        if(getIntent().getIntExtra("logr",0)== 1){
           Toast.makeText(this, "Please Check your Email to Verify your Account!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class CreateNewProduct extends AsyncTask<String, String, String> {

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



                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

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
                            JSONObject user = json.getJSONObject("user");
                            String uid = user.getString("id");
                            String name = user.getString("name");
                            String emaill = user.getString("email");
                            String created_at = user
                                    .getString("created_at");

                            // Inserting row in users table
                            db.addUser(name, emaill, uid, created_at);


                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
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

                        if (success == 3) {
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
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            h.removeCallbacks(r);
            if (pd.isShowing() ) {
                pd.dismiss();
            }
        }

    }


}
