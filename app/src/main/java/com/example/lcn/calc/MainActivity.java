package com.example.lcn.calc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    /**
     * initialize special math symbols
     */
    private void initBtnText() {
        Button btnExp = (Button) findViewById(R.id.btn_exp);
        btnExp.setText(Html.fromHtml(getResources().getString(R.string.btn_exp)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtnText();
    }
}
