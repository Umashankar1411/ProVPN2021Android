package com.ugt.premiumvpn.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ugt.premiumvpn.AppController;
import com.ugt.premiumvpn.Constant;
import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.SQLiteHandler;
import com.ugt.premiumvpn.database.SessionManager;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends Activity {
    private static final String TAG = UserActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnRegister;
    private Button btnSKIP;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputFullName;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    ImageView imageView;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSKIP = (Button) findViewById(R.id.btnSKIP);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        //Get the time of day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting = null;
        if(hour>= 12 && hour < 17){
            greeting = "Good Afternoon";
            imageView.setImageResource(R.drawable.good_morning_img);
            textView.setText("Afternoon");
        } else if(hour >= 17 && hour < 21){
            greeting = "Good Evening";
            imageView.setImageResource(R.drawable.good_morning_img);
            textView.setText("Evening");
        } else if(hour >= 21 && hour < 24){
            greeting = "Good Night";
            imageView.setImageResource(R.drawable.good_night_img);
            textView.setText("Night");
        } else {
            greeting = "Good Morning";
            imageView.setImageResource(R.drawable.good_morning_img);
            textView.setText("Morning");
        }




        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    checkLogin(email, password);
                } else {

                    FancyToast.makeText(getApplicationContext(),"Please enter the credentials!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();

                }
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    FancyToast.makeText(getApplicationContext(),"Please enter your details!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();

                }
            }
        });

        btnSKIP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                session.setLogin(true);
                Intent intent = new Intent(UserActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void checkLogin(final String email, final String password) {

        String tag_string_req = "req_login";

        final ProgressDialog progressDialog = new ProgressDialog(UserActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Method.POST,
                Constant.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        session.setLogin(true);
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        db.addUser(name, email, uid, created_at);
                        Intent intent = new Intent(UserActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        FancyToast.makeText(getApplicationContext(),
                                errorMsg,FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        //Toast.makeText(getApplicationContext(),
                        //errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FancyToast.makeText(getApplicationContext(),
                            "Json error: " + e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                FancyToast.makeText(getApplicationContext(),
                        error.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                //Toast.makeText(getApplicationContext(),
                //error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public void onBackPressed()
    {

    }












    private void registerUser(final String name, final String email,
                              final String password) {

        String tag_string_req = "req_register";

        final ProgressDialog progressDialog = new ProgressDialog(UserActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Method.POST,
                Constant.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        db.addUser(name, email, uid, created_at);

                        FancyToast.makeText(getApplicationContext(),"User successfully registered. Try login now!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        //Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(
                                UserActivity.this,
                                UserActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        FancyToast.makeText(getApplicationContext(),
                                errorMsg,FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        //Toast.makeText(getApplicationContext(),
                        //errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                FancyToast.makeText(getApplicationContext(),
                        error.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                //Toast.makeText(getApplicationContext(),
                //error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
