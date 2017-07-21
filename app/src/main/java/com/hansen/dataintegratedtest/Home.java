package com.hansen.dataintegratedtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {
    EditText edInput;
    Button btnGo;
    TextView txtResult;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        edInput = (EditText) findViewById(R.id.edInput);
        btnGo = (Button) findViewById(R.id.btnGo);
        txtResult = (TextView) findViewById(R.id.txtResult);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSum(edInput.getText().toString());
            }
        });
    }

    private void validateAndSum(String text) {
        if (text != null && !text.equals("")) {
            List<String> numList = Arrays.asList(text.split(","));
            ArrayList<Integer> sumList = new ArrayList<>();
            for (int i = 0; i < numList.size(); i++) {

                int sum = 0;

                String number = String.valueOf(numList.get(i));
                for (int k = 0; k < number.length(); k++) {
                    int j = Character.digit(number.charAt(k), 10);

                    sumList.add(j);

                }

            }
            int s = 0;
            for (int x = 0; x < sumList.size(); x++) {
                s = s + sumList.get(x);
                txtResult.setText(getString(R.string.result)+s);

            }
        } else {
            edInput.setError("Field cannot be empty");
        }

    }
}
