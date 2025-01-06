package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PremiumActivity extends AppCompatActivity implements PaymentResultListener {

    private Button upgradeBUtton;
    private TextView tryPremiumTextView;
    private Checkout checkout;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private String uid;
    private DatabaseReference detailsref;
    private LinearLayout backBUtton;
    private JSONObject jsonObject;
    public String url= "https://us-central1-philoreport.cloudfunctions.net/hello";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        Checkout.preload(getApplicationContext());
        checkout = new Checkout();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        detailsref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        // ...
        checkout.setKeyID("rzp_live_JjA8WqfHWWOuVT");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Payment Gateway");

        tryPremiumTextView = (TextView) findViewById(R.id.try_premium_text_view);
        backBUtton = (LinearLayout) findViewById(R.id.update_premium_back_button);
        backBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tryPremiumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PremiumActivity.this);

                // set title
                builder.setTitle("Request Premium Feature!");
                builder.setMessage("Please contact us via reportplusapplication@gmail.com if you wish to try the premium option for free!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();

            }
        });
        upgradeBUtton = (Button) findViewById(R.id.upgrade_to_premium_button);

        upgradeBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PremiumActivity.this);

                // set title
                builder.setTitle("Region Check!");
                builder.setMessage("Do you live in India?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.show();
                        new GetAPIResponseTask().execute(url);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PremiumActivity.this);

                        // set title
                        builder.setTitle("Contact Us!");
                        builder.setMessage("Contact us via reportplusapplication@gmail.com for making the payment!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                builder.show();

            }
        });


    }
    private class GetAPIResponseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                // Read response from the API
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    return "Error: " + responseCode;
                }
                InputStream inputStream = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                return e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Show response as a Toast message
            String orderid = "";
            try {
                jsonObject = new JSONObject(result);
                orderid = jsonObject.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                try {


                    JSONObject options = new JSONObject();

                    options.put("name", "Report Plus");
                    options.put("description", "Report Plus Premium Package");
                    options.put("image", "https://firebasestorage.googleapis.com/v0/b/philoreport.appspot.com/o/ic_launcher_foreground.png?alt=media&token=6475d1df-4164-4525-85f8-2e13315f0112");
                    options.put("order_id", orderid);//from response of step 3.
                    options.put("theme.color", "#3399cc");


//

                    checkout.open(PremiumActivity.this, options);

                } catch(Exception e) {
                    Log.e(TAG, "Error in starting Razorpay Checkout", e);
                }
                finally {
                    progressDialog.dismiss();
                }
            }

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Completed Successfully, Enjoy Premium Features"+s, Toast.LENGTH_SHORT).show();
        detailsref.child("activationkey").setValue(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed, Try again!", Toast.LENGTH_SHORT).show();

    }

}