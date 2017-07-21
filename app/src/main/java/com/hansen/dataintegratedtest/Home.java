package com.hansen.dataintegratedtest;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        btnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -7);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                String formatted = format1.format(cal.getTime());
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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        rv.removeAllViews();
                        repos=new ArrayList<>();
                        if (response != null) {
                            ListView repoList = new ListView(Home.this);
                            rv.addView(repoList);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Home.this, android.R.layout.simple_list_item_1,repos );

                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response.toString());

                                JSONArray tsmresponse = (JSONArray) jsonObject.get("name");

                                for(int i=0; i<tsmresponse.length(); i++){
                                    repos.add(tsmresponse.getJSONObject(i).getString("name"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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
            List<String> numList = Arrays.asList(text.split(","));
            ArrayList<Integer> sumList = new ArrayList<>();
            for (int i = 0; i < numList.size(); i++) {

                String number = String.valueOf(numList.get(i));
                for (int k = 0; k < number.length(); k++) {
                    int j = Character.digit(number.charAt(k), 10);

                    sumList.add(j);

                }

            }
            int s = 0;
            for (int x = 0; x < sumList.size(); x++) {
                s = s + sumList.get(x);
                txtResult.setText(getString(R.string.result) + s);

            }
        } else {
            edInput.setError("Field cannot be empty");
        }

    }
}
