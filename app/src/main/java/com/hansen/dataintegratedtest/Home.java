package com.hansen.dataintegratedtest;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {
    private static final String TAG = "JSON RESPONSE";
    ArrayList<String> repos;
    EditText edInput;
    Button btnGo;
    TextView txtResult;
    FirebaseAnalytics mFirebaseAnalytics;
    Button btnGithub;
    RelativeLayout rv;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        edInput = (EditText) findViewById(R.id.edInput);
        btnGo = (Button) findViewById(R.id.btnGo);
        txtResult = (TextView) findViewById(R.id.txtResult);
        btnGithub = (Button) findViewById(R.id.btnGithub);
        rv = (RelativeLayout) findViewById(R.id.rv);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Firebase analytics init
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        btnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the current dtae
                Calendar cal = Calendar.getInstance();
                //getting the last two weeks date
                cal.add(Calendar.DATE, -14);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                String formatted = format1.format(cal.getTime());
                //using get method to get the response
                getGitRepos("https://api.github.com/search/repositories?q=created>" + formatted + "&sort=stars&order=desc");
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSum(edInput.getText().toString());
            }
        });
    }

    private void getGitRepos(String url) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        //getting the request object
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();

                        //removing all views from the relative layout
                        rv.removeAllViews();
                        repos=new ArrayList<>();
                        if (response != null) {
                            //creating a listview instance
                            ListView repoList = new ListView(Home.this);
                            //adding the listview to the relative layout
                            rv.addView(repoList);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Home.this, android.R.layout.simple_list_item_1,repos );

                            JSONObject jsonObject ;
                            //decoding the response
                            try {
                                jsonObject = new JSONObject(response.toString());

                                JSONArray jsonArray = (JSONArray) jsonObject.get("items");

                                for(int i=0; i<jsonArray .length(); i++){
                                    //adding the names of the repos to the arraylist
                                    repos.add(jsonArray .getJSONObject(i).getString("name"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//setting the adapter of the list
                            repoList.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        //txtResult.setText();
    }

    private void validateAndSum(String text) {
        if (text != null && !text.equals("")) {
            //splitting the numbers by comma and adding to a list
            List<String> numList = Arrays.asList(text.split(","));

            //arraylist to hold the sum of the individual numbers
            ArrayList<Integer> sumList = new ArrayList<>();
            for (int i = 0; i < numList.size(); i++) {

                String number = String.valueOf(numList.get(i));
                for (int k = 0; k < number.length(); k++) {
                    //getting each digit for all the numbers
                    int j = Character.digit(number.charAt(k), 10);

                    //adding the digits to my array list
                    sumList.add(j);

                }

            }
            int s = 0;
            for (int x = 0; x < sumList.size(); x++) {
                //adding the digits
                s = s + sumList.get(x);
                txtResult.setText(getString(R.string.result) + s);

            }
        } else {
            edInput.setError("Field cannot be empty");
        }

    }
}
